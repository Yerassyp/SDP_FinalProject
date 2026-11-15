import ui.CinemaFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CinemaFrame().setVisible(true));
    }
}
