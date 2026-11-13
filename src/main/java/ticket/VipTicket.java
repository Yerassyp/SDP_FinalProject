package ticket;

public class VipTicket implements Ticket {
    @Override
    public int getPrice() {
        return 4000;
    }

    @Override
    public String getDescription() {
        return "VIP Ticket";
    }
}
