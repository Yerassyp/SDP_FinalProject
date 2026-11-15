package facade;

import booking.BookTicketCommand;
import booking.BookingCommand;
import booking.BookingService;
import booking.CancelBookingCommand;
import model.Seat;
import seats.FirstAvailableStrategy;
import seats.Screening;
import seats.SeatObserver;
import seats.SeatSelectionStrategy;
import ticket.PopcornDecorator;
import ticket.StandardTicketCreator;
import ticket.Ticket;
import ticket.TicketCreator;
import ticket.VipTicketCreator;

import java.util.List;
import java.util.Objects;

public class CinemaFacade {
    private final Screening screening;
    private final BookingService bookingService;
    private SeatSelectionStrategy strategy;

    public CinemaFacade() {
        screening = new Screening();
        bookingService = new BookingService(screening);
        strategy = new FirstAvailableStrategy();
    }

    public List<Seat> getSeats() {
        return screening.getSeats();
    }

    public List<Seat> getAvailableSeats() {
        return screening.getAvailableSeats();
    }

    public boolean isAvailable(Seat seat) {
        return screening.isAvailable(seat);
    }

    public void setStrategy(SeatSelectionStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy must not be null");
    }

    public Seat findAvailableSeat() {
        return strategy.selectSeat(screening.getAvailableSeats());
    }

    public Ticket createStandardTicket() {
        TicketCreator creator = new StandardTicketCreator();
        return creator.createTicket();
    }

    public Ticket createVipTicket() {
        TicketCreator creator = new VipTicketCreator();
        return creator.createTicket();
    }

    public Ticket addPopcorn(Ticket ticket) {
        return new PopcornDecorator(ticket);
    }

    public boolean bookTicket(Seat seat, Ticket ticket) {
        BookingCommand command = new BookTicketCommand(bookingService, seat, ticket);
        return command.execute();
    }

    public boolean cancelBooking(Seat seat) {
        BookingCommand command = new CancelBookingCommand(bookingService, seat);
        return command.execute();
    }

    public Ticket getTicket(Seat seat) {
        return bookingService.getTicket(seat);
    }

    public void addObserver(SeatObserver observer) {
        screening.addObserver(observer);
    }

    public void removeObserver(SeatObserver observer) {
        screening.removeObserver(observer);
    }
}
