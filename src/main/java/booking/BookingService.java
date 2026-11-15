package booking;

import model.Seat;
import seats.Screening;
import ticket.Ticket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingService {
    private final Screening screening;
    private final Map<Seat, Ticket> bookings = new HashMap<>();

    public BookingService(Screening screening) {
        this.screening = Objects.requireNonNull(screening, "Screening must not be null");
    }

    public boolean bookTicket(Seat seat, Ticket ticket) {
        if (ticket == null || bookings.containsKey(seat) || !screening.isAvailable(seat)) {
            return false;
        }

        bookings.put(seat, ticket);
        if (!screening.bookSeat(seat)) {
            bookings.remove(seat);
            return false;
        }
        return true;
    }

    public boolean cancelBooking(Seat seat) {
        Ticket ticket = bookings.get(seat);
        if (ticket == null) {
            return false;
        }

        bookings.remove(seat);
        if (!screening.releaseSeat(seat)) {
            bookings.put(seat, ticket);
            return false;
        }
        return true;
    }

    public Ticket getTicket(Seat seat) {
        return bookings.get(seat);
    }
}
