package bcu.cmp5332.bookingsystem.gui;


import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

/**
* Represents a AddFlightWindow in the flight booking system.
*/

public class AddFlightWindow extends JFrame {

    private final FlightBookingSystem fbs;
    private final MainWindow mw;

    private JTextField tfNumber;
    private JTextField tfOrigin;
    private JTextField tfDestination;
    private JTextField tfDate;
    private JTextField tfCapacity;
    private JTextField tfPrice;

    private JLabel lblStatus;

    public AddFlightWindow(FlightBookingSystem fbs, MainWindow mw) {
        this.fbs = fbs;
        this.mw = mw;

        UIUtil.setModernLAF();
        buildUI();
        setVisible(true);
    }
    /**
    * Performs the buildUI operation.
    */


    private void buildUI() {
        setTitle("Add Flight");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Add New Flight");
        title.setFont(UIUtil.h1());
        root.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;

        tfNumber = new JTextField();
        tfOrigin = new JTextField();
        tfDestination = new JTextField();
        tfDate = new JTextField(LocalDate.now().plusDays(7).toString()); // default
        tfCapacity = new JTextField("100");
        tfPrice = new JTextField("100");

        addRow(card, gc, "Flight Number", tfNumber);
        addRow(card, gc, "Origin", tfOrigin);
        addRow(card, gc, "Destination", tfDestination);
        addRow(card, gc, "Departure Date (YYYY-MM-DD)", tfDate);
        addRow(card, gc, "Capacity", tfCapacity);
        addRow(card, gc, "Base Price", tfPrice);

        gc.gridx = 0;
        gc.gridwidth = 2;
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(180, 40, 40));
        card.add(lblStatus, gc);

        root.add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Save Flight");
        buttons.add(btnCancel);
        buttons.add(btnSave);

        root.add(buttons, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        setContentPane(root);
    }
 /**
    * Performs the addRow operation.
    *
    * @param card the card value
    * @param gc the gc value
    * @param label the label value
    * @param field the field value
    */


    private void addRow(JPanel card, GridBagConstraints gc, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(UIUtil.base());

        gc.gridx = 0;
        gc.gridwidth = 1;
        card.add(l, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        card.add(field, gc);

        gc.gridy++;
        gc.weightx = 0;
    }
    /**
    * Performs the onSave operation.
    */


    private void onSave() {
        String number = tfNumber.getText().trim();
        String origin = tfOrigin.getText().trim();
        String dest = tfDestination.getText().trim();
        String dateStr = tfDate.getText().trim();
        String capStr = tfCapacity.getText().trim();
        String priceStr = tfPrice.getText().trim();

        if (number.isEmpty() || origin.isEmpty() || dest.isEmpty() || dateStr.isEmpty()
                || capStr.isEmpty() || priceStr.isEmpty()) {
            lblStatus.setText("All fields are required.");
            return;
        }

        LocalDate date;
        int capacity;
        double price;

        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception ex) {
            lblStatus.setText("Invalid date. Use YYYY-MM-DD.");
            return;
        }

        try {
            capacity = Integer.parseInt(capStr);
            if (capacity <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            lblStatus.setText("Capacity must be a positive integer.");
            return;
        }

        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            lblStatus.setText("Price must be a positive number.");
            return;
        }

        try {
            int id = fbs.nextFlightId();
            Flight f = new Flight(id, number, origin, dest, date, capacity, price);
            fbs.addFlight(f);

            FlightBookingSystemData.storeAtomic(fbs);

            // refresh flights table in main window
            if (mw != null) mw.displayFlights();

            UIUtil.msg(this, "Flight #" + id + " added successfully!");
            dispose();

        } catch (FlightBookingSystemException ex) {
            lblStatus.setText(ex.getMessage());
        } catch (Exception ex) {
            lblStatus.setText("Save failed: " + ex.getMessage());
        }
    }
}
