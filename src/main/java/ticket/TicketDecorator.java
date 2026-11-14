package ticket;

import java.util.Objects;

public abstract class TicketDecorator implements Ticket {
    private final Ticket ticket;

    protected TicketDecorator(Ticket ticket) {
        this.ticket = Objects.requireNonNull(ticket, "Ticket must not be null");
    }

    @Override
    public int getPrice() {
        return ticket.getPrice();
    }

    @Override
    public String getDescription() {
        return ticket.getDescription();
    }
}
