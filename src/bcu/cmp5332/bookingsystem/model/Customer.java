package bcu.cmp5332.bookingsystem.model;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Customer in the flight booking system.
 * Manages customer information and their bookings.
 */
public class Customer {

    private final int id;
    private String name;
    private String phone;
    private String email;
    private boolean deleted;

    private final List<Booking> bookings = new ArrayList<>();

    /**
     * Creates a new Customer with the specified details.
     *
     * @param id the unique customer ID
     * @param name the customer's name
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public Customer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = validateAndClean(name, "Name");
        this.phone = validateAndClean(phone, "Phone");
        this.email = validateAndClean(email, "Email");
        this.deleted = false;
    }

    /**
     * Retrieves the customer ID.
     *
     * @return the customer ID
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the customer's name.
     *
     * @return the customer's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the customer's phone number.
     *
     * @return the customer's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Retrieves the customer's email address.
     *
     * @return the customer's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Checks if the customer is marked as deleted.
     *
     * @return true if customer is deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deleted status of the customer.
     *
     * @param deleted true to mark as deleted, false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Validates that a value doesn't contain the data file delimiter and returns cleaned value.
     * This prevents data corruption in the file-based storage system.
     *
     * @param value the value value to validate
     * @param fieldName the name of the field being validated (for error messages)
     * @return the trimmed and validated value
     * @throws IllegalArgumentException if the value contains the delimiter "::"
     */
    private String validateAndClean(String value, String fieldName) {
        if (value != null && value.contains("::")) {
            throw new IllegalArgumentException(
                    fieldName + " cannot contain '::' as it would corrupt the data storage."
            );
        }
        return (value == null) ? "" : value.trim();
    }

    /**
     * Sets the customer's name.
     * Validates that the name doesn't contain the data delimiter.
     *
     * @param name the customer's name
     * @throws FlightBookingSystemException if name contains invalid characters
     */
    public void setName(String name) throws FlightBookingSystemException {
        if (name != null && name.contains("::")) {
            throw new FlightBookingSystemException(
                    "Name cannot contain '::' as it would corrupt the data storage."
            );
        }
        this.name = (name == null) ? "" : name.trim();
    }

    /**
     * Sets the customer's phone number.
     * Validates that the phone doesn't contain the data delimiter.
     *
     * @param phone the customer's phone number
     * @throws FlightBookingSystemException if phone contains invalid characters
     */
    public void setPhone(String phone) throws FlightBookingSystemException {
        if (phone != null && phone.contains("::")) {
            throw new FlightBookingSystemException(
                    "Phone cannot contain '::' as it would corrupt the data storage."
            );
        }
        this.phone = (phone == null) ? "" : phone.trim();
    }

    /**
     * Sets the customer's email address.
     * Validates that the email doesn't contain the data delimiter.
     *
     * @param email the customer's email address
     * @throws FlightBookingSystemException if email contains invalid characters
     */
    public void setEmail(String email) throws FlightBookingSystemException {
        if (email != null && email.contains("::")) {
            throw new FlightBookingSystemException(
                    "Email cannot contain '::' as it would corrupt the data storage."
            );
        }
        this.email = (email == null) ? "" : email.trim();
    }

    /**
     * Retrieves an unmodifiable list of all bookings for this customer.
     *
     * @return an unmodifiable list of bookings
     */
    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    /**
     * Adds a booking to this customer's booking list.
     * Prevents duplicates: customer cannot have two ACTIVE bookings for the same trip
     * (same outbound and same return flight status).
     *
     * @param booking the booking value to add
     * @throws FlightBookingSystemException if booking is null or is a duplicate trip
     */
    public void addBooking(Booking booking) throws FlightBookingSystemException {
        if (booking == null) {
            throw new FlightBookingSystemException("Cannot add a null booking.");
        }
        if (containsSameTrip(booking)) {
            throw new FlightBookingSystemException(
                    "Customer already has a booking for the same trip (outbound/return)."
            );
        }
        bookings.add(booking);
    }

    /**
     * Removes a booking from this customer's booking list.
     * Used by cancel/update flows.
     *
     * @param booking the booking value to remove
     * @throws FlightBookingSystemException if booking is null or not found
     */
    public void removeBooking(Booking booking) throws FlightBookingSystemException {
        if (booking == null || !bookings.remove(booking)) {
            throw new FlightBookingSystemException("Booking not found for this customer.");
        }
    }

    /**
     * Finds an active booking that includes the given flight ID as either outbound or return.
     * Useful for supporting cancellation by passing either flight ID.
     *
     * @param flightId the flight ID to search for
     * @return the booking containing the flight, or null if not found
     */
    public Booking findBookingByFlightId(int flightId) {
        for (Booking b : bookings) {
            if (b == null) continue;

            Flight out = b.getOutboundFlight();
            if (out != null && out.getId() == flightId) return b;

            Flight ret = b.getReturnFlight();
            if (ret != null && ret.getId() == flightId) return b;
        }
        return null;
    }

    /**
     * Checks if this customer already has an ACTIVE booking containing the specified trip.
     *
     * Business Rule:
     * Two bookings are considered the same trip if they have:
     * 1. The same outbound flight, AND
     * 2. The same return flight status (both one-way OR both same return flight ID)
     *
     * Only checks ACTIVE bookings, allowing rebooking after cancellation.
     *
     * @param candidate the booking to check for duplication
     * @return true if customer already has this trip booked, false otherwise
     */
    private boolean containsSameTrip(Booking candidate) {
        Flight candOut = candidate.getOutboundFlight();
        Flight candRet = candidate.getReturnFlight();

        int candOutId = (candOut == null) ? -1 : candOut.getId();
        Integer candRetId = (candRet == null) ? null : candRet.getId();
        boolean candHasReturn = (candRet != null);

        for (Booking existing : bookings) {
            if (existing == null) continue;

            // Only check ACTIVE bookings for duplicates
            if (existing.getStatus() != BookingStatus.ACTIVE) {
                continue;
            }

            Flight exOut = existing.getOutboundFlight();
            Flight exRet = existing.getReturnFlight();

            int exOutId = (exOut == null) ? -1 : exOut.getId();
            Integer exRetId = (exRet == null) ? null : exRet.getId();
            boolean exHasReturn = (exRet != null);

            // Check if outbound flights match
            if (candOutId == exOutId) {
                // Both bookings must have the same return flight status
                // Either both are one-way (candHasReturn == exHasReturn == false)
                // OR both are round-trip with same return flight (candRetId.equals(exRetId))
                if (candHasReturn == exHasReturn) {
                    // If neither has a return, it's a duplicate one-way
                    if (!candHasReturn) {
                        return true;
                    }
                    // If both have returns, they must be the same flight
                    if (candRetId != null && candRetId.equals(exRetId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if this customer is already a passenger on the specified flight.
     * Only checks ACTIVE bookings.
     *
     * @param flightId the flight ID to check
     * @return true if customer is on the flight, false otherwise
     */
    public boolean isAlreadyOnFlight(int flightId) {
        for (Booking existing : bookings) {
            if (existing == null) continue;
            if (existing.getStatus() != BookingStatus.ACTIVE) continue;

            Flight out = existing.getOutboundFlight();
            if (out != null && out.getId() == flightId) {
                return true;
            }

            Flight ret = existing.getReturnFlight();
            if (ret != null && ret.getId() == flightId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a detailed string representation of the customer.
     *
     * @return detailed customer information
     */
    public String getDetailsLong() {
        return "Customer #" + id
                + "\nName: " + name
                + "\nPhone: " + phone
                + "\nEmail: " + email
                + "\nDeleted: " + deleted
                + "\nBookings: " + bookings.size();
    }

    /**
     * Returns a string representation of the customer.
     *
     * @return customer summary string
     */
    @Override
    public String toString() {
        return "Customer #" + id + " - " + name;
    }
}