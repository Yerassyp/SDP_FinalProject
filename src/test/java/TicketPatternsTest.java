import org.junit.jupiter.api.Test;
import ticket.PopcornDecorator;
import ticket.StandardTicket;
import ticket.StandardTicketCreator;
import ticket.Ticket;
import ticket.TicketCreator;
import ticket.VipTicket;
import ticket.VipTicketCreator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TicketPatternsTest {
    @Test
    void standardCreatorCreatesStandardTicket() {
        TicketCreator creator = new StandardTicketCreator();

        Ticket ticket = creator.createTicket();

        assertInstanceOf(StandardTicket.class, ticket);
        assertEquals(2000, ticket.getPrice());
        assertEquals("Standard Ticket", ticket.getDescription());
    }

    @Test
    void vipCreatorCreatesVipTicket() {
        TicketCreator creator = new VipTicketCreator();

        Ticket ticket = creator.createTicket();

        assertInstanceOf(VipTicket.class, ticket);
        assertEquals(4000, ticket.getPrice());
        assertEquals("VIP Ticket", ticket.getDescription());
    }

    @Test
    void popcornAddsPriceAndDescriptionToStandardTicket() {
        Ticket original = new StandardTicketCreator().createTicket();

        Ticket decorated = new PopcornDecorator(original);

        assertEquals(2800, decorated.getPrice());
        assertEquals("Standard Ticket + Popcorn", decorated.getDescription());
        assertEquals(2000, original.getPrice());
        assertEquals("Standard Ticket", original.getDescription());
    }

    @Test
    void popcornAddsPriceAndDescriptionToVipTicket() {
        Ticket original = new VipTicketCreator().createTicket();

        Ticket decorated = new PopcornDecorator(original);

        assertEquals(4800, decorated.getPrice());
        assertEquals("VIP Ticket + Popcorn", decorated.getDescription());
        assertEquals(4000, original.getPrice());
        assertEquals("VIP Ticket", original.getDescription());
    }
}
