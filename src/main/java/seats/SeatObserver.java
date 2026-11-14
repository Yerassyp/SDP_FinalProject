package seats;

import model.Seat;

public interface SeatObserver {
    void onSeatStatusChanged(Seat seat, boolean available);
}
