package ticket;

public class StandardTicketCreator extends TicketCreator {
    @Override
    public Ticket createTicket() {
        return new StandardTicket();
    }
}
