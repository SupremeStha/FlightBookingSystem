package bcu.cmp5332.bookingsystem.auth;

/**
* Represents a Session in the flight booking system.
*/


public final class Session {

    private static AppUser currentUser;

    private Session(){}
    /**
    * Performs the login operation.
    *
    * @param user the user value
    */


    public static void login(AppUser user) {
        currentUser = user;
    }
    /**
    * Performs the logout operation.
    */


    public static void logout() {
        currentUser = null;
    }
    /**
    * Checks if the specified condition is true.
    * @return the operation result
    */


    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    /**
    * Checks if the specified condition is true.
    * @return the operation result
    */


    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    /**
    * Returns the currentuser.
     * @return the currentuser value
    */


    public static AppUser getCurrentUser() {
        return currentUser;
    }
}
