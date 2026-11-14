package seats;

import model.Seat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Screening {
    private final Map<Seat, Boolean> availability = new LinkedHashMap<>();

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
        return true;
    }

    public boolean releaseSeat(Seat seat) {
        if (!Boolean.FALSE.equals(availability.get(seat))) {
            return false;
        }
        availability.put(seat, true);
        return true;
    }
}
