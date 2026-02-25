package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to update an existing booking to different flights.
 * Supports changing outbound flight, adding/removing return flights for round trips.
 * Validates flight availability and prevents double-booking conflicts.
 * Applies rebooking fees according to business rules.
 * Maintains referential integrity by updating passenger lists across affected flights.
 * 
 * Usage:
 *   updatebooking [customerId] [bookingId] [newOutboundFlightId]
 *   updatebooking [customerId] [bookingId] [newOutboundFlightId] [newReturnFlightId]
 *
 * For users (rebook command):
 *   rebook [bookingId] [newFlightId]
 *   rebook [bookingId] [newFlightId] [newReturnFlightId]
 */

public class UpdateBooking implements Command {

    private final int customerId;
    private final int bookingId;
    private final int newOutboundFlightId;
    private final Integer newReturnFlightId;  // null for one-way
    /**
    * Constructor for updating a specific booking to one-way
    */

    public UpdateBooking(int customerId, int bookingId, int newOutboundFlightId) {
        this(customerId, bookingId, newOutboundFlightId, null);
    }
    /**
    * Constructor for updating a specific booking with return flight
    */

    public UpdateBooking(int customerId, int bookingId, int newOutboundFlightId, Integer newReturnFlightId) {
        this.customerId = customerId;
        this.bookingId = bookingId;
        this.newOutboundFlightId = newOutboundFlightId;
        this.newReturnFlightId = newReturnFlightId;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Booking b = fbs.updateBooking(customerId, bookingId, newOutboundFlightId, newReturnFlightId);

        if (newReturnFlightId == null) {
            System.out.println("Booking #" + bookingId + " updated to one-way: " + b.getDetails());
        } else {
            System.out.println("Booking #" + bookingId + " updated to round-trip: " + b.getDetails());
        }
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() {
        return true;
    }
}