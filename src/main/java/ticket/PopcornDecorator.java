package ticket;

public class PopcornDecorator extends TicketDecorator {
    public PopcornDecorator(Ticket ticket) {
        super(ticket);
    }

    @Override
    public int getPrice() {
        return super.getPrice() + 800;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Popcorn";
    }
}
