package bcu.cmp5332.bookingsystem.gui;


import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
* Represents a BookingHistoryDialog in the flight booking system.
*/

public class BookingHistoryDialog extends JDialog {

    public BookingHistoryDialog(Window owner, Booking booking) {
        super(owner, "Booking History (Timeline)", ModalityType.APPLICATION_MODAL);
        build(booking);
    }
    /**
    * Performs the build operation.
    *
    * @param b the b value
    */


    private void build(Booking b) {
        setMinimumSize(new Dimension(760, 560));
        setPreferredSize(new Dimension(820, 600));
        setLocationRelativeTo(getOwner());
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Header (boxed)
        String bookingType = b.isRoundTrip() ? "Round-trip" : "One-way";
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel header = new JLabel(
                "Booking #" + b.getId() + "  (" + bookingType + ")   |   Status: " + b.getStatus()
                        + "   |   Last Updated: " + b.getLastUpdated()
        );
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerCard.add(header, BorderLayout.CENTER);
        root.add(headerCard, BorderLayout.NORTH);

        // Main content (vertical stack)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ===== Timeline (table) =====
        JPanel timelinePanel = new JPanel(new BorderLayout(8, 8));
        timelinePanel.setBorder(new TitledBorder("Booking Timeline"));

        DefaultTableModel tm = new DefaultTableModel(new Object[]{"#", "Event"}, 0) {
            /**
            * Checks if is cell editable.
            *
            * @param row the row value to use
            * @param column the column value to use
            * @return the boolean result
            */
            @Override

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        int idx = 1;
        for (String event : b.getHistory()) {
            tm.addRow(new Object[]{idx, event});
            idx++;
        }

        JTable timelineTable = new JTable(tm);
        timelineTable.setRowHeight(24);
        timelineTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        timelineTable.getColumnModel().getColumn(0).setMaxWidth(55);
        timelineTable.setFillsViewportHeight(true);

        JScrollPane timelineScroll = new JScrollPane(timelineTable);
        timelineScroll.setPreferredSize(new Dimension(760, 170));
        timelinePanel.add(timelineScroll, BorderLayout.CENTER);

        content.add(timelinePanel);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        // ===== Flight cards (side-by-side) =====
        JPanel flightsRow = new JPanel(new GridLayout(1, 2, 12, 0));

        Flight outbound = b.getOutboundFlight();
        flightsRow.add(createFlightCard("Outbound Flight", outbound));

        Flight ret = (b.isRoundTrip() ? b.getReturnFlight() : null);
        flightsRow.add(createFlightCard("Return Flight", ret));

        content.add(flightsRow);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        // ===== Booking details card =====
        JPanel details = new JPanel(new GridBagLayout());
        details.setBorder(new TitledBorder("Payment & Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 8, 6, 8);

        addKV(details, gbc, "Booked On:", String.valueOf(b.getBookingDate()));
        addKV(details, gbc, "Paid:", String.format("%.2f", b.getPricePaid()));
        addKV(details, gbc, "Fee:", String.format("%.2f", b.getFeeCharged()));

        // If you store paid/fee differently in your model, keep your existing getters.

        content.add(details);

        root.add(content, BorderLayout.CENTER);

        // Buttons
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.add(close);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
    }
    /**
    * Performs the createFlightCard operation.
    *
    * @param title the title value
    * @param f the f value
    * @return the operation result
    */


    private JPanel createFlightCard(String title, Flight f) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(new TitledBorder(title));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 8, 6, 8);

        if (f == null) {
            addKV(card, gbc, "Flight:", "N/A");
            addKV(card, gbc, "Route:", "N/A");
            addKV(card, gbc, "Departure:", "N/A");
            return card;
        }

        addKV(card, gbc, "Flight:", "#" + f.getId() + " (" + f.getFlightNumber() + ")");
        addKV(card, gbc, "Route:", f.getOrigin() + "  →  " + f.getDestination());
        addKV(card, gbc, "Departure:", String.valueOf(f.getDepartureDate()));

        return card;
    }
 /**
    * Performs the addKV operation.
    *
    * @param panel the panel value
    * @param gbc the gbc value
    * @param key the key value
    * @param value the value
    */


    private void addKV(JPanel panel, GridBagConstraints gbc, String key, String value) {
        JLabel k = new JLabel(key);
        k.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.PLAIN, 12));

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(k, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(v, gbc);

        gbc.gridy++;
    }
}