package bcu.cmp5332.bookingsystem.auth;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;
import java.util.Map;

/**
 * Handles user authentication, registration, and account management.
 * Validates user credentials during login and enforces password policies.
 * Manages user account creation with email and phone number validation.
 * Integrates with the customer system to create associated customer records.
 */

public class AuthService {

    private final UserStore store;

    public AuthService(UserStore store) {
        this.store = store;
    }
    /**
    * Custom exception for auth errors.
    */

    public static class AuthException extends Exception {
        public AuthException(String msg) { super(msg); }
    }

    // ---------- VALIDATION ----------
    /**
    * Checks if is valid phone10.
    *
    * @param phone the phone value to use
    * @return the boolean result
    */

    private boolean isValidPhone10(String phone) {
        return phone != null && phone.trim().matches("\\d{10}");
    }
    /**
    * Checks if the specified condition is true.
    *
    * @param email the email value
    * @return the operation result
    */


    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        return e.contains("@") && e.contains(".") && !e.startsWith("@") && !e.endsWith("@");
    }

    // ---------- LOGIN ----------
    /**
     /**
     * Attempts to log in a user using a username and password.
     *
     * @param username the username
     * @param password the password
     * @return the authenticated user
     * @throws AuthException if the username/password are invalid or authentication fails
     */

    public AppUser login(String username, String password) throws AuthException {
        if (username == null || username.trim().isEmpty())
            throw new AuthException("Username is required.");
        if (password == null || password.isEmpty())
            throw new AuthException("Password is required.");

        String u = username.trim();

        if (u.equalsIgnoreCase("Admin")) {
            if (!"admin123".equals(password)) throw new AuthException("Invalid admin password.");
            return AppUser.admin();
        }

        try {
            Map<String, AppUser> users = store.loadUsers();

            AppUser found = users.get(u.toLowerCase());
            if (found == null) throw new AuthException("User not found. Please register first.");

            if (!found.getPassword().equals(password)) {
                throw new AuthException("Invalid password.");
            }

            return found;
        } catch (IOException ex) {
            throw new AuthException("Login failed: " + ex.getMessage());
        }
    }

    // ---------- REGISTER ----------
    public AppUser registerUser(FlightBookingSystem fbs,
                                String firstName, String lastName,
                                String phone, String email,
                                String username,
                                String password, String confirmPassword)
            throws AuthException, FlightBookingSystemException {

        if (fbs == null) throw new AuthException("Internal error: system not available.");

        if (firstName == null || firstName.trim().isEmpty()) throw new AuthException("First name is required.");
        if (lastName == null || lastName.trim().isEmpty()) throw new AuthException("Last name is required.");
        if (phone == null || phone.trim().isEmpty()) throw new AuthException("Phone is required.");
        if (email == null || email.trim().isEmpty()) throw new AuthException("Email is required.");
        if (username == null || username.trim().isEmpty()) throw new AuthException("Username is required.");
        if (password == null || password.isEmpty()) throw new AuthException("Password is required.");
        if (confirmPassword == null || confirmPassword.isEmpty()) throw new AuthException("Confirm password is required.");

        String fn = firstName.trim();
        String ln = lastName.trim();
        String ph = phone.trim();
        String em = email.trim();
        String un = username.trim();

        if (un.equalsIgnoreCase("Admin")) throw new AuthException("Username 'Admin' is reserved.");


        if (!isValidPhone10(ph)) throw new AuthException("Phone must be exactly 10 digits.");


        if (!isValidEmail(em)) throw new AuthException("Email format is invalid.");


        if (!password.equals(confirmPassword)) throw new AuthException("Passwords do not match.");
        if (password.length() < 6) throw new AuthException("Password must be at least 6 characters.");

        try {
            Map<String, AppUser> users = store.loadUsers();

            if (users.containsKey(un.toLowerCase())) {
                throw new AuthException("Username already exists.");
            }


            int cid = fbs.nextCustomerId();
            Customer c = new Customer(cid, fn + " " + ln, ph, em);
            fbs.addCustomer(c);


            AppUser newUser = new AppUser(fn, ln, ph, em, un, password, Role.USER, cid);
            users.put(un.toLowerCase(), newUser);

            store.saveUsers(users.values());
            return newUser;

        } catch (IOException ex) {
            throw new AuthException("Register failed: " + ex.getMessage());
        }
    }
}
