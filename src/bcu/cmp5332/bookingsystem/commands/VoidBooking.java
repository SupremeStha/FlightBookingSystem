package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Admin-only: void a booking without charging a cancellation fee.
*
* Usage:
*   voidbooking [customerId] [bookingId]
*/

public class VoidBooking implements Command {

    private final int customerId;
    private final int bookingId;

    public VoidBooking(int customerId, int bookingId) {
        this.customerId = customerId;
        this.bookingId = bookingId;
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
        Booking b = fbs.voidBookingByAdmin(customerId, bookingId);
        System.out.println("Booking #" + bookingId + " voided (no fee): " + b.getDetails());
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
