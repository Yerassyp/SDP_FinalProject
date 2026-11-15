package booking;

import model.Seat;
import ticket.Ticket;

import java.util.Objects;

public class BookTicketCommand implements BookingCommand {
    private final BookingService bookingService;
    private final Seat seat;
    private final Ticket ticket;

    public BookTicketCommand(BookingService bookingService, Seat seat, Ticket ticket) {
        this.bookingService = Objects.requireNonNull(bookingService, "Booking service must not be null");
        this.seat = seat;
        this.ticket = ticket;
    }

    @Override
    public boolean execute() {
        return bookingService.bookTicket(seat, ticket);
    }
}
