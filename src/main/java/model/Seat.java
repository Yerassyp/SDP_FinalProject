package model;

public final class Seat {
    private final char row;
    private final int number;

    public Seat(char row, int number) {
        if (row < 'A' || row > 'C') {
            throw new IllegalArgumentException("Row must be A, B or C");
        }
        if (number < 1 || number > 4) {
            throw new IllegalArgumentException("Seat number must be between 1 and 4");
        }
        this.row = row;
        this.number = number;
    }

    public String getId() {
        return String.valueOf(row) + number;
    }

    public char getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Seat)) {
            return false;
        }
        Seat seat = (Seat) object;
        return row == seat.row && number == seat.number;
    }

    @Override
    public int hashCode() {
        return 31 * row + number;
    }

    @Override
    public String toString() {
        return getId();
    }
}
