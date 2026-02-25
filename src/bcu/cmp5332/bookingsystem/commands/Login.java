package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.auth.*;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a Login in the flight booking system.
*/

public class Login implements Command {

    private final String username;
    private final String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        try {
            AuthService auth = new AuthService(new UserStore("resources/data/users.txt"));
            AppUser u = auth.login(username, password);
            Session.login(u);

            System.out.println("Logged in as: " + u.getUsername() + " (" + u.getRole() + ")");
            if (!u.isAdmin()) {
                System.out.println("Your Customer ID: " + u.getCustomerId());
            }
        } catch (AuthService.AuthException ex) {
            throw new FlightBookingSystemException(ex.getMessage());
        }
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() { return false; }
}
