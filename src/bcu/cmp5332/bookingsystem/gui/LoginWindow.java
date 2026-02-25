package bcu.cmp5332.bookingsystem.gui;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.UserStore;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
* Represents a LoginWindow in the flight booking system.
*/

public class LoginWindow extends JFrame {

    private final FlightBookingSystem fbs;
    private final AuthService auth;

    private JTextField username;
    private JPasswordField password;
    private JLabel status;

    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;


        this.auth = new AuthService(new UserStore("resources/data/users.txt"));

        UIUtil.setModernLAF();
        build();
    }
    /**
    * Performs the build operation.
    */


    private void build() {
        setTitle("Flight Booking System | Login");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);

        ModernBackgroundPanel bg = new ModernBackgroundPanel("/image/airplane_bg.jpg");
        bg.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(380, 360));
        card.setBackground(new Color(255, 255, 255, 235));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;

        JLabel title = new JLabel("Login");
        title.setFont(UIUtil.h1());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, gc);

        gc.gridy++;
        JLabel hint = new JLabel("Admin login: Admin / admin123");
        hint.setFont(UIUtil.base());
        card.add(hint, gc);

        gc.gridy++;
        gc.gridwidth = 1;
        gc.gridx = 0;
        card.add(new JLabel("Username"), gc);

        gc.gridx = 1;
        username = new JTextField(16);
        card.add(username, gc);

        gc.gridx = 0;
        gc.gridy++;
        card.add(new JLabel("Password"), gc);

        gc.gridx = 1;
        password = new JPasswordField(16);
        card.add(password, gc);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 2;

        JButton loginBtn = new JButton("Login");
        JButton regBtn = new JButton("Register");

        loginBtn.setBackground(new Color(60, 120, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
        btns.setOpaque(false);
        btns.add(regBtn);
        btns.add(loginBtn);
        card.add(btns, gc);

        gc.gridy++;
        status = new JLabel(" ");
        status.setForeground(new Color(180, 40, 40));
        status.setFont(UIUtil.base());
        card.add(status, gc);


        GridBagConstraints bgc = new GridBagConstraints();
        bgc.gridx = 0;
        bgc.gridy = 0;
        bgc.anchor = GridBagConstraints.CENTER;
        bg.add(card, bgc);

        setContentPane(bg);

        // Actions
        loginBtn.addActionListener(e -> doLogin());

        regBtn.addActionListener(e -> {

            new RegisterWindow(this, auth, fbs).setVisible(true);
            setVisible(false);
        });

        //Enter key support for both text fields
        KeyAdapter enterKeyListener = new KeyAdapter() {
            /**
            * Performs key pressed operation.
            *
            * @param e the e value to use
            */
            @Override

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        };

        username.addKeyListener(enterKeyListener);
        password.addKeyListener(enterKeyListener);

        setVisible(true);
    }
    /**
    * Performs the doLogin operation.
    */


    private void doLogin() {
        status.setText(" ");
        try {
            AppUser u = auth.login(username.getText().trim(), new String(password.getPassword()));
            dispose();
            new MainWindow(fbs, u).setVisible(true);
        } catch (AuthService.AuthException ex) {
            status.setText(ex.getMessage());
            password.setText("");
        }
    }
    /**
    * Performs the back operation.
    */


    public void back() {
        setVisible(true);
    }
}