package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.auth.Role;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a BookingHistory in the flight booking system.
*/

public class BookingHistory implements Command {

    private final int bookingId;
    private final AppUser user;

    public BookingHistory(int bookingId, AppUser user) {
        this.bookingId = bookingId;
        this.user = user;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Booking b = fbs.getBookingById(bookingId);

        // User can only view their own booking history
        if (user.getRole() != Role.ADMIN && b.getCustomer().getId() != user.getCustomerId()) {
            throw new FlightBookingSystemException("You can only view your own booking history.");
        }

        System.out.println("\nBooking #" + b.getId());
        System.out.println("Status: " + b.getStatus());
        System.out.println("Last Updated: " + b.getLastUpdated());
        System.out.println("History:");
        int i = 1;
        for (String h : b.getHistory()) {
            System.out.println("  " + (i++) + ". " + h);
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
