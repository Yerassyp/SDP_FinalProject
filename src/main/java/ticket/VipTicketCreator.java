package ticket;

public class VipTicketCreator extends TicketCreator {
    @Override
    public Ticket createTicket() {
        return new VipTicket();
    }
}
