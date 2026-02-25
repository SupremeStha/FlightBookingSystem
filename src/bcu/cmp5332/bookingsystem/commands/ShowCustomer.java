package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a ShowCustomer in the flight booking system.
*/

public class ShowCustomer implements Command {

    private final int customerId;

    public ShowCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Customer c = fbs.getCustomerByID(customerId);
        System.out.println(c.getDetailsLong());
        System.out.println("Bookings:");
        for (Booking b : c.getBookings()) {
            System.out.println(" - " + b.getDetails());
        }
    }
}
