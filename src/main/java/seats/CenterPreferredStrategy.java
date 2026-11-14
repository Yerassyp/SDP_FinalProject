package seats;

import model.Seat;

import java.util.List;

public class CenterPreferredStrategy implements SeatSelectionStrategy {
    @Override
    public Seat selectSeat(List<Seat> availableSeats) {
        Seat closestSeat = null;
        double closestDistanceSquared = Double.POSITIVE_INFINITY;

        for (Seat seat : availableSeats) {
            double rowDistance = seat.getRow() - 'B';
            double numberDistance = seat.getNumber() - 2.5;
            double distanceSquared = rowDistance * rowDistance + numberDistance * numberDistance;

            if (distanceSquared < closestDistanceSquared) {
                closestSeat = seat;
                closestDistanceSquared = distanceSquared;
            }
        }

        return closestSeat;
    }
}
