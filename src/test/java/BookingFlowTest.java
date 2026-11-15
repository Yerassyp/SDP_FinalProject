import booking.BookTicketCommand;
import booking.BookingCommand;
import booking.BookingService;
import booking.CancelBookingCommand;
import facade.CinemaFacade;
import model.Seat;
import org.junit.jupiter.api.Test;
import seats.CenterPreferredStrategy;
import seats.FirstAvailableStrategy;
import seats.Screening;
import seats.SeatObserver;
import ticket.StandardTicket;
import ticket.Ticket;
import ticket.VipTicket;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingFlowTest {
    @Test
    void standardTicketBookingStoresTicketAndOccupiesSeat() {
        CinemaFacade facade = new CinemaFacade();
        Ticket ticket = facade.createStandardTicket();
        Seat seat = facade.findAvailableSeat();

        assertInstanceOf(StandardTicket.class, ticket);
        assertNotNull(seat);
        assertTrue(facade.bookTicket(seat, ticket));

        assertFalse(facade.isAvailable(seat));
        Ticket savedTicket = facade.getTicket(seat);
        assertSame(ticket, savedTicket);
        assertEquals(2000, savedTicket.getPrice());
        assertEquals("Standard Ticket", savedTicket.getDescription());
    }

    @Test
    void vipTicketWithPopcornKeepsPriceAndDescriptionAfterBooking() {
        CinemaFacade facade = new CinemaFacade();
        Ticket vip = facade.createVipTicket();
        assertInstanceOf(VipTicket.class, vip);
        Ticket ticket = facade.addPopcorn(vip);
        Seat seat = facade.findAvailableSeat();

        assertEquals(4800, ticket.getPrice());
        assertNotNull(seat);
        assertTrue(facade.bookTicket(seat, ticket));

        Ticket savedTicket = facade.getTicket(seat);
        assertSame(ticket, savedTicket);
        assertEquals(4800, savedTicket.getPrice());
        assertEquals("VIP Ticket + Popcorn", savedTicket.getDescription());
        assertFalse(facade.isAvailable(seat));
    }

    @Test
    void duplicateBookingDoesNotReplaceOriginalTicket() {
        CinemaFacade facade = new CinemaFacade();
        Seat seat = new Seat('B', 2);
        Ticket original = facade.createStandardTicket();
        Ticket replacement = facade.addPopcorn(facade.createVipTicket());

        assertTrue(facade.bookTicket(seat, original));
        assertFalse(facade.bookTicket(new Seat('B', 2), replacement));

        assertSame(original, facade.getTicket(seat));
        assertEquals(2000, facade.getTicket(seat).getPrice());
        assertFalse(facade.isAvailable(seat));
        assertEquals(11, facade.getAvailableSeats().size());
    }

    @Test
    void cancellationFreesSeatAndAllowsBookingAgain() {
        CinemaFacade facade = new CinemaFacade();
        Seat seat = new Seat('B', 2);
        assertTrue(facade.bookTicket(seat, facade.createStandardTicket()));

        assertTrue(facade.cancelBooking(seat));
        assertTrue(facade.isAvailable(seat));
        assertNull(facade.getTicket(seat));
        assertEquals(12, facade.getAvailableSeats().size());

        Ticket newTicket = facade.createVipTicket();
        assertTrue(facade.bookTicket(seat, newTicket));
        assertSame(newTicket, facade.getTicket(seat));
        assertFalse(facade.isAvailable(seat));
        assertEquals(11, facade.getAvailableSeats().size());
    }

    @Test
    void cancellingFreeSeatDoesNotChangeState() {
        CinemaFacade facade = new CinemaFacade();
        Seat seat = new Seat('A', 1);
        List<Seat> availableBefore = facade.getAvailableSeats();

        assertFalse(facade.cancelBooking(seat));

        assertTrue(facade.isAvailable(seat));
        assertNull(facade.getTicket(seat));
        assertEquals(availableBefore, facade.getAvailableSeats());
    }

    @Test
    void strategySwitchingUsesCurrentSeatAvailability() {
        CinemaFacade facade = new CinemaFacade();
        assertEquals(new Seat('A', 1), facade.findAvailableSeat());

        facade.setStrategy(new CenterPreferredStrategy());
        assertEquals(new Seat('B', 2), facade.findAvailableSeat());
        assertEquals(12, facade.getAvailableSeats().size());
        assertTrue(facade.bookTicket(new Seat('B', 2), facade.createStandardTicket()));
        assertEquals(new Seat('B', 3), facade.findAvailableSeat());

        facade.setStrategy(new FirstAvailableStrategy());
        assertEquals(new Seat('A', 1), facade.findAvailableSeat());
        assertTrue(facade.bookTicket(new Seat('A', 1), facade.createStandardTicket()));
        assertEquals(new Seat('A', 2), facade.findAvailableSeat());
        assertEquals(10, facade.getAvailableSeats().size());
    }

    @Test
    void observerSeesConsistentBookingAndCancellationThroughFacade() {
        CinemaFacade facade = new CinemaFacade();
        Seat seat = new Seat('B', 2);
        Ticket ticket = facade.createStandardTicket();
        List<Seat> notifiedSeats = new ArrayList<>();
        List<Boolean> notifiedStatuses = new ArrayList<>();
        SeatObserver observer = (changedSeat, available) -> {
            assertEquals(available, facade.isAvailable(changedSeat));
            if (available) {
                assertNull(facade.getTicket(changedSeat));
            } else {
                assertSame(ticket, facade.getTicket(changedSeat));
            }
            notifiedSeats.add(changedSeat);
            notifiedStatuses.add(available);
        };
        facade.addObserver(observer);

        assertTrue(facade.bookTicket(seat, ticket));
        assertEquals(List.of(seat), notifiedSeats);
        assertEquals(List.of(false), notifiedStatuses);
        assertFalse(facade.bookTicket(seat, ticket));
        assertEquals(List.of(false), notifiedStatuses);

        assertTrue(facade.cancelBooking(seat));
        assertEquals(List.of(seat, seat), notifiedSeats);
        assertEquals(List.of(false, true), notifiedStatuses);
        assertFalse(facade.cancelBooking(seat));
        assertEquals(List.of(false, true), notifiedStatuses);

        facade.removeObserver(observer);
        assertTrue(facade.bookTicket(seat, ticket));
        assertTrue(facade.cancelBooking(seat));
        assertEquals(List.of(seat, seat), notifiedSeats);
        assertEquals(List.of(false, true), notifiedStatuses);
    }

    @Test
    void commandsBookAndCancelUsingSameService() {
        Screening screening = new Screening();
        BookingService service = new BookingService(screening);
        Seat seat = new Seat('B', 2);
        Ticket ticket = new StandardTicket();
        BookingCommand book = new BookTicketCommand(service, seat, ticket);
        BookingCommand cancel = new CancelBookingCommand(service, seat);

        assertTrue(book.execute());
        assertFalse(screening.isAvailable(seat));
        assertSame(ticket, service.getTicket(seat));
        assertFalse(book.execute());

        assertTrue(cancel.execute());
        assertTrue(screening.isAvailable(seat));
        assertNull(service.getTicket(seat));
        assertFalse(cancel.execute());
    }

    @Test
    void soldOutScreeningReturnsNoSuggestionUntilCancellation() {
        CinemaFacade facade = new CinemaFacade();
        for (Seat seat : facade.getSeats()) {
            assertTrue(facade.bookTicket(seat, facade.createStandardTicket()));
        }

        assertTrue(facade.getAvailableSeats().isEmpty());
        assertNull(facade.findAvailableSeat());
        facade.setStrategy(new CenterPreferredStrategy());
        assertNull(facade.findAvailableSeat());

        Seat releasedSeat = new Seat('C', 4);
        assertTrue(facade.cancelBooking(releasedSeat));
        assertTrue(facade.isAvailable(releasedSeat));
        assertNull(facade.getTicket(releasedSeat));
        assertEquals(List.of(releasedSeat), facade.getAvailableSeats());
        assertEquals(releasedSeat, facade.findAvailableSeat());

        facade.setStrategy(new FirstAvailableStrategy());
        assertEquals(releasedSeat, facade.findAvailableSeat());
    }
}
