package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a MyDetails in the flight booking system.
*/

public class MyDetails implements Command {

    private final int myCustomerId;

    public MyDetails(int myCustomerId) {
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

        System.out.println("\n--- My Profile ---");
        System.out.println("Customer ID: " + c.getId());
        System.out.println("Name: " + c.getName());
        System.out.println("Phone: " + c.getPhone());
        System.out.println("Email: " + c.getEmail());
        System.out.println("Bookings: " + c.getBookings().size());
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
