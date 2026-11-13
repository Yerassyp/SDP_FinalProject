package ticket;

public class StandardTicket implements Ticket {
    @Override
    public int getPrice() {
        return 2000;
    }

    @Override
    public String getDescription() {
        return "Standard Ticket";
    }
}
