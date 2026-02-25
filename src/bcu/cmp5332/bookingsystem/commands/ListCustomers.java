package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.util.List;

/**
* Represents a ListCustomers in the flight booking system.
*/

public class ListCustomers implements Command {

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        List<Customer> customers = fbs.getActiveCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers available.");
            return;
        }

        for (Customer c : customers) {
            System.out.println("Customer #" + c.getId() + " - " + c.getName() +
                    " | phone: " + c.getPhone() +
                    " | email: " + c.getEmail() +
                    " | bookings: " + c.getBookings().size());
        }
    }
}
