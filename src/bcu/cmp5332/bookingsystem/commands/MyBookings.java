package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a MyBookings in the flight booking system.
*/

public class MyBookings implements Command {

    private final int myCustomerId;

    public MyBookings(int myCustomerId) {
        this.myCustomerId = myCustomerId;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Customer c = fbs.getCustomerByID(myCustomerId);

        System.out.println("\n--- My Bookings ---");
        if (c.getBookings().isEmpty()) {
            System.out.println("No bookings.\n");
            return;
        }

        for (Booking b : c.getBookings()) {
            System.out.println(b.getDetails());
        }
        System.out.println();
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() {
        return false;
    }
}
