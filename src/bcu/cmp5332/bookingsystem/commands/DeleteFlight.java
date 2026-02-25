package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a DeleteFlight in the flight booking system.
*/

public class DeleteFlight implements Command {

    private final int flightId;

    public DeleteFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    /**
     * Execute.
     *
     * @param fbs Value.
     *
     * @throws FlightBookingSystemException If the operation fails.
     */
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        fbs.deleteFlight(flightId);
        System.out.println("Flight #" + flightId + " deleted (hidden).");
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    /**
     * Changes state.
     *
     * @return Result of the operation.
     */
    public boolean changesState() {
        return true;
    }
}
