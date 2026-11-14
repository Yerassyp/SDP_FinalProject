package seats;

import model.Seat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Screening {
    private final Map<Seat, Boolean> availability = new LinkedHashMap<>();
    private final List<SeatObserver> observers = new ArrayList<>();

    public Screening() {
        for (char row = 'A'; row <= 'C'; row++) {
            for (int number = 1; number <= 4; number++) {
                availability.put(new Seat(row, number), true);
            }
        }
    }

    public List<Seat> getSeats() {
        return List.copyOf(availability.keySet());
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : availability.keySet()) {
            if (isAvailable(seat)) {
                availableSeats.add(seat);
            }
        }
        return List.copyOf(availableSeats);
    }

    public boolean isAvailable(Seat seat) {
        return Boolean.TRUE.equals(availability.get(seat));
    }

    public boolean bookSeat(Seat seat) {
        if (!isAvailable(seat)) {
            return false;
        }
        availability.put(seat, false);
        notifyObservers(seat, false);
        return true;
    }

    public boolean releaseSeat(Seat seat) {
        if (!Boolean.FALSE.equals(availability.get(seat))) {
            return false;
        }
        availability.put(seat, true);
        notifyObservers(seat, true);
        return true;
    }

    public void addObserver(SeatObserver observer) {
        Objects.requireNonNull(observer, "Observer must not be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(SeatObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Seat seat, boolean available) {
        for (SeatObserver observer : new ArrayList<>(observers)) {
            observer.onSeatStatusChanged(seat, available);
        }
    }
}
