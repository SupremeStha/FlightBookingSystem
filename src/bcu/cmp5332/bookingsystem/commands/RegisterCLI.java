package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.UserStore;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
* Represents a RegisterCLI in the flight booking system.
*/

public class RegisterCLI implements Command {

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("First name: ");
            String first = r.readLine().trim();

            System.out.print("Last name: ");
            String last = r.readLine().trim();

            System.out.print("Phone: ");
            String phone = r.readLine().trim();

            System.out.print("Email: ");
            String email = r.readLine().trim();

            System.out.print("Username: ");
            String username = r.readLine().trim();

            System.out.print("Password: ");
            String pass = r.readLine();

            System.out.print("Confirm password: ");
            String confirm = r.readLine();

            AuthService auth = new AuthService(new UserStore("resources/data/users.txt"));
            AppUser u = auth.registerUser(fbs, first, last, phone, email, username, pass, confirm);

            System.out.println("Registration successful.");
            System.out.println("Your Customer ID: " + u.getCustomerId());
            System.out.println("Now login using: login " + u.getUsername() + " <password>");

        } catch (AuthService.AuthException ex) {
            throw new FlightBookingSystemException(ex.getMessage());
        } catch (Exception ex) {
            throw new FlightBookingSystemException("Registration failed: " + ex.getMessage());
        }
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() { return true; }
}
