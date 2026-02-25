package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.Session;
import bcu.cmp5332.bookingsystem.auth.UserStore;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a AddCustomer in the flight booking system.
*/

public class AddCustomer implements Command {

    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String email;
    private final String username;
    private final String password;
    private final String confirmPassword;

    public AddCustomer(String firstName, String lastName, String phone, String email,
                       String username, String password, String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {

        if (!Session.isAdmin()) {
            throw new FlightBookingSystemException("Admin only.");
        }

        try {
            AuthService auth = new AuthService(new UserStore("resources/data/users.txt"));

            auth.registerUser(
                    fbs,
                    firstName, lastName,
                    phone, email,
                    username,
                    password, confirmPassword
            );

            System.out.println("Customer account created successfully.");
            System.out.println("User can now login with username: " + username);

        } catch (AuthService.AuthException ex) {
            throw new FlightBookingSystemException(ex.getMessage());
        }
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() {
        return true;
    }
}
