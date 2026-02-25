package bcu.cmp5332.bookingsystem.gui;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.Role;
import bcu.cmp5332.bookingsystem.auth.UserStore;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
* Represents a MainWindow in the flight booking system.
*/

public class MainWindow extends JFrame {

    private final FlightBookingSystem fbs;
    private final AppUser user;

    // Tabs
    private JTabbedPane tabs;

    // Flights tab
    private JTable flightsTable;

    // Admin Customers tab OR User My Dashboard tab
    private JTable customersTable;      // used only for admin customers list
    private JTable myBookingsTable;     // used only for user dashboard bookings list

    // Filters
    private JTextField tfOriginFilter;
    private JTextField tfDestFilter;
    private JTextField tfDateFilter; // YYYY-MM-DD

    public MainWindow(FlightBookingSystem fbs, AppUser user) {
        this.fbs = fbs;
        this.user = user;

        UIUtil.setModernLAF();
        buildUI();
        refreshAll();

        setVisible(true);
    }

    /** Authentication service used by the GUI for login and registration. */

    private final AuthService auth = new AuthService(new UserStore("resources/data/users.txt"));
    /**
    * Checks if is admin.
    * @return the boolean result
    */

    private boolean isAdmin() {
        return user.getRole() == Role.ADMIN;
    }
    /**
    * Performs the myCustomerId operation.
    * @return the operation result
    */


    private int myCustomerId() {
        return user.getCustomerId();
    }
    /**
    * Performs the myCustomerOrNull operation.
    * @return the operation result
    */


    private Customer myCustomerOrNull() {
        try {
            return fbs.getCustomerByID(myCustomerId());
        } catch (Exception e) {
            return null;
        }
    }
    /**
    * Performs the buildUI operation.
    */


    private void buildUI() {
        // keep system date synced to local machine date (as you asked)
        fbs.syncSystemDateToNow();

        setTitle("Flight Booking System | " + user.getRole() + " | Date: " + fbs.getSystemDate());
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            /**
            * Performs window closing operation.
            *
            * @param e the e value to use
            */
            @Override

            /**
             * Window closing.
             *
             * @param e Value.
             */
            public void windowClosing(java.awt.event.WindowEvent e) {
                try { FlightBookingSystemData.storeAtomic(fbs); } catch (Exception ignored) {}
            }
        });

        tabs = new JTabbedPane();
        tabs.addTab("Flights", buildFlightsTab());

        if (isAdmin()) {
            tabs.addTab("Customers", buildCustomersTab_Admin());
        } else {
            tabs.addTab("My Dashboard", buildMyDashboardTab_User());
        }

        setJMenuBar(buildMenuBar());
        setContentPane(tabs);
    }

    // ---------------------------------------------------------------------
    // MENU BAR
    // ---------------------------------------------------------------------
    /**
    * Performs build menu bar operation.
    * @return the jmenubar result
    */

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu sys = new JMenu("System");
        JMenuItem miSave = new JMenuItem("Save Now");
        JMenuItem miRefresh = new JMenuItem("Refresh");
        JMenuItem miLogout = new JMenuItem("Logout");
        sys.add(miSave);
        sys.add(miRefresh);
        sys.addSeparator();
        sys.add(miLogout);

        JMenu booking = new JMenu("Booking");
        JMenuItem miIssue = new JMenuItem("Issue Booking");
        JMenuItem miRebook = new JMenuItem("Rebook Booking");
        JMenuItem miCancel = new JMenuItem("Cancel Booking");
        JMenuItem miHistory = new JMenuItem("View Booking History");
        booking.add(miIssue);
        booking.add(miRebook);
        booking.add(miCancel);
        booking.addSeparator();
        booking.add(miHistory);

        JMenu admin = new JMenu("Admin");
        JMenuItem miAddFlight = new JMenuItem("Add Flight");
        JMenuItem miAddCustomer = new JMenuItem("Add Customer");
        JMenuItem miDeleteFlight = new JMenuItem("Delete Flight");
        JMenuItem miDeleteCustomer = new JMenuItem("Delete Customer");

        admin.add(miAddFlight);
        admin.add(miDeleteFlight);
        admin.addSeparator();
        admin.add(miAddCustomer);
        admin.add(miDeleteCustomer);

        bar.add(sys);
        bar.add(booking);
        if (isAdmin()) bar.add(admin);

        // actions
        miSave.addActionListener(e -> saveNow(false));
        miRefresh.addActionListener(e -> refreshAll());
        miLogout.addActionListener(e -> logout());

        miIssue.addActionListener(e -> issueBookingDialog(selectedFlightId()));
        miRebook.addActionListener(e -> rebookDialog());
        miCancel.addActionListener(e -> cancelBookingDialog());
        miHistory.addActionListener(e -> viewBookingHistoryDialog());

        miAddFlight.addActionListener(e -> {
            if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }
            new AddFlightWindow(fbs, this);
        });

        miDeleteFlight.addActionListener(e -> {
            if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }
            deleteSelectedFlight();
        });

        miAddCustomer.addActionListener(e -> {
            if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }
            addCustomerDialog();
        });

        miDeleteCustomer.addActionListener(e -> {
            if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }
            deleteSelectedCustomer();
        });

        return bar;
    }

    // ---------------------------------------------------------------------
    // FLIGHTS TAB (with filter panel)
    // ---------------------------------------------------------------------
    /**
    * Performs build flights tab operation.
    * @return the jpanel result
    */

    private JPanel buildFlightsTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel filters = new JPanel(new GridBagLayout());
        filters.setBorder(BorderFactory.createTitledBorder("Filter Flights"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridy = 0;

        tfOriginFilter = new JTextField(10);
        tfDestFilter = new JTextField(10);
        tfDateFilter = new JTextField(10); // YYYY-MM-DD

        JButton btnApply = new JButton("Apply Filter");
        JButton btnClear = new JButton("Clear");

        int x = 0;
        gc.gridx = x++; filters.add(new JLabel("Origin:"), gc);
        gc.gridx = x++; filters.add(tfOriginFilter, gc);

        gc.gridx = x++; filters.add(new JLabel("Destination:"), gc);
        gc.gridx = x++; filters.add(tfDestFilter, gc);

        gc.gridx = x++; filters.add(new JLabel("Departure Date (YYYY-MM-DD):"), gc);
        gc.gridx = x++; filters.add(tfDateFilter, gc);

        gc.gridx = x++; filters.add(btnApply, gc);
        gc.gridx = x++; filters.add(btnClear, gc);

        root.add(filters, BorderLayout.NORTH);

        flightsTable = new JTable();
        root.add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        // Bottom buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnPassengers = new JButton("Passengers");
        JButton btnBook = new JButton(isAdmin() ? "Issue Booking" : "Book Selected Flight");
        actions.add(btnPassengers);
        actions.add(btnBook);
        root.add(actions, BorderLayout.SOUTH);

        btnApply.addActionListener(e -> refreshFlights());
        btnClear.addActionListener(e -> {
            tfOriginFilter.setText("");
            tfDestFilter.setText("");
            tfDateFilter.setText("");
            refreshFlights();
        });

        btnPassengers.addActionListener(e -> showPassengersPopup());

        btnBook.addActionListener(e -> {
    Integer flightId = selectedFlightId();
    if (flightId == null) {
        UIUtil.msg(this, "Select a flight first.");
        return;
    }
    // Admin & User: open the same booking dialog (users can also choose return flight)
    issueBookingDialog(flightId);
});

        return root;
    }

    // ---------------------------------------------------------------------
    // ADMIN CUSTOMERS TAB
    // ---------------------------------------------------------------------
    /**
    * Performs build customers tab_ admin operation.
    * @return the jpanel result
    */

    private JPanel buildCustomersTab_Admin() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        customersTable = new JTable();
        root.add(new JScrollPane(customersTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnBookingDetails = new JButton("Booking Details");
        JButton btnAddCustomer = new JButton("Add Customer");
        JButton btnDeleteCustomer = new JButton("Delete Customer");

        buttons.add(btnBookingDetails);
        buttons.add(btnDeleteCustomer);
        buttons.add(btnAddCustomer);
        root.add(buttons, BorderLayout.SOUTH);

        btnBookingDetails.addActionListener(e -> showCustomerBookingsPopup_AdminOrSelf());
        btnAddCustomer.addActionListener(e -> addCustomerDialog());
        btnDeleteCustomer.addActionListener(e -> deleteSelectedCustomer());

        return root;
    }

    // ---------------------------------------------------------------------
    // USER DASHBOARD TAB (My profile + my bookings table)
    // ---------------------------------------------------------------------
    /**
    * Performs build my dashboard tab_ user operation.
    * @return the jpanel result
    */

    private JPanel buildMyDashboardTab_User() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(BorderFactory.createTitledBorder("My Profile"));

        JTextArea profile = new JTextArea();
        profile.setEditable(false);
        profile.setLineWrap(true);
        profile.setWrapStyleWord(true);
        profile.setBackground(new Color(248, 248, 248));
        profile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(profile, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        myBookingsTable = new JTable();
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createTitledBorder("My Bookings"));
        center.add(new JScrollPane(myBookingsTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnDetails = new JButton("Details");
        JButton btnRebook = new JButton("Rebook");
        JButton btnCancel = new JButton("Cancel");
        JButton btnHistory = new JButton("History");
        btns.add(btnHistory);
        btns.add(btnDetails);
        btns.add(btnRebook);
        btns.add(btnCancel);
        center.add(btns, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);

        // actions
        btnDetails.addActionListener(e -> showCustomerBookingsPopup_AdminOrSelf());
        btnRebook.addActionListener(e -> rebookDialog());
        btnCancel.addActionListener(e -> cancelBookingDialog());
        btnHistory.addActionListener(e -> viewBookingHistoryDialog());

        // store profile component reference in client property so refresh can update it
        root.putClientProperty("profileArea", profile);
        return root;
    }

    // ---------------------------------------------------------------------
    // Refresh
    // ---------------------------------------------------------------------
    /**
    * Performs refresh all operation.
    */

    private void refreshAll() {
        // Always sync to local time when the window refreshes
        fbs.syncSystemDateToNow();

        refreshFlights();
        if (isAdmin()) refreshCustomers_Admin();
        else refreshMyDashboard_User();

        String extra = isAdmin() ? "" : (" | YourCustomerID: " + myCustomerId());
        setTitle("Flight Booking System | " + user.getRole() + " | Date: " + fbs.getSystemDate() + extra);
    }
    /**
    * Performs the displayCustomers operation.
    */

    public void displayFlights() { refreshFlights(); }
    /**
    * Performs display customers operation.
    */

    public void displayCustomers() {
        if (isAdmin()) refreshCustomers_Admin();
        else refreshMyDashboard_User();
    }
    /**
    * Performs the refreshFlights operation.
    */


    private void refreshFlights() {
        fbs.refreshCompletedBookings();
        List<Flight> flights = fbs.getActiveFutureFlights();

        // apply filters
        String originF = tfOriginFilter == null ? "" : tfOriginFilter.getText().trim().toLowerCase();
        String destF = tfDestFilter == null ? "" : tfDestFilter.getText().trim().toLowerCase();
        String dateF = tfDateFilter == null ? "" : tfDateFilter.getText().trim();

        LocalDate filterDate = null;
        if (!dateF.isEmpty()) {
            try { filterDate = LocalDate.parse(dateF); }
            catch (Exception ex) { UIUtil.msg(this, "Invalid date filter. Use YYYY-MM-DD."); }
        }

        List<Flight> filtered = new ArrayList<>();
        for (Flight f : flights) {
            if (!originF.isEmpty() && !f.getOrigin().toLowerCase().contains(originF)) continue;
            if (!destF.isEmpty() && !f.getDestination().toLowerCase().contains(destF)) continue;
            if (filterDate != null && !f.getDepartureDate().isEqual(filterDate)) continue;
            filtered.add(f);
        }

        String[] cols = {"ID", "Flight No", "Origin", "Destination", "Departure", "Seats Left", "Capacity", "Price Today"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            /**
            * Checks if is cell editable.
            *
            * @param row the row value to use
            * @param col the col value to use
            * @return the boolean result
            */
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Flight f : filtered) {
            m.addRow(new Object[]{
                    f.getId(),
                    f.getFlightNumber(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureDate(),
                    f.getSeatsLeft(),
                    f.getCapacity(),
                    String.format("%.2f", fbs.calculateCurrentPrice(f))
            });
        }

        flightsTable.setModel(m);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    /**
    * Performs the refreshCustomers_Admin operation.
    */


    private void refreshCustomers_Admin() {
        List<Customer> customers = fbs.getActiveCustomers();

        String[] cols = {"ID", "Name", "Phone", "Email", "Bookings"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            /**
            * Checks if is cell editable.
            *
            * @param row the row value to use
            * @param col the col value to use
            * @return the boolean result
            */
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Customer c : customers) {
            m.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getBookings().size()
            });
        }

        customersTable.setModel(m);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    /**
    * Performs the refreshMyDashboard_User operation.
    */


    private void refreshMyDashboard_User() {
        // update profile text area
        JPanel dash = (JPanel) tabs.getComponentAt(1); // My Dashboard is second tab
        JTextArea profile = (JTextArea) dash.getClientProperty("profileArea");

        Customer mine = myCustomerOrNull();
        if (mine == null) {
            if (profile != null) profile.setText("Your user account is not linked to a customer record.\n(Ask admin to fix.)");
            refreshMyBookingsTable(new ArrayList<>());
            return;
        }

        if (profile != null) {
            profile.setText(
                    "Customer ID: " + mine.getId() + "\n" +
                            "Name: " + mine.getName() + "\n" +
                            "Phone: " + mine.getPhone() + "\n" +
                            "Email: " + mine.getEmail() + "\n" +
                            "Bookings: " + mine.getBookings().size()
            );
        }

        refreshMyBookingsTable(mine.getBookings());
    }
    /**
    * Performs the refreshMyBookingsTable operation.
    *
    * @param bookings the bookings value
    */


    private void refreshMyBookingsTable(List<Booking> bookings) {
        // User dashboard table: show full outbound + return details (flight no, route, dates)
        String[] cols = {
                "BookingID", "Type", "Status",
                "Outbound Flight", "Outbound Route", "Outbound Date",
                "Return Flight", "Return Route", "Return Date",
                "Booked", "LastUpdated", "Paid", "Fee"
        };

        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            /**
            * Checks if is cell editable.
            *
            * @param row the row value to use
            * @param col the col value to use
            * @return the boolean result
            */
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Booking b : bookings) {
            Flight outbound = b.getOutboundFlight();

            String type = b.isRoundTrip() ? "Round-trip" : "One-way";

            String outFlight = "#" + outbound.getId() + " " + outbound.getFlightNumber();
            String outRoute = outbound.getOrigin() + " → " + outbound.getDestination();
            String outDate = String.valueOf(outbound.getDepartureDate());

            String retFlight = "-";
            String retRoute = "-";
            String retDate = "-";
            if (b.isRoundTrip() && b.getReturnFlight() != null) {
                Flight ret = b.getReturnFlight();
                retFlight = "#" + ret.getId() + " " + ret.getFlightNumber();
                retRoute = ret.getOrigin() + " → " + ret.getDestination();
                retDate = String.valueOf(ret.getDepartureDate());
            }

            m.addRow(new Object[]{
                    b.getId(),
                    type,
                    b.getStatus(),
                    outFlight,
                    outRoute,
                    outDate,
                    retFlight,
                    retRoute,
                    retDate,
                    b.getBookingDate(),
                    b.getLastUpdated(),
                    String.format("%.2f", b.getPricePaid()),
                    String.format("%.2f", b.getFeeCharged())
            });
        }

        myBookingsTable.setModel(m);
        myBookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------
    /**
    * Performs selected flight id operation.
    * @return the integer result
    */

    private Integer selectedFlightId() {
        int row = flightsTable.getSelectedRow();
        if (row < 0) return null;
        return (Integer) flightsTable.getValueAt(row, 0);
    }
    /**
    * Performs the selectedCustomerId_Admin operation.
    * @return the operation result
    */


    private Integer selectedCustomerId_Admin() {
        if (!isAdmin()) return null;
        int row = customersTable.getSelectedRow();
        if (row < 0) return null;
        return (Integer) customersTable.getValueAt(row, 0);
    }
    /**
    * Performs the selectedMyBookingId_User operation.
    * @return the operation result
    */


    private Integer selectedMyBookingId_User() {
        if (isAdmin() || myBookingsTable == null) return null;
        int row = myBookingsTable.getSelectedRow();
        if (row < 0) return null;
        return (Integer) myBookingsTable.getValueAt(row, 0);
    }

    // ---------------------------------------------------------------------
    // Logout / Save
    // ---------------------------------------------------------------------
    /**
    * Performs the logout operation.
    */

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        saveNow(true);
        dispose();
        SwingUtilities.invokeLater(() -> new LoginWindow(fbs));
    }
    /**
    * Performs the saveNow operation.
    *
    * @param quiet the quiet value
    */


    private void saveNow(boolean quiet) {
        try {
            FlightBookingSystemData.storeAtomic(fbs);
            if (!quiet) UIUtil.msg(this, "Saved successfully.");
        } catch (Exception ex) {
            UIUtil.msg(this, "Save failed: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------------------
    // Flights actions
    // ---------------------------------------------------------------------
    /**
    * Performs show passengers popup operation.
    */

    private void showPassengersPopup() {
        Integer id = selectedFlightId();
        if (id == null) {
            UIUtil.msg(this, "Select a flight first.");
            return;
        }

        try {
            Flight f = fbs.getFlightByID(id);

            // ---------- Header ----------
            JLabel title = new JLabel("Flight #" + f.getId() + "  " + f.getFlightNumber());
            title.setFont(new Font("SansSerif", Font.BOLD, 18));

            JLabel route = new JLabel(f.getOrigin() + "  \u2192  " + f.getDestination() + "   |   " + f.getDepartureDate());
            route.setFont(new Font("SansSerif", Font.PLAIN, 13));
            route.setForeground(new Color(90, 90, 90));

            JPanel header = new JPanel();
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
            header.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 16));
            header.setBackground(Color.WHITE);
            header.add(title);
            header.add(Box.createVerticalStrut(4));
            header.add(route);

            // ---------- Stats (2-column grid) ----------
            Font keyFont = new Font("SansSerif", Font.BOLD, 13);
            Font valFont = new Font("SansSerif", Font.PLAIN, 13);

            JLabel capK = new JLabel("Capacity");
            JLabel capV = new JLabel(String.valueOf(f.getCapacity()));
            JLabel seatsK = new JLabel("Seats left");
            JLabel seatsV = new JLabel(String.valueOf(f.getSeatsLeft()));
            JLabel baseK = new JLabel("Base price");
            JLabel baseV = new JLabel(String.format("%.2f", f.getBasePrice()));

            for (JLabel l : new JLabel[]{capK, seatsK, baseK}) l.setFont(keyFont);
            for (JLabel l : new JLabel[]{capV, seatsV, baseV}) l.setFont(valFont);

            JPanel stats = new JPanel(new GridBagLayout());
            stats.setBackground(Color.WHITE);
            stats.setBorder(BorderFactory.createEmptyBorder(8, 16, 10, 16));
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6, 0, 6, 0);
            gc.gridy = 0;

            java.util.function.BiConsumer<JLabel, JLabel> addStat = (k, v) -> {
                gc.gridx = 0;
                gc.weightx = 0;
                gc.anchor = GridBagConstraints.LINE_START;
                stats.add(k, gc);

                gc.gridx = 1;
                gc.weightx = 1;
                gc.anchor = GridBagConstraints.LINE_END;
                stats.add(v, gc);

                gc.gridy++;
            };

            addStat.accept(capK, capV);
            addStat.accept(seatsK, seatsV);
            addStat.accept(baseK, baseV);

            // ---------- Passengers list ----------
            JLabel paxTitle = new JLabel("Passengers");
            paxTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

            DefaultListModel<String> model = new DefaultListModel<>();
            if (f.getPassengers().isEmpty()) {
                model.addElement("No passengers");
            } else {
                for (Customer c : f.getPassengers()) {
                    model.addElement(c.getId() + "  •  " + c.getName());
                }
            }

            JList<String> paxList = new JList<>(model);
            paxList.setFont(new Font("SansSerif", Font.PLAIN, 13));
            paxList.setVisibleRowCount(Math.min(8, Math.max(3, model.getSize())));
            paxList.setFixedCellHeight(26);
            paxList.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            JScrollPane paxScroll = new JScrollPane(paxList);
            paxScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

            JPanel paxPanel = new JPanel(new BorderLayout(0, 8));
            paxPanel.setBackground(Color.WHITE);
            paxPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 16, 16));
            paxPanel.add(paxTitle, BorderLayout.NORTH);
            paxPanel.add(paxScroll, BorderLayout.CENTER);

            // ---------- Card container ----------
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.add(header, BorderLayout.NORTH);

            JPanel mid = new JPanel(new BorderLayout());
            mid.setBackground(Color.WHITE);
            mid.add(stats, BorderLayout.NORTH);
            mid.add(paxPanel, BorderLayout.CENTER);

            card.add(mid, BorderLayout.CENTER);

            // Outer padding + subtle border (looks modern)
            JPanel outer = new JPanel(new BorderLayout());
            outer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(12, 12, 12, 12),
                    BorderFactory.createLineBorder(new Color(210, 210, 210))
            ));
            outer.setBackground(new Color(245, 246, 248));
            outer.add(card, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                    this,
                    outer,
                    "Flight Details",
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (Exception ex) {
            UIUtil.msg(this, ex.getMessage());
        }
    }
    /**
    * Performs the deleteSelectedFlight operation.
    */


    private void deleteSelectedFlight() {
        if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }

        Integer id = selectedFlightId();
        if (id == null) {
            UIUtil.msg(this, "Select a flight first.");
            return;
        }

        if (UIUtil.confirm(this, "Delete (hide) flight #" + id + "?") != JOptionPane.YES_OPTION) return;

        try {
            fbs.deleteFlight(id);
            saveNow(true);
            refreshFlights();
        } catch (Exception ex) {
            UIUtil.msg(this, "Failed: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------------------
    // Customers actions (admin)
    // ---------------------------------------------------------------------
    /**
    * Adds a new customer dialog to the collection.
    */

    private void addCustomerDialog() {
        if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }

        // Fields (nice size + font)
        JTextField fn = new JTextField(18);
        JTextField ln = new JTextField(18);
        JTextField phone = new JTextField(18);
        UIUtil.limitToDigits(phone, 10);
        JTextField email = new JTextField(18);
        JTextField username = new JTextField(18);
        JPasswordField pass = new JPasswordField(18);
        JPasswordField cpass = new JPasswordField(18);

        Font f = fn.getFont().deriveFont(13f);
        for (JComponent c : new JComponent[]{fn, ln, phone, email, username, pass, cpass}) {
            c.setFont(f);
            c.setPreferredSize(new Dimension(260, 28));
        }

        // Header
        JLabel header = new JLabel("Create Customer Account");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        header.setBorder(BorderFactory.createEmptyBorder(12, 14, 6, 14));

        JLabel sub = new JLabel("This will create a login account (username + password) for the customer.");
        sub.setFont(sub.getFont().deriveFont(12f));
        sub.setForeground(new Color(80, 80, 80));
        sub.setBorder(BorderFactory.createEmptyBorder(0, 14, 10, 14));

        JPanel headerBox = new JPanel(new BorderLayout());
        headerBox.setOpaque(false);
        headerBox.add(header, BorderLayout.NORTH);
        headerBox.add(sub, BorderLayout.SOUTH);

        // Form (clean alignment using GridBagLayout)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 14, 12, 14));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.gridy = 0;

        // helper to add row
        java.util.function.BiConsumer<String, JComponent> row = (label, field) -> {
            gc.gridx = 0;
            gc.weightx = 0;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.LINE_END;
            JLabel l = new JLabel(label);
            l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
            form.add(l, gc);

            gc.gridx = 1;
            gc.weightx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.anchor = GridBagConstraints.LINE_START;
            form.add(field, gc);

            gc.gridy++;
        };

        row.accept("First Name", fn);
        row.accept("Last Name", ln);
        row.accept("Phone (10 digits)", phone);
        row.accept("Email", email);
        row.accept("Username", username);
        row.accept("Password", pass);
        row.accept("Confirm Password", cpass);

        // Wrap everything
        JPanel container = new JPanel(new BorderLayout());
        container.add(headerBox, BorderLayout.NORTH);
        container.add(form, BorderLayout.CENTER);

        int ok = JOptionPane.showConfirmDialog(
                this,
                container,
                "Admin - Create Customer Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (ok != JOptionPane.OK_OPTION) return;

        try {
            auth.registerUser(
                    fbs,
                    fn.getText().trim(),
                    ln.getText().trim(),
                    phone.getText().trim(),
                    email.getText().trim(),
                    username.getText().trim(),
                    new String(pass.getPassword()),
                    new String(cpass.getPassword())
            );

            saveNow(true);
            refreshAll();

            // Nicer success message (HTML)
            UIUtil.msg(this,
                    "<html><b>Customer account created successfully!</b><br><br>" +
                            "Username: <b>" + username.getText().trim() + "</b><br>" +
                            "The user can now login using the password you set.</html>"
            );

        } catch (AuthService.AuthException ex) {
            UIUtil.msg(this, "<html><b>Validation Error</b><br><br>" + ex.getMessage() + "</html>");
        } catch (Exception ex) {
            UIUtil.msg(this, "<html><b>Error</b><br><br>Failed to create customer account: " + ex.getMessage() + "</html>");
        }
    }
    /**
    * Performs the deleteSelectedCustomer operation.
    */


    private void deleteSelectedCustomer() {
        if (!isAdmin()) { UIUtil.msg(this, "Admin only."); return; }

        Integer id = selectedCustomerId_Admin();
        if (id == null) {
            UIUtil.msg(this, "Select a customer first.");
            return;
        }

        if (UIUtil.confirm(this, "Delete (hide) customer #" + id + "?") != JOptionPane.YES_OPTION) return;

        try {
            fbs.deleteCustomer(id);
            saveNow(true);
            refreshAll();
        } catch (Exception ex) {
            UIUtil.msg(this, "Failed: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------------------
    // Booking details popup
    // ---------------------------------------------------------------------
    /**
    * Performs show customer bookings popup_ admin or self operation.
    */

    private void showCustomerBookingsPopup_AdminOrSelf() {
        try {
            int custId;

            if (isAdmin()) {
                Integer sel = selectedCustomerId_Admin();
                if (sel == null) {
                    UIUtil.msg(this, "Select a customer first.");
                    return;
                }
                custId = sel;
            } else {
                custId = myCustomerId();
            }

            Customer c = fbs.getCustomerByID(custId);

            // Show a clearer, boxed dialog (also includes return flight for round-trips)
            showCustomerBookingDetailsDialog(c);

        } catch (Exception ex) {
            UIUtil.msg(this, ex.getMessage());
        }
    }
    /**
    * Shows customer booking details in a boxed/tabled Swing dialog (not CLI-style text).
    * Includes return flight information for round-trip bookings.
    */

    private void showCustomerBookingDetailsDialog(Customer c) {
        final JDialog dlg = new JDialog(this, "Customer Booking Details", true);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setLayout(new BorderLayout(10, 10));

        // ===== Customer card =====
        JPanel customerCard = new JPanel(new GridBagLayout());
        customerCard.setBorder(BorderFactory.createTitledBorder("Customer"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;

        addKV(customerCard, gc, 0, "Customer ID:", String.valueOf(c.getId()));
        addKV(customerCard, gc, 1, "Name:", c.getName());
        addKV(customerCard, gc, 2, "Phone:", c.getPhone());
        addKV(customerCard, gc, 3, "Email:", c.getEmail());
        dlg.add(customerCard, BorderLayout.NORTH);

        // ===== Bookings (tabs) =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        if (c.getBookings().isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.add(new JLabel("No bookings found."), BorderLayout.CENTER);
            tabs.addTab("Bookings", empty);
        } else {
            int idx = 1;
            for (Booking b : c.getBookings()) {
                String tabTitle = "#" + b.getId() + " - " + (b.isRoundTrip() ? "Round-trip" : "One-way");
                tabs.addTab(tabTitle, buildBookingTab(b, idx++));
            }
        }
        dlg.add(tabs, BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> dlg.dispose());
        btns.add(ok);
        dlg.add(btns, BorderLayout.SOUTH);

        dlg.setMinimumSize(new Dimension(780, 560));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
    /**
    * Performs the buildBookingTab operation.
    *
    * @param b the b value
    * @param index the index value
    * @return the operation result
    */


    private JPanel buildBookingTab(Booking b, int index) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header row (Status + dates)
        JPanel header = new JPanel(new GridLayout(2, 2, 10, 6));
        header.setBorder(BorderFactory.createTitledBorder("Booking Summary"));

        header.add(labelPair("Booking ID", String.valueOf(b.getId())));
        header.add(labelPair("Status", String.valueOf(b.getStatus())));
        header.add(labelPair("Booked", String.valueOf(b.getBookingDate())));
        header.add(labelPair("Last Updated", String.valueOf(b.getLastUpdated())));
        root.add(header, BorderLayout.NORTH);

        // Flights (Outbound / Return) side by side
        JPanel flightsRow = new JPanel(new GridLayout(1, 2, 10, 10));
        flightsRow.add(buildFlightBox("Outbound Flight", b.getOutboundFlight()));
        flightsRow.add(buildFlightBox("Return Flight", b.getReturnFlight()));
        root.add(flightsRow, BorderLayout.CENTER);

        // Bottom: Payment + History
        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.add(buildPaymentBox(b));
        bottom.add(buildHistoryBox(b));
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }
    /**
    * Performs the buildFlightBox operation.
    *
    * @param title the title value
    * @param f the f value
    * @return the operation result
    */


    private JPanel buildFlightBox(String title, Flight f) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        if (f == null) {
            addKV(p, gc, 0, "Flight:", "N/A");
            addKV(p, gc, 1, "Route:", "N/A");
            addKV(p, gc, 2, "Date:", "N/A");
            addKV(p, gc, 3, "Status:", "—");
            return p;
        }

        addKV(p, gc, 0, "Flight:", f.getFlightNumber());
        addKV(p, gc, 1, "Route:", f.getOrigin() + " → " + f.getDestination());
        addKV(p, gc, 2, "Date:", String.valueOf(f.getDepartureDate()));
        addKV(p, gc, 3, "Status:", f.isDeleted() ? "DELETED/HIDDEN" : "ACTIVE");
        return p;
    }
    /**
    * Performs the buildPaymentBox operation.
    *
    * @param b the b value
    * @return the operation result
    */


    private JPanel buildPaymentBox(Booking b) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Payment"));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        addKV(p, gc, 0, "Paid:", String.format("%.2f", b.getPricePaid()));
        addKV(p, gc, 1, "Fee:", String.format("%.2f", b.getFeeCharged()));
        return p;
    }
    /**
    * Performs the buildHistoryBox operation.
    *
    * @param b the b value
    * @return the operation result
    */


    private JPanel buildHistoryBox(Booking b) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("History"));

        JTextArea ta = new JTextArea(String.valueOf(b.getHistoryString()));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ta.setBackground(new Color(250, 250, 250));
        ta.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createEmptyBorder());
        p.add(sp, BorderLayout.CENTER);
        return p;
    }
    /**
    * Performs the labelPair operation.
    *
    * @param k the k value
    * @param v the v value
    * @return the operation result
    */


    private JPanel labelPair(String k, String v) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        JLabel kl = new JLabel(k + ":");
        kl.setFont(kl.getFont().deriveFont(Font.BOLD));
        JLabel vl = new JLabel(v);
        p.add(kl, BorderLayout.WEST);
        p.add(vl, BorderLayout.CENTER);
        return p;
    }
 /**
  * Performs the addKV operation.
    *
    * @param panel the panel value
    * @param gc the gc value
    * @param row the row value
    * @param k the k value
    * @param v the v value
    */


    private void addKV(JPanel panel, GridBagConstraints gc, int row, String k, String v) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0.0;
        JLabel kl = new JLabel(k);
        kl.setFont(kl.getFont().deriveFont(Font.BOLD));
        panel.add(kl, gc);

        gc.gridx = 1;
        gc.weightx = 1.0;
        panel.add(new JLabel(v == null ? "" : v), gc);
    }
    /**
    * Checks if the specified condition is true.
    */


    private void issueBookingDialog() {
    issueBookingDialog(null);
}
/**
* Booking dialog used by both Admin and User.
* If preselectedOutboundFlightId is not null, the outbound flight selector is pre-selected to that flight.
*/

private void issueBookingDialog(Integer preselectedOutboundFlightId) {
    try {
        List<Flight> flights = fbs.getActiveFutureFlights();
        if (flights.isEmpty()) {
            UIUtil.msg(this, "No future flights available.");
            return;
        }

        Customer bookingCustomer;
        JComboBox<Customer> custBox = null;

        if (isAdmin()) {
            List<Customer> customers = fbs.getActiveCustomers();
            if (customers.isEmpty()) {
                UIUtil.msg(this, "No customers available.");
                return;
            }
            custBox = new JComboBox<>(customers.toArray(new Customer[0]));
            bookingCustomer = (Customer) custBox.getSelectedItem();
        } else {
            bookingCustomer = myCustomerOrNull();
            if (bookingCustomer == null) {
                UIUtil.msg(this, "Your user account is not linked to a customer.");
                return;
            }
        }

        JCheckBox roundTripCheckBox = new JCheckBox("Round-trip booking");
        JComboBox<Flight> outboundFlightBox = new JComboBox<>(flights.toArray(new Flight[0]));
        JComboBox<Flight> returnFlightBox = new JComboBox<>(flights.toArray(new Flight[0]));
        JLabel priceLabel = new JLabel(" ");

        // Preselect outbound flight (when booking from table selection)
        if (preselectedOutboundFlightId != null) {
            for (int i = 0; i < outboundFlightBox.getItemCount(); i++) {
                Flight f = outboundFlightBox.getItemAt(i);
                if (f != null && f.getId() == preselectedOutboundFlightId) {
                    outboundFlightBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Initially disable return flight selector
        returnFlightBox.setEnabled(false);

        roundTripCheckBox.addActionListener(e -> {
            boolean isRoundTrip = roundTripCheckBox.isSelected();
            returnFlightBox.setEnabled(isRoundTrip);
            updateBookingPrice(outboundFlightBox, returnFlightBox, isRoundTrip, priceLabel);
        });

        outboundFlightBox.addActionListener(e ->
                updateBookingPrice(outboundFlightBox, returnFlightBox, roundTripCheckBox.isSelected(), priceLabel)
        );

        returnFlightBox.addActionListener(e ->
                updateBookingPrice(outboundFlightBox, returnFlightBox, roundTripCheckBox.isSelected(), priceLabel)
        );

        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));

        if (isAdmin()) {
            p.add(new JLabel("Customer:"));
            p.add(custBox);
        } else {
            p.add(new JLabel("Customer (You):"));
            p.add(new JLabel(bookingCustomer.getId() + " - " + bookingCustomer.getName()));
        }

        p.add(new JLabel("Outbound Flight:"));
        p.add(outboundFlightBox);

        p.add(roundTripCheckBox);
        p.add(new JLabel("Return Flight:"));
        p.add(returnFlightBox);

        Flight f0 = (Flight) outboundFlightBox.getSelectedItem();
        if (f0 != null) {
            priceLabel.setText("Price today: " + String.format("%.2f", fbs.calculateCurrentPrice(f0)));
        }
        p.add(priceLabel);

        String title = isAdmin() ? "Issue Booking" : "Book Flight";
        int ok = JOptionPane.showConfirmDialog(this, p, title, JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        Flight outbound = (Flight) outboundFlightBox.getSelectedItem();
        if (outbound == null) return;

        Customer c = isAdmin() ? (Customer) custBox.getSelectedItem() : bookingCustomer;
        if (c == null) return;

        Integer returnFlightId = null;
        if (roundTripCheckBox.isSelected()) {
            Flight returnFlight = (Flight) returnFlightBox.getSelectedItem();
            if (returnFlight != null) {
                returnFlightId = returnFlight.getId();
            }
        }

        Booking b = fbs.issueBooking(c.getId(), outbound.getId(), returnFlightId);
        saveNow(true);
        refreshAll();

        String bookingType = returnFlightId != null ? "Round-trip" : "One-way";
        UIUtil.msg(this, bookingType + " booking created successfully!\n" + b.getDetails());

    } catch (Exception ex) {
        UIUtil.msg(this, "Failed: " + ex.getMessage());
    }
}



    private void updateBookingPrice(JComboBox<Flight> outboundBox, JComboBox<Flight> returnBox,
                                    boolean isRoundTrip, JLabel priceLabel) {
        Flight outbound = (Flight) outboundBox.getSelectedItem();
        if (outbound == null) {
            priceLabel.setText(" ");
            return;
        }

        double totalPrice = fbs.calculateCurrentPrice(outbound);

        if (isRoundTrip) {
            Flight returnFlight = (Flight) returnBox.getSelectedItem();
            if (returnFlight != null) {
                totalPrice += fbs.calculateCurrentPrice(returnFlight);
            }
        }

        String type = isRoundTrip ? "Round-trip" : "One-way";
        priceLabel.setText(type + " price: " + String.format("%.2f", totalPrice));
    }
    /**
    * Rebook dialog using a dropdown (JComboBox) for selecting Booking ID
    * based on the chosen Customer ID (admin) or current user (non-admin).
    */

    private void rebookDialog() {
        try {
            int custId;

            // 1) Decide customer
            if (isAdmin()) {
                String custIdStr = JOptionPane.showInputDialog(this, "Customer ID:");
                if (custIdStr == null) return;
                custId = Integer.parseInt(custIdStr.trim());
            } else {
                custId = myCustomerId();
            }

            Customer customer = fbs.getCustomerByID(custId);

            // 2) Build list of ACTIVE bookings for this customer
            List<Booking> activeBookings = new ArrayList<>();
            for (Booking b : customer.getBookings()) {
                if (b.getStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE) {
                    activeBookings.add(b);
                }
            }

            if (activeBookings.isEmpty()) {
                UIUtil.msg(this, "No ACTIVE bookings found for customer #" + custId);
                return;
            }

            // 3) Booking dropdown
            DefaultComboBoxModel<Booking> bookingModel = new DefaultComboBoxModel<>();
            for (Booking b : activeBookings) bookingModel.addElement(b);

            JComboBox<Booking> bookingBox = new JComboBox<>(bookingModel);

            bookingBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (value instanceof Booking b) {
                        Flight out = b.getOutboundFlight();
                        String text = "Booking #" + b.getId()
                                + " | " + (b.isRoundTrip() ? "Round-trip" : "One-way")
                                + " | " + out.getOrigin() + " → " + out.getDestination()
                                + " | " + out.getDepartureDate();
                        setText(text);
                    }
                    return this;
                }
            });

            // 4) Ask user to pick booking first
            JPanel pick = new JPanel(new GridLayout(0, 1, 6, 6));
            pick.add(new JLabel("Customer: #" + customer.getId() + " - " + customer.getName()));
            pick.add(new JLabel("Select Booking to Rebook:"));
            pick.add(bookingBox);

            int okPick = JOptionPane.showConfirmDialog(this, pick, "Select Booking", JOptionPane.OK_CANCEL_OPTION);
            if (okPick != JOptionPane.OK_OPTION) return;

            Booking currentBooking = (Booking) bookingBox.getSelectedItem();
            if (currentBooking == null) {
                UIUtil.msg(this, "Select a booking first.");
                return;
            }

            int bookingId = currentBooking.getId();

            // 5) Load available flights
            List<Flight> flights = fbs.getActiveFutureFlights();
            if (flights.isEmpty()) {
                UIUtil.msg(this, "No future flights available.");
                return;
            }

            // 6) Build rebook UI
            JCheckBox roundTripCheckBox = new JCheckBox("Round-trip booking");
            JComboBox<Flight> outboundFlightBox = new JComboBox<>(flights.toArray(new Flight[0]));
            JComboBox<Flight> returnFlightBox = new JComboBox<>(flights.toArray(new Flight[0]));
            JLabel priceLabel = new JLabel(" ");

            // Pre-select the current outbound flight
            Flight currentOutbound = currentBooking.getOutboundFlight();
            if (currentOutbound != null) outboundFlightBox.setSelectedItem(currentOutbound);

            // Pre-select return flight if round-trip
            if (currentBooking.isRoundTrip() && currentBooking.getReturnFlight() != null) {
                roundTripCheckBox.setSelected(true);
                returnFlightBox.setEnabled(true);
                returnFlightBox.setSelectedItem(currentBooking.getReturnFlight());
            } else {
                roundTripCheckBox.setSelected(false);
                returnFlightBox.setEnabled(false);
            }

            // Price update hooks (you already have updateBookingPrice)
            roundTripCheckBox.addActionListener(e -> {
                returnFlightBox.setEnabled(roundTripCheckBox.isSelected());
                updateBookingPrice(outboundFlightBox, returnFlightBox, roundTripCheckBox.isSelected(), priceLabel);
            });

            outboundFlightBox.addActionListener(e ->
                    updateBookingPrice(outboundFlightBox, returnFlightBox, roundTripCheckBox.isSelected(), priceLabel)
            );

            returnFlightBox.addActionListener(e ->
                    updateBookingPrice(outboundFlightBox, returnFlightBox, roundTripCheckBox.isSelected(), priceLabel)
            );

            // Initial price
            updateBookingPrice(outboundFlightBox, returnFlightBox, roundTripCheckBox.isSelected(), priceLabel);

            JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
            p.add(new JLabel("Rebooking Booking #" + bookingId));
            p.add(new JLabel("New Outbound Flight:"));
            p.add(outboundFlightBox);
            p.add(roundTripCheckBox);
            p.add(new JLabel("New Return Flight:"));
            p.add(returnFlightBox);
            p.add(priceLabel);

            int ok = JOptionPane.showConfirmDialog(this, p, "Rebook Booking", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;

            Flight outbound = (Flight) outboundFlightBox.getSelectedItem();
            if (outbound == null) return;

            Integer returnFlightId = null;
            if (roundTripCheckBox.isSelected()) {
                Flight returnFlight = (Flight) returnFlightBox.getSelectedItem();
                if (returnFlight != null) returnFlightId = returnFlight.getId();
            }

            Booking updated = fbs.updateBooking(custId, bookingId, outbound.getId(), returnFlightId);

            saveNow(true);
            refreshAll();

            String bookingType = returnFlightId != null ? "Round-trip" : "One-way";
            UIUtil.msg(this, "Rebooked to " + bookingType + "!\n" + updated.getDetails());

        } catch (NumberFormatException ex) {
            UIUtil.msg(this, "Customer ID must be a number.");
        } catch (Exception ex) {
            UIUtil.msg(this, "Failed: " + ex.getMessage());
        }
    }
    /**
    * Performs the cancelBookingDialog operation.
    */


    private void cancelBookingDialog() {
        try {
            int custId;

            if (isAdmin()) {
                String custIdStr = JOptionPane.showInputDialog(this, "Customer ID:");
                if (custIdStr == null) return;
                custId = Integer.parseInt(custIdStr.trim());
            } else {
                custId = myCustomerId();
            }

            // Get customer and their bookings
            Customer customer = fbs.getCustomerByID(custId);

            // Filter ACTIVE bookings only (recommended)
            List<Booking> activeBookings = new ArrayList<>();
            for (Booking b : customer.getBookings()) {
                if (b.getStatus() == bcu.cmp5332.bookingsystem.model.BookingStatus.ACTIVE) {
                    activeBookings.add(b);
                }
            }

            if (activeBookings.isEmpty()) {
                UIUtil.msg(this, "No ACTIVE bookings found for customer #" + custId);
                return;
            }

            // Dropdown model
            DefaultComboBoxModel<Booking> model = new DefaultComboBoxModel<>();
            for (Booking b : activeBookings) model.addElement(b);

            JComboBox<Booking> bookingBox = new JComboBox<>(model);

            // Show nice text instead of Booking.toString()
            bookingBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (value instanceof Booking b) {
                        Flight out = b.getOutboundFlight();
                        String text = "Booking #" + b.getId()
                                + " | " + (b.isRoundTrip() ? "Round-trip" : "One-way")
                                + " | " + out.getOrigin() + " → " + out.getDestination()
                                + " | " + out.getDepartureDate();
                        setText(text);
                    }
                    return this;
                }
            });

            JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
            p.add(new JLabel("Customer: #" + customer.getId() + " - " + customer.getName()));
            p.add(new JLabel("Select Booking to Cancel:"));
            p.add(bookingBox);

            int ok = JOptionPane.showConfirmDialog(this, p, "Cancel Booking", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;

            Booking selected = (Booking) bookingBox.getSelectedItem();
            if (selected == null) {
                UIUtil.msg(this, "Please select a booking.");
                return;
            }

            int bookingId = selected.getId();

            Booking result;
            if (isAdmin()) {
                Object[] options = {"Cancel (fee applies)", "Void (no fee)", "Close"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Choose how to cancel this booking:",
                        "Cancel Booking",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choice == 1) {
                    result = fbs.voidBookingByAdmin(custId, bookingId);
                } else if (choice == 0) {
                    result = fbs.cancelBooking(custId, bookingId);
                } else {
                    return;
                }
            } else {
                result = fbs.cancelBooking(custId, bookingId);
            }

            saveNow(true);
            refreshAll();
            UIUtil.msg(this, "Cancelled!\n" + result.getDetails());

        } catch (NumberFormatException ex) {
            UIUtil.msg(this, "Customer ID must be a number.");
        } catch (Exception ex) {
            UIUtil.msg(this, "Failed: " + ex.getMessage());
        }
    }
    /**
    * Performs the viewBookingHistoryDialog operation.
    */


    private void viewBookingHistoryDialog() {
        try {
            String in = JOptionPane.showInputDialog(this, "Enter Booking ID:");
            if (in == null) return;

            int bookingId = Integer.parseInt(in.trim());
            Booking b = fbs.getBookingById(bookingId);

            // permission: user can view only their own bookings
            if (!isAdmin() && b.getCustomer().getId() != myCustomerId()) {
                UIUtil.msg(this, "You can only view your own booking history.");
                return;
            }

            new BookingHistoryDialog(this, b).setVisible(true);

        } catch (NumberFormatException ex) {
            UIUtil.msg(this, "Booking ID must be a number.");
        } catch (Exception ex) {
            UIUtil.msg(this, ex.getMessage());
        }
    }
}