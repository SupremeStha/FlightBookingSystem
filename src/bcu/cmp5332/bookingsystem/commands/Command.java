package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

 /**
* Base interface for all CLI commands.
* Every command must implement execute().
*/

public interface Command {
     /**
      * Executes the command.
      *
      * @param flightBookingSystem the system to operate on
      * @throws bcu.cmp5332.bookingsystem.main.FlightBookingSystemException if execution fails
      */
    void execute(FlightBookingSystem flightBookingSystem)
            throws FlightBookingSystemException;
     /**
      * Indicates whether executing this command changes system state.
      *
      * @return true if it changes state, otherwise false
      */
    default boolean changesState() {
        return false;
    }
}
