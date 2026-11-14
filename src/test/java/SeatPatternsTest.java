import model.Seat;
import org.junit.jupiter.api.Test;
import seats.CenterPreferredStrategy;
import seats.FirstAvailableStrategy;
import seats.Screening;
import seats.SeatObserver;
import seats.SeatSelectionStrategy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeatPatternsTest {
    @Test
    void screeningTracksAvailableSeatsAfterBookingAndRelease() {
        Screening screening = new Screening();
        Seat seat = new Seat('B', 2);

        assertEquals(12, screening.getSeats().size());
        assertEquals(screening.getSeats(), screening.getAvailableSeats());
        for (Seat initialSeat : screening.getSeats()) {
            assertTrue(screening.isAvailable(initialSeat));
        }

        assertTrue(screening.bookSeat(seat));
        assertFalse(screening.isAvailable(seat));
        assertEquals(11, screening.getAvailableSeats().size());
        assertFalse(screening.getAvailableSeats().contains(seat));

        assertTrue(screening.releaseSeat(seat));
        assertTrue(screening.isAvailable(seat));
        assertEquals(12, screening.getAvailableSeats().size());
        assertEquals(screening.getSeats(), screening.getAvailableSeats());
    }

    @Test
    void firstAvailableSelectsA1ThenA2AfterBooking() {
        Screening screening = new Screening();
        SeatSelectionStrategy strategy = new FirstAvailableStrategy();
        List<Seat> initialAvailableSeats = screening.getAvailableSeats();

        assertEquals(new Seat('A', 1), strategy.selectSeat(initialAvailableSeats));
        assertEquals(initialAvailableSeats, screening.getAvailableSeats());

        assertTrue(screening.bookSeat(new Seat('A', 1)));
        assertEquals(new Seat('A', 2), strategy.selectSeat(screening.getAvailableSeats()));
        assertEquals(11, screening.getAvailableSeats().size());
        assertTrue(screening.isAvailable(new Seat('A', 2)));
    }

    @Test
    void centerPreferredSelectsB2ThenB3ThenA2AfterBooking() {
        Screening screening = new Screening();
        SeatSelectionStrategy strategy = new CenterPreferredStrategy();
        List<Seat> initialAvailableSeats = screening.getAvailableSeats();

        assertEquals(new Seat('B', 2), strategy.selectSeat(initialAvailableSeats));
        assertEquals(initialAvailableSeats, screening.getAvailableSeats());

        assertTrue(screening.bookSeat(new Seat('B', 2)));
        assertEquals(new Seat('B', 3), strategy.selectSeat(screening.getAvailableSeats()));

        assertTrue(screening.bookSeat(new Seat('B', 3)));
        assertEquals(new Seat('A', 2), strategy.selectSeat(screening.getAvailableSeats()));
        assertEquals(10, screening.getAvailableSeats().size());
        assertTrue(screening.isAvailable(new Seat('A', 2)));
    }

    @Test
    void bothStrategiesReturnNullWhenAllSeatsAreBooked() {
        Screening screening = new Screening();
        SeatSelectionStrategy first = new FirstAvailableStrategy();
        SeatSelectionStrategy center = new CenterPreferredStrategy();

        for (Seat seat : screening.getSeats()) {
            assertTrue(screening.bookSeat(seat));
        }

        assertTrue(screening.getAvailableSeats().isEmpty());
        assertNull(first.selectSeat(screening.getAvailableSeats()));
        assertNull(center.selectSeat(screening.getAvailableSeats()));
    }

    @Test
    void observerReceivesBookingAndReleaseAfterStateChanges() {
        Screening screening = new Screening();
        Seat seat = new Seat('B', 2);
        List<Seat> notifiedSeats = new ArrayList<>();
        List<Boolean> notifiedStatuses = new ArrayList<>();
        SeatObserver observer = (changedSeat, available) -> {
            assertEquals(available, screening.isAvailable(changedSeat));
            notifiedSeats.add(changedSeat);
            notifiedStatuses.add(available);
        };
        screening.addObserver(observer);

        assertTrue(screening.bookSeat(seat));
        assertEquals(List.of(seat), notifiedSeats);
        assertEquals(List.of(false), notifiedStatuses);

        assertTrue(screening.releaseSeat(seat));
        assertEquals(List.of(seat, seat), notifiedSeats);
        assertEquals(List.of(false, true), notifiedStatuses);
        assertTrue(screening.isAvailable(seat));
    }

    @Test
    void failedBookingAndReleaseDoNotNotifyObserver() {
        Screening screening = new Screening();
        Seat bookedSeat = new Seat('B', 2);
        Seat freeSeat = new Seat('A', 1);
        assertTrue(screening.bookSeat(bookedSeat));
        List<Seat> notifiedSeats = new ArrayList<>();
        screening.addObserver((seat, available) -> notifiedSeats.add(seat));

        assertFalse(screening.bookSeat(bookedSeat));
        assertFalse(screening.releaseSeat(freeSeat));

        assertTrue(notifiedSeats.isEmpty());
        assertFalse(screening.isAvailable(bookedSeat));
        assertTrue(screening.isAvailable(freeSeat));
        assertEquals(11, screening.getAvailableSeats().size());
    }

    @Test
    void removedObserverReceivesNoFurtherNotifications() {
        Screening screening = new Screening();
        Seat seat = new Seat('B', 2);
        List<Boolean> notifiedStatuses = new ArrayList<>();
        SeatObserver observer = (changedSeat, available) -> notifiedStatuses.add(available);
        screening.addObserver(observer);
        assertTrue(screening.bookSeat(seat));
        assertEquals(List.of(false), notifiedStatuses);

        screening.removeObserver(observer);
        assertTrue(screening.releaseSeat(seat));

        assertEquals(List.of(false), notifiedStatuses);
        assertTrue(screening.isAvailable(seat));
    }

    @Test
    void addingSameObserverTwiceDoesNotDuplicateNotifications() {
        Screening screening = new Screening();
        Seat seat = new Seat('B', 2);
        List<Boolean> notifiedStatuses = new ArrayList<>();
        SeatObserver observer = (changedSeat, available) -> notifiedStatuses.add(available);
        screening.addObserver(observer);
        screening.addObserver(observer);

        assertTrue(screening.bookSeat(seat));
        assertEquals(List.of(false), notifiedStatuses);

        assertTrue(screening.releaseSeat(seat));
        assertEquals(List.of(false, true), notifiedStatuses);
    }
}
