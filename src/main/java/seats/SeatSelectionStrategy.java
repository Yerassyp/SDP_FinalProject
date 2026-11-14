package seats;

import model.Seat;

import java.util.List;

public interface SeatSelectionStrategy {
    Seat selectSeat(List<Seat> availableSeats);
}
