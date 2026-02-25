package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**


* Usage:
*   cancel [bookingId]                    - User cancels their booking
*   cancelbooking [customerId] [bookingId] - Admin cancels customer's booking
*/

public class CancelBooking implements Command {

    private final int customerId;
    private final int bookingId;
    /**
    * Constructor for cancelling a specific booking
    */

    public CancelBooking(int customerId, int bookingId) {
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
        Booking b = fbs.cancelBooking(customerId, bookingId);
        System.out.println("Booking #" + bookingId + " cancelled: " + b.getDetails());
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