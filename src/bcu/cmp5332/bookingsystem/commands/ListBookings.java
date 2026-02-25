package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a ListBookings in the flight booking system.
*/

public class ListBookings implements Command {

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    */
    @Override

    public void execute(FlightBookingSystem fbs) {
        if (fbs.getAllBookings().isEmpty()) {
            System.out.println("No bookings.");
            return;
        }

        System.out.println("---- BOOKINGS ----");
        for (Booking b : fbs.getAllBookings()) {
            // Use the updated getDetails() which handles outbound/return logic
            System.out.println(b.getDetails());
        }
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