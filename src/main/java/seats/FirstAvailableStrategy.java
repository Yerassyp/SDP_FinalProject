package seats;

import model.Seat;

import java.util.List;

public class FirstAvailableStrategy implements SeatSelectionStrategy {
    @Override
    public Seat selectSeat(List<Seat> availableSeats) {
        if (availableSeats.isEmpty()) {
            return null;
        }
        return availableSeats.get(0);
    }
}
