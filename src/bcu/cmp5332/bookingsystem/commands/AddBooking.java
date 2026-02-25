package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**

* Usage:
*   addbooking [customerId] [outboundFlightId]              - One-way booking
*   addbooking [customerId] [outboundFlightId] [returnFlightId]  - Round-trip booking
*/

public class AddBooking implements Command {

    private final int customerId;
    private final int outboundFlightId;
    private final Integer returnFlightId;  // null for one-way
    /**
    * Constructor for one-way booking (backward compatible)
    */

    public AddBooking(int customerId, int outboundFlightId) {
        this(customerId, outboundFlightId, null);
    }
    /**
    * Constructor for round-trip booking
    */

    public AddBooking(int customerId, int outboundFlightId, Integer returnFlightId) {
        this.customerId = customerId;
        this.outboundFlightId = outboundFlightId;
        this.returnFlightId = returnFlightId;
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
        fbs.syncSystemDateToNow();
        Booking b = fbs.issueBooking(customerId, outboundFlightId, returnFlightId);
        
        if (returnFlightId == null) {
            System.out.println("One-way booking created: " + b.getDetails());
        } else {
            System.out.println("Round-trip booking created: " + b.getDetails());
        }
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
