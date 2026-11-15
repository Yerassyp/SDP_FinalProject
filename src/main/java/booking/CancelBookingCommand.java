package booking;

import model.Seat;

import java.util.Objects;

public class CancelBookingCommand implements BookingCommand {
    private final BookingService bookingService;
    private final Seat seat;

    public CancelBookingCommand(BookingService bookingService, Seat seat) {
        this.bookingService = Objects.requireNonNull(bookingService, "Booking service must not be null");
        this.seat = seat;
    }

    @Override
    public boolean execute() {
        return bookingService.cancelBooking(seat);
    }
}
