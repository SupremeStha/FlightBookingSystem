package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.auth.Session;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a Logout in the flight booking system.
*/

public class Logout implements Command {
    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    */
    @Override

    public void execute(FlightBookingSystem fbs) {
        Session.logout();
        System.out.println("Logged out.");
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() { return false; }
}
