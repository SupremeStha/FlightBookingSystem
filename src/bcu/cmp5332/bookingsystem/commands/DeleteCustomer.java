package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a DeleteCustomer in the flight booking system.
*/

public class DeleteCustomer implements Command {

    private final int customerId;

    public DeleteCustomer(int customerId) {
        this.customerId = customerId;
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
        fbs.deleteCustomer(customerId);
        System.out.println("Customer #" + customerId + " deleted (hidden).");
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
