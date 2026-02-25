package bcu.cmp5332.bookingsystem.auth;

/**
 * Represents an authenticated user account in the flight booking system.
 * Stores user credentials, personal information, and role-based permissions.
 * Links user accounts to customer records for integrated booking management.
 * Supports different user roles (ADMIN, USER) for access control.
 */



public class AppUser {
    private final String firstName, lastName, phone, email, username, password;
    private final Role role;
    private final int customerId;


    public AppUser(String firstName, String lastName, String phone, String email,
                   String username, String password, Role role, int customerId) {
        this.firstName = firstName == null ? "" : firstName.trim();
        this.lastName = lastName == null ? "" : lastName.trim();
        this.phone = phone == null ? "" : phone.trim();
        this.email = email == null ? "" : email.trim();
        this.username = username == null ? "" : username.trim();
        this.password = password == null ? "" : password;
        this.role = role;
        this.customerId = customerId;
    }
    /**
    * Performs the admin operation.
    * @return the operation result
    */


    public static AppUser admin() {
        return new AppUser("Admin", "User", "", "", "Admin", "admin123", Role.ADMIN, -1);
    }
    /**
    * Checks if the specified condition is true.
    * @return the operation result
    */


    public boolean isAdmin() { return role == Role.ADMIN; }
    /**
    * Returns the lastname.
     * @return the lastname value
    */

    public String getFirstName() { return firstName; }
    /**
    * Returns the phone.
     * @return the phone value
    */

    public String getLastName() { return lastName; }
    /**
    * Returns the email.
     * @return the email value
    */

    public String getPhone() { return phone; }
    /**
    * Returns the username.
     * @return the username value
    */

    public String getEmail() { return email; }
    /**
    * Returns the password.
     * @return the password value
    */

    public String getUsername() { return username; }
    /**
    * Returns the role.
     * @return the role value
    */

    public String getPassword() { return password; }
    /**
    * Returns the customerid.
     * @return the customerid value
    */

    public Role getRole() { return role; }
    /**
    * Retrieves the customer id.
    * @return the integer value
    */

    public int getCustomerId() { return customerId; }

    /**
    * Returns a string representation of this object.
    * @return the string result
    */
    @Override

    public String toString() {
        return username + " (" + role + ")";
    }
}
