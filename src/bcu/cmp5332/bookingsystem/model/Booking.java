package bcu.cmp5332.bookingsystem.model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a flight booking with support for both one-way and round-trip reservations.
 * Tracks booking status throughout its lifecycle (ACTIVE, CANCELLED, COMPLETED).
 * Maintains a complete history trail of all status changes and modifications.
 * Calculates and stores pricing information including fees for cancellations and changes.
 */

public class Booking {

    private final int id;
    private final Customer customer;

    private Flight outboundFlight;
    private Flight returnFlight;  // Can be null for one-way bookings
    
    private final LocalDate bookingDate;
    private BookingStatus status;
    private final double pricePaid;
    private double feeCharged;
    private LocalDate lastUpdated;

    // History trail
    private final List<String> history = new ArrayList<>();
    /**
    * Full constructor for round-trip bookings
    */

    public Booking(int id,
                   Customer customer,
                   Flight outboundFlight,
                   Flight returnFlight,
                   LocalDate bookingDate,
                   double pricePaid,
                   double feeCharged,
                   BookingStatus status,
                   LocalDate lastUpdated) {

        this.id = id;
        this.customer = customer;
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
        this.bookingDate = bookingDate;
        this.pricePaid = pricePaid;
        this.feeCharged = feeCharged;
        this.status = status;
        this.lastUpdated = (lastUpdated == null ? bookingDate : lastUpdated);

        // Default history for new bookings
        this.history.add("BOOKED");
    }
    /**
    * Constructor for one-way bookings
    */

    public Booking(int id, Customer customer, Flight outboundFlight, LocalDate bookingDate, double pricePaid) {
        this(id, customer, outboundFlight, null, bookingDate, pricePaid, 0.0, BookingStatus.ACTIVE, bookingDate);
    }
    /**
    * Constructor for round-trip bookings
    */

    public Booking(int id, Customer customer, Flight outboundFlight, Flight returnFlight, 
                   LocalDate bookingDate, double pricePaid) {
        this(id, customer, outboundFlight, returnFlight, bookingDate, pricePaid, 0.0, BookingStatus.ACTIVE, bookingDate);
    }

    // Getters
    /**
    * Returns the customer.
     * @return the customer value
    */

    public int getId() { return id; }
    /**
    * Returns all customer.
     * @return list of customer, never null
    */

    public Customer getCustomer() { return customer; }
    
    /**
    * Returns the returnflight.
     * @return the returnflight value
    */

    public Flight getOutboundFlight() { return outboundFlight; }
    /**
    * Retrieves the return flight.
    * @return the flight result
    */

    public Flight getReturnFlight() { return returnFlight; }
    
    // Backward compatibility - returns outbound flight
    @Deprecated
    /**
    * Returns all flight.
     * @return list of flight, never null
    */

    public Flight getFlight() { return outboundFlight; }
    /**
    * Checks if the specified condition is true.
    * @return the operation result
    */

    
    public boolean isRoundTrip() { return returnFlight != null; }
    
    /**
    * Returns the status.
     * @return the status value
    */

    public LocalDate getBookingDate() { return bookingDate; }
    /**
    * Returns the pricepaid.
     * @return the pricepaid value
    */

    public BookingStatus getStatus() { return status; }
    /**
    * Returns the feecharged.
     * @return the feecharged value
    */

    public double getPricePaid() { return pricePaid; }
    /**
    * Returns the lastupdated.
     * @return the lastupdated value
    */

    public double getFeeCharged() { return feeCharged; }
    /**
    * Retrieves the last updated.
    * @return the localdate result
    */

    public LocalDate getLastUpdated() { return lastUpdated; }

    // Setters
    /**
    * Sets the outbound flight.
    *
    * @param flight the flight value to use
    */

    public void setOutboundFlight(Flight flight) {
        this.outboundFlight = flight;
    }
    /**
    * Sets the returnflight.
    *
    * @param flight the flight value
    */


    public void setReturnFlight(Flight flight) {
        this.returnFlight = flight;
    }

    // Backward compatibility
    @Deprecated
    /**
    * Sets the flight.
    *
    * @param newFlight the newFlight value to use
    */

    public void setFlight(Flight newFlight) {
        this.outboundFlight = newFlight;
    }
    /**
    * Sets the status.
    *
    * @param status the status value
    */


    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    /**
    * Sets the feecharged.
    *
    * @param feeCharged the feeCharged value
    */


    public void setFeeCharged(double feeCharged) {
        this.feeCharged = feeCharged;
    }
    /**
    * Performs the touch operation.
    *
    * @param date the date value
    */


    public void touch(LocalDate date) {
        if (date != null) this.lastUpdated = date;
    }

    // History methods
    /**
    * Adds a new history to the collection.
    *
    * @param event the event value to use
    */

    public void addHistory(String event) {
        if (event == null) return;
        String e = event.trim();
        if (e.isEmpty()) return;
        history.add(e);
    }
    /**
    * Returns the history.
     * @return the history value
    */


    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }
    /**
    * Returns the historystring.
     * @return the historystring value
    */


    public String getHistoryString() {
        return String.join(" -> ", history);
    }
    /**
    * Performs the replaceHistory operation.
    *
    * @param newHistory the newHistory value
    */


    public void replaceHistory(List<String> newHistory) {
        history.clear();
        if (newHistory == null || newHistory.isEmpty()) {
            history.add("BOOKED");
            return;
        }
        history.addAll(newHistory);
    }
    /**
    * Returns the details.
     * @return the details value
    */


    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking #").append(id)
          .append(" | customer #").append(customer.getId())
          .append(" | outbound flight #").append(outboundFlight.getId());
        
        if (returnFlight != null) {
            sb.append(" | return flight #").append(returnFlight.getId());
        } else {
            sb.append(" | (one-way)");
        }
        
        sb.append(" | booked: ").append(bookingDate)
          .append(" | lastUpdated: ").append(lastUpdated)
          .append(" | status: ").append(status)
          .append(" | paid: ").append(String.format("%.2f", pricePaid))
          .append(" | fee: ").append(String.format("%.2f", feeCharged))
          .append(" | history: ").append(getHistoryString());
        
        return sb.toString();
    }
}
