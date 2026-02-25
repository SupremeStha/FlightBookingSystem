package bcu.cmp5332.bookingsystem.gui;


import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
* Represents a RegisterWindow in the flight booking system.
*/

public class RegisterWindow extends JFrame {

    private final LoginWindow login;
    private final AuthService auth;
    private final FlightBookingSystem fbs;

    private JTextField fn, ln, phone, email, user;
    private JPasswordField pass, cpass;
    private JLabel status;


    public RegisterWindow(LoginWindow login, AuthService auth, FlightBookingSystem fbs) {
        this.login = login;
        this.auth = auth;
        this.fbs = fbs;

        UIUtil.setModernLAF();
        build();
    }
    /**
    * Performs the build operation.
    */


    private void build() {
        setTitle("Flight Booking System | Register");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        ModernBackgroundPanel bg = new ModernBackgroundPanel("/image/Nepflight.jpg");
        bg.setLayout(new GridBagLayout());

        // Card panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(640, 420));
        card.setBackground(new Color(255, 255, 255, 235));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(30, 35, 30, 35)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weighty = 0;

        int r = 0;

        // Title
        JLabel title = new JLabel("Register");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gc.gridx = 0;
        gc.gridy = r++;
        gc.gridwidth = 4;
        gc.weightx = 1.0;
        card.add(title, gc);

        // Fields
        fn = new JTextField();
        ln = new JTextField();
        phone = new JTextField();
        UIUtil.limitToDigits(phone, 10);
        email = new JTextField();
        user = new JTextField();
        pass = new JPasswordField();
        cpass = new JPasswordField();

        Dimension fieldSize = new Dimension(240, 32);
        for (JComponent c : new JComponent[]{fn, ln, phone, email, user, pass, cpass}) {
            c.setPreferredSize(fieldSize);
        }

        addRow(card, gc, r++, "First Name", fn, "Last Name", ln);
        addRow(card, gc, r++, "Phone", phone, "Email", email);
        addRow(card, gc, r++, "Username", user, "Password", pass);
        addSingleRow(card, gc, r++, "Confirm Password", cpass);

        // Status label
        status = new JLabel(" ");
        status.setForeground(new Color(180, 40, 40));
        status.setFont(UIUtil.base());

        gc.gridx = 0;
        gc.gridy = r++;
        gc.gridwidth = 4;
        gc.weightx = 1.0;
        card.add(status, gc);

        // Buttons
        JButton back = new JButton("Back");
        JButton reg = new JButton("Register");
        reg.setBackground(new Color(60, 120, 200));
        reg.setForeground(Color.WHITE);
        reg.setFocusPainted(false);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        btns.setOpaque(false);
        btns.add(back);
        btns.add(reg);

        gc.gridx = 0;
        gc.gridy = r++;
        gc.gridwidth = 4;
        gc.weightx = 1.0;
        card.add(btns, gc);

        // Center card in background
        GridBagConstraints bgc = new GridBagConstraints();
        bgc.gridx = 0;
        bgc.gridy = 0;
        bgc.anchor = GridBagConstraints.CENTER;
        bgc.insets = new Insets(20, 20, 20, 20);
        bg.add(card, bgc);

        setContentPane(bg);

        // Actions
        back.addActionListener(e -> {
            dispose();
            login.back();
        });

        reg.addActionListener(e -> doRegister());


        KeyAdapter enterKeyListener = new KeyAdapter() {
            /**
            * Performs key pressed operation.
            *
            * @param e the e value to use
            */
            @Override

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doRegister();
                }
            }
        };

        fn.addKeyListener(enterKeyListener);
        ln.addKeyListener(enterKeyListener);
        phone.addKeyListener(enterKeyListener);
        email.addKeyListener(enterKeyListener);
        user.addKeyListener(enterKeyListener);
        pass.addKeyListener(enterKeyListener);
        cpass.addKeyListener(enterKeyListener);

        setVisible(true);
    }

    private void addRow(JPanel card, GridBagConstraints gc, int row,
                        String l1, JComponent f1, String l2, JComponent f2) {

        gc.gridy = row;
        gc.gridwidth = 1;

        // label 1
        gc.weightx = 0.0;
        gc.gridx = 0;
        card.add(label(l1), gc);

        // field 1
        gc.weightx = 1.0;
        gc.gridx = 1;
        card.add(f1, gc);

        // label 2
        gc.weightx = 0.0;
        gc.gridx = 2;
        card.add(label(l2), gc);

        // field 2
        gc.weightx = 1.0;
        gc.gridx = 3;
        card.add(f2, gc);
    }

    private void addSingleRow(JPanel card, GridBagConstraints gc, int row,
                              String label, JComponent field) {

        gc.gridy = row;

        gc.gridwidth = 1;
        gc.weightx = 0.0;
        gc.gridx = 0;
        card.add(this.label(label), gc);

        gc.gridx = 1;
        gc.gridwidth = 3;
        gc.weightx = 1.0;
        card.add(field, gc);

        gc.gridwidth = 1;
    }
    /**
    * Performs the label operation.
    *
    * @param text the text value
    * @return the operation result
    */


    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIUtil.base());
        return l;
    }
    /**
    * Performs the doRegister operation.
    */


    private void doRegister() {
        status.setText(" ");

        try {
            auth.registerUser(
                    fbs,
                    fn.getText().trim(),
                    ln.getText().trim(),
                    phone.getText().trim(),
                    email.getText().trim(),
                    user.getText().trim(),
                    new String(pass.getPassword()),
                    new String(cpass.getPassword())
            );

            UIUtil.msg(this, "Registered successfully! Now login.");
            dispose();
            login.back();

        } catch (AuthService.AuthException ex) {
            status.setText(ex.getMessage());
            pass.setText("");
            cpass.setText("");
        } catch (Exception ex) { //catches FlightBookingSystemException (duplicate email)
            status.setText(ex.getMessage());
            pass.setText("");
            cpass.setText("");
        }
    }

}