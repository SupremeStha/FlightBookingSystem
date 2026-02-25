package bcu.cmp5332.bookingsystem.model;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a scheduled flight with passenger capacity management.
 * Tracks passenger lists and enforces capacity limits to prevent overbooking.
 * Stores flight details including route, departure date, and pricing information.
 * Supports soft deletion while maintaining referential integrity with existing bookings.
 */

public class Flight {

    private final int id;
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;

    private final int capacity;
    private final double basePrice;

    private boolean deleted = false;

    private final Set<Customer> passengers = new HashSet<>();

    public Flight(int id,
                  String flightNumber,
                  String origin,
                  String destination,
                  LocalDate departureDate,
                  int capacity,
                  double basePrice) {

        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.basePrice = basePrice;
    }
    /**
    * Returns the flightnumber.
     * @return the flightnumber value
    */

    public int getId() { return id; }
    /**
    * Returns the origin.
     * @return the origin value
    */

    public String getFlightNumber() { return flightNumber; }
    /**
    * Returns the destination.
     * @return the destination value
    */

    public String getOrigin() { return origin; }
    /**
    * Returns the departuredate.
     * @return the departuredate value
    */

    public String getDestination() { return destination; }
    /**
    * Returns the capacity.
     * @return the capacity value
    */

    public LocalDate getDepartureDate() { return departureDate; }
    /**
    * Returns the baseprice.
     * @return the baseprice value
    */

    public int getCapacity() { return capacity; }
    /**
    * Retrieves the base price.
    * @return the double result
    */

    public double getBasePrice()
    {
        return basePrice;
    }
    /**
     * Indicates whether this flight has been marked as deleted (soft delete).
     *
     * @return true if the flight is deleted, otherwise false
     */

    public boolean isDeleted() {

        return deleted;
    }
    /**
    * Sets the deleted.
    *
    * @param deleted the deleted value to use
    */

    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }
    /**
    * Returns the seatsleft.
     * @return the seatsleft value
    */


    public int getSeatsLeft() {
        return capacity - passengers.size();
    }
    /**
    * Checks if the specified condition is true.
    * @return the operation result
    */


    public boolean isFull()
    {
        return passengers.size() >= capacity;
    }
    /**
    * Performs the addPassenger operation.
    *
    * @param c the c value
    */


    public void addPassenger(Customer c) throws FlightBookingSystemException {
        if (isFull()) {
            throw new FlightBookingSystemException("Flight is full.");
        }
        passengers.add(c);
    }
    /**
    * Performs the removePassenger operation.
    *
    * @param c the c value
    */


    public void removePassenger(Customer c) {
        passengers.remove(c);
    }
    /**
    * Returns the passengers.
     * @return the passengers value
    */


    public Set<Customer> getPassengers() {
        return passengers;
    }
    /**
    * Returns the detailsshort.
     * @return the detailsshort value
    */


    public String getDetailsShort() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Flight #" + id + " " + flightNumber +
                " (" + origin + " → " + destination + ") " +
                departureDate.format(df);
    }
    /**
    * Returns the detailslong.
     * @return the detailslong value
    */


    public String getDetailsLong() {
        return getDetailsShort()
                + "\nCapacity: " + capacity
                + "\nSeats left: " + getSeatsLeft()
                + "\nBase price: " + basePrice
                + "\nDeleted: " + deleted;
    }

    /**
    * Returns a string representation of this object.
    * @return the string result
    */
    @Override

    public String toString() {
        return "Flight #" + id + " (" + flightNumber + ") "
                + origin + " → " + destination
                + " | " + getDepartureDate();
    }
}
