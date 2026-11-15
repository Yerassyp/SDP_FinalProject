package ui;

import facade.CinemaFacade;
import model.Seat;
import seats.CenterPreferredStrategy;
import seats.FirstAvailableStrategy;
import seats.SeatObserver;
import ticket.Ticket;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class CinemaFrame extends JFrame implements SeatObserver {
    private final CinemaFacade facade = new CinemaFacade();
    private final Map<Seat, JButton> seatButtons = new LinkedHashMap<>();
    private final JComboBox<String> ticketType = new JComboBox<>(new String[] {
            "Standard Ticket", "VIP Ticket"
    });
    private final JCheckBox popcorn = new JCheckBox("Add Popcorn (+800 KZT)");
    private final JComboBox<String> strategyChoice = new JComboBox<>(new String[] {
            "First Available", "Center Preferred"
    });
    private final JLabel priceLabel = new JLabel();
    private final JLabel selectedSeatLabel = new JLabel("Selected seat: none");
    private final JLabel bookingLabel = new JLabel("Booking: none");
    private final JLabel statusLabel = new JLabel("Select a seat or use Find Seat.");
    private Seat selectedSeat;

    public CinemaFrame() {
        super("Cinema Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createSeatPanel(), BorderLayout.CENTER);
        content.add(createOptionsPanel(), BorderLayout.EAST);
        content.add(createActionsPanel(), BorderLayout.SOUTH);
        setContentPane(content);

        ticketType.addActionListener(event -> updatePrice());
        popcorn.addActionListener(event -> updatePrice());
        strategyChoice.addActionListener(event -> {
            if (strategyChoice.getSelectedIndex() == 0) {
                facade.setStrategy(new FirstAvailableStrategy());
            } else {
                facade.setStrategy(new CenterPreferredStrategy());
            }
        });
        facade.addObserver(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                facade.removeObserver(CinemaFrame.this);
            }
        });

        updatePrice();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 6));
        JLabel title = new JLabel("Cinema Ticket Booking System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        header.add(title);
        header.add(new JLabel("Movie: Cinema Demo Movie"));
        return header;
    }

    private JPanel createSeatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JLabel("Screen", JLabel.CENTER), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(3, 4, 8, 8));
        grid.setPreferredSize(new Dimension(540, 240));
        for (Seat seat : facade.getSeats()) {
            JButton button = new JButton();
            button.setOpaque(true);
            button.addActionListener(event -> selectSeat(seat));
            seatButtons.put(seat, button);
            updateSeatButton(seat);
            grid.add(button);
        }
        panel.add(grid, BorderLayout.CENTER);
        panel.add(new JLabel("Green: available | Red: booked | Blue border: selected"), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOptionsPanel() {
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setPreferredSize(new Dimension(240, 260));
        options.add(new JLabel("Ticket type"));
        options.add(ticketType);
        options.add(Box.createVerticalStrut(8));
        options.add(popcorn);
        options.add(Box.createVerticalStrut(16));
        options.add(new JLabel("Seat selection strategy"));
        options.add(strategyChoice);
        options.add(Box.createVerticalStrut(16));
        options.add(priceLabel);
        options.add(Box.createVerticalStrut(12));
        options.add(selectedSeatLabel);
        options.add(Box.createVerticalGlue());
        return options;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton findButton = new JButton("Find Seat");
        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Booking");
        findButton.addActionListener(event -> findSeat());
        bookButton.addActionListener(event -> bookTicket());
        cancelButton.addActionListener(event -> cancelBooking());
        buttons.add(findButton);
        buttons.add(bookButton);
        buttons.add(cancelButton);
        JPanel messages = new JPanel(new GridLayout(2, 1, 0, 6));
        messages.add(bookingLabel);
        messages.add(statusLabel);
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(messages, BorderLayout.CENTER);
        return panel;
    }

    private Ticket createSelectedTicket() {
        Ticket ticket = ticketType.getSelectedIndex() == 0
                ? facade.createStandardTicket() : facade.createVipTicket();
        return popcorn.isSelected() ? facade.addPopcorn(ticket) : ticket;
    }

    private void updatePrice() {
        priceLabel.setText("Ticket Price: " + createSelectedTicket().getPrice() + " KZT");
    }

    private void selectSeat(Seat seat) {
        Seat previousSeat = selectedSeat;
        selectedSeat = seat;
        if (previousSeat != null) {
            updateSeatButton(previousSeat);
        }
        updateSeatButton(seat);
        updateBookingDetails();
        statusLabel.setText("Seat selected: " + seat.getId());
    }

    private void updateSeatButton(Seat seat) {
        JButton button = seatButtons.get(seat);
        boolean available = facade.isAvailable(seat);
        button.setText(seat.getId() + (available ? " - Available" : " - Booked"));
        button.setBackground(available ? new Color(220, 238, 220) : new Color(242, 215, 215));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(
                seat.equals(selectedSeat) ? new Color(45, 95, 170) : Color.GRAY,
                seat.equals(selectedSeat) ? 3 : 1));
    }

    private void updateBookingDetails() {
        selectedSeatLabel.setText("Selected seat: " + selectedSeat.getId());
        Ticket ticket = facade.getTicket(selectedSeat);
        bookingLabel.setText(ticket == null ? "Booking: none" : "Booking: " + selectedSeat.getId()
                + " | " + ticket.getDescription() + " | " + ticket.getPrice() + " KZT");
    }

    private void findSeat() {
        Seat seat = facade.findAvailableSeat();
        if (seat == null) {
            statusLabel.setText("No available seats.");
            return;
        }
        selectSeat(seat);
    }

    private void bookTicket() {
        if (selectedSeat == null) {
            statusLabel.setText("Select a seat first.");
            return;
        }
        Ticket ticket = createSelectedTicket();
        if (facade.bookTicket(selectedSeat, ticket)) {
            statusLabel.setText("Booking successful: " + selectedSeat.getId()
                    + " | " + ticket.getPrice() + " KZT");
        } else {
            statusLabel.setText("Seat already booked: " + selectedSeat.getId());
        }
    }

    private void cancelBooking() {
        if (selectedSeat == null) {
            statusLabel.setText("Select a seat first.");
            return;
        }
        if (facade.cancelBooking(selectedSeat)) {
            statusLabel.setText("Booking cancelled: " + selectedSeat.getId());
        } else {
            statusLabel.setText("No booking to cancel.");
        }
    }

    @Override
    public void onSeatStatusChanged(Seat seat, boolean available) {
        updateSeatButton(seat);
        if (seat.equals(selectedSeat)) {
            updateBookingDetails();
        }
    }
}
