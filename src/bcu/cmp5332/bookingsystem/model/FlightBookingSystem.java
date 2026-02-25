package bcu.cmp5332.bookingsystem.model;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Core domain model managing all flight booking operations.
 * Maintains collections of customers, flights, and bookings with referential integrity.
 * Provides business logic for creating, updating, and managing bookings with validation.
 * Uses a configurable system date to support testing and simulation scenarios.
 */

public class FlightBookingSystem {

    private LocalDate systemDate = LocalDate.now();

    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();
    private final Map<Integer, Booking> bookings = new TreeMap<>();
    /**
    * Returns the systemdate.
     * @return the systemdate value
    */


    public LocalDate getSystemDate() {
        return systemDate;
    }
    /**
    * Performs the syncSystemDateToNow operation.
    */


    public void syncSystemDateToNow() {
        this.systemDate = LocalDate.now();
        refreshCompletedBookings();
    }
    /**
    * Sets the systemdate.
    *
    * @param newDate the newDate value
    */


    public void setSystemDate(LocalDate newDate) {
        this.systemDate = newDate;
        refreshCompletedBookings();
    }

    // ---------- IDs ----------
    /**
    * Performs next flight id operation.
    * @return the integer value
    */

    public int nextFlightId() {
        return flights.isEmpty() ? 1 : (Collections.max(flights.keySet()) + 1);
    }
    /**
    * Performs the nextCustomerId operation.
    * @return the operation result
    */


    public int nextCustomerId() {
        return customers.isEmpty() ? 1 : (Collections.max(customers.keySet()) + 1);
    }
    /**
    * Performs the nextBookingId operation.
    * @return the operation result
    */


    public int nextBookingId() {
        return bookings.isEmpty() ? 1 : (Collections.max(bookings.keySet()) + 1);
    }

    // ---------- Flights ----------
    /**
     * Returns all non-deleted flights.
     *
     * @return the {@code List<Flight>} of active flights
     */

    public List<Flight> getFlights() {
        return Collections.unmodifiableList(new ArrayList<>(flights.values()));
    }
    /**
    * Returns the allflightsincludingdeleted.
     * @return the allflightsincludingdeleted value
    */


    public List<Flight> getAllFlightsIncludingDeleted() {
        return new ArrayList<>(flights.values());
    }
    /**
    * Returns the activefutureflights.
     * @return the activefutureflights value
    */


    public List<Flight> getActiveFutureFlights() {
        List<Flight> out = new ArrayList<>();
        for (Flight f : flights.values()) {
            if (f.isDeleted()) continue;
            if (!f.getDepartureDate().isBefore(systemDate)) {
                out.add(f);
            }
        }
        return out;
    }
    /**
    * Gets the flightbyid.
    *
    * @param id the id value
    * @return the operation result
    */


    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        Flight f = flights.get(id);
        if (f == null) throw new FlightBookingSystemException("There is no flight with that ID.");
        if (f.isDeleted()) throw new FlightBookingSystemException("That flight is deleted (hidden).");
        return f;
    }
    /**
    * Gets the flightbyidincludingdeleted.
    *
    * @param id the id value
    * @return the operation result
    */


    public Flight getFlightByIDIncludingDeleted(int id) throws FlightBookingSystemException {
        Flight f = flights.get(id);
        if (f == null) throw new FlightBookingSystemException("There is no flight with that ID.");
        return f;
    }
    /**
    * Performs the addFlight operation.
    *
    * @param flight the flight value
    */


    public void addFlight(Flight flight) throws FlightBookingSystemException {
        if (flights.containsKey(flight.getId())) throw new IllegalArgumentException("Duplicate flight ID.");

        for (Flight existing : flights.values()) {
            if (existing.getFlightNumber().equalsIgnoreCase(flight.getFlightNumber())
                    && existing.getDepartureDate().isEqual(flight.getDepartureDate())
                    && !existing.isDeleted()) {
                throw new FlightBookingSystemException(
                        "There is a flight with same number and departure date in the system"
                );
            }
        }
        flights.put(flight.getId(), flight);
    }
    /**
    * Performs the deleteFlight operation.
    *
    * @param flightId the flightId value
    */


    public void deleteFlight(int flightId) throws FlightBookingSystemException {
        Flight f = getFlightByIDIncludingDeleted(flightId);
        f.setDeleted(true);
    }

    // ---------- Customers ----------
    /**
     * Returns all non-deleted customers.
     *
     * @return the {@code List<Customer>} of active customers
     */

    public List<Customer> getActiveCustomers() {
        List<Customer> out = new ArrayList<>();
        for (Customer c : customers.values()) {
            if (!c.isDeleted()) out.add(c);
        }
        return out;
    }
    /**
    * Returns the allcustomersincludingdeleted.
     * @return the allcustomersincludingdeleted value
    */


    public List<Customer> getAllCustomersIncludingDeleted() {
        return new ArrayList<>(customers.values());
    }
    /**
    * Gets the customerbyid.
    *
    * @param id the id value
    * @return the operation result
    */


    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        Customer c = customers.get(id);
        if (c == null) throw new FlightBookingSystemException("There is no customer with that ID.");
        if (c.isDeleted()) throw new FlightBookingSystemException("That customer is deleted (hidden).");
        return c;
    }
    /**
    * Gets the customerbyidincludingdeleted.
    *
    * @param id the id value
    * @return the operation result
    */


    public Customer getCustomerByIDIncludingDeleted(int id) throws FlightBookingSystemException {
        Customer c = customers.get(id);
        if (c == null) throw new FlightBookingSystemException("There is no customer with that ID.");
        return c;
    }
    /**
    * Gets the customerbyemail.
    *
    * @param email the email value
    * @return the operation result
    */


    public Customer getCustomerByEmail(String email) {
        for (Customer c : customers.values()) {
            if (!c.isDeleted() && c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }
        return null;
    }
    /**
    * Checks if the specified condition is true.
    *
    * @param email the email value
    * @return the operation result
    */


    private boolean isEmailUnique(String email) {
        return getCustomerByEmail(email) == null;
    }
    /**
    * Checks if the specified condition is true.
    *
    * @param phone the phone value
    * @return the operation result
    */


    private boolean isValidPhone10(String phone) {
        if (phone == null) return false;
        String digits = phone.replaceAll("\\D", "");
        return digits.length() == 10;
    }
    /**
    * Performs the addCustomer operation.
    *
    * @param customer the customer value
    */


    public void addCustomer(Customer customer) throws FlightBookingSystemException {
        if (customers.containsKey(customer.getId())) throw new IllegalArgumentException("Duplicate customer ID.");

        String name = customer.getName();
        String phone = customer.getPhone();
        String email = customer.getEmail();

        if (name.isEmpty()) throw new FlightBookingSystemException("Customer name is required.");
        if (email.isEmpty()) throw new FlightBookingSystemException("Email is required.");
        if (!email.contains("@") || !email.contains(".")) {
            throw new FlightBookingSystemException("Email format is invalid.");
        }

        if (!isValidPhone10(phone)) {
            throw new FlightBookingSystemException("Phone must be exactly 10 digits.");
        }

        if (!isEmailUnique(email)) {
            throw new FlightBookingSystemException("A customer with this email already exists.");
        }

        customers.put(customer.getId(), customer);
    }
    /**
    * Performs the deleteCustomer operation.
    *
    * @param customerId the customerId value
    */


    public void deleteCustomer(int customerId) throws FlightBookingSystemException {
        Customer c = getCustomerByIDIncludingDeleted(customerId);
        c.setDeleted(true);
    }

    // ---------- Pricing ----------
    /**
    * Performs calculate current price operation.
    *
    * @param flight the flight value to use
    * @return the double result
    */

    public double calculateCurrentPrice(Flight flight) {
        long daysLeft = ChronoUnit.DAYS.between(systemDate, flight.getDepartureDate());
        if (daysLeft < 0) daysLeft = 0;

        double timeFactor;
        if (daysLeft <= 0) timeFactor = 1.50;
        else if (daysLeft <= 3) timeFactor = 1.40;
        else if (daysLeft <= 7) timeFactor = 1.25;
        else if (daysLeft <= 14) timeFactor = 1.15;
        else timeFactor = 1.05;

        double seatsLeft = flight.getSeatsLeft();
        double cap = Math.max(1, flight.getCapacity());
        double fill = 1.0 - (seatsLeft / cap);
        double scarcityFactor = 1.0 + (fill * 0.50);

        double price = flight.getBasePrice() * timeFactor * scarcityFactor;
        return Math.round(price * 100.0) / 100.0;
    }

    // ---------- Bookings ----------
    /**
     * Returns all bookings.
     *
     * @return the {@code List<Booking>} of bookings
     */

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }
    /**
    * Gets the bookingbyid.
    *
    * @param bookingId the bookingId value
    * @return the operation result
    */


    public Booking getBookingById(int bookingId) throws FlightBookingSystemException {
        Booking b = bookings.get(bookingId);
        if (b == null) throw new FlightBookingSystemException("No booking with that ID.");
        return b;
    }
    /**
    * Performs the addBookingFromData operation.
    *
    * @param b the b value
    */


    public void addBookingFromData(Booking b) throws FlightBookingSystemException {
        bookings.put(b.getId(), b);

        Customer c = b.getCustomer();
        Flight outbound = b.getOutboundFlight();

        if (!c.getBookings().contains(b)) {
            c.addBooking(b);
        }

        if (!outbound.getPassengers().contains(c)) {
            outbound.addPassenger(c);
        }

        if (b.isRoundTrip()) {
            Flight returnFlight = b.getReturnFlight();
            if (!returnFlight.getPassengers().contains(c)) {
                returnFlight.addPassenger(c);
            }
        }
    }
    /**
    * Checks if the specified condition is true.
    *
    * @param customerId the customerId value
    * @param flightId the flightId value
    * @return the operation result
    */


    public Booking issueBooking(int customerId, int flightId) throws FlightBookingSystemException {
        return issueBooking(customerId, flightId, null);
    }
 /**
    * Checks if the specified condition is true.
    *
    * @param customerId the customerId value
    * @param outboundFlightId the outboundFlightId value
    * @param returnFlightId the returnFlightId value
    * @return the operation result
    */


    public Booking issueBooking(int customerId, int outboundFlightId, Integer returnFlightId)
            throws FlightBookingSystemException {

        Customer c = getCustomerByID(customerId);
        Flight outbound = getFlightByID(outboundFlightId);

        // DOUBLE-BOOKING PROTECTION: Check Outbound
        if (c.isAlreadyOnFlight(outboundFlightId)) {
            throw new FlightBookingSystemException("Customer is already on flight #" + outboundFlightId);
        }

        if (outbound.getDepartureDate().isBefore(systemDate)) {
            throw new FlightBookingSystemException("Cannot book a departed flight.");
        }
        if (outbound.isFull()) {
            throw new FlightBookingSystemException("Outbound flight is at full capacity.");
        }

        Flight returnFlight = null;
        double totalPrice = calculateCurrentPrice(outbound);

        if (returnFlightId != null) {
            returnFlight = getFlightByID(returnFlightId);

            // DOUBLE-BOOKING PROTECTION: Check Return
            if (c.isAlreadyOnFlight(returnFlightId)) {
                throw new FlightBookingSystemException("Customer is already on return flight #" + returnFlightId);
            }

            if (returnFlight.getDepartureDate().isBefore(systemDate)) {
                throw new FlightBookingSystemException("Cannot book a departed return flight.");
            }
            if (returnFlight.isFull()) {
                throw new FlightBookingSystemException("Return flight is at full capacity.");
            }

            // --- VALIDATION: Location & Date ---
            if (!returnFlight.getDepartureDate().isAfter(outbound.getDepartureDate())) {
                throw new FlightBookingSystemException("Return flight must depart after outbound flight.");
            }

            if (!returnFlight.getOrigin().equalsIgnoreCase(outbound.getDestination())) {
                throw new FlightBookingSystemException("Return flight must start from " + outbound.getDestination());
            }

            if (!returnFlight.getDestination().equalsIgnoreCase(outbound.getOrigin())) {
                throw new FlightBookingSystemException("Return flight must return to " + outbound.getOrigin());
            }

            totalPrice += calculateCurrentPrice(returnFlight);
            totalPrice = Math.round(totalPrice * 100.0) / 100.0;
        }

        //TRANSACTION: Use transaction for atomic booking creation with rollback
        int bookingId = nextBookingId();
        final Booking b = new Booking(bookingId, c, outbound, returnFlight, systemDate, totalPrice);
        b.touch(systemDate);

        boolean outboundAdded = false;
        boolean returnAdded = false;
        boolean bookingAddedToCustomer = false;
        boolean bookingAddedToMap = false;

        try {
            outbound.addPassenger(c);
            outboundAdded = true;

            if (returnFlight != null) {
                returnFlight.addPassenger(c);
                returnAdded = true;
            }

            c.addBooking(b);
            bookingAddedToCustomer = true;

            bookings.put(b.getId(), b);
            bookingAddedToMap = true;

            return b;

        } catch (Exception e) {
            try {
                if (bookingAddedToMap) {
                    bookings.remove(b.getId());
                }
                if (bookingAddedToCustomer) {
                    c.removeBooking(b);
                }
                if (returnAdded && returnFlight != null) {
                    returnFlight.removePassenger(c);
                }
                if (outboundAdded) {
                    outbound.removePassenger(c);
                }
            } catch (Exception rollbackEx) {
                System.err.println("Warning: Rollback failed: " + rollbackEx.getMessage());
            }

            throw new FlightBookingSystemException("Failed to create booking: " + e.getMessage());
        }
    }
    /**
    * Performs the cancelBooking operation.
    *
    * @param customerId the customerId value
    * @param bookingId the bookingId value
    * @return the operation result
    */


    public Booking cancelBooking(int customerId, int bookingId) throws FlightBookingSystemException {
        return cancelBookingInternal(customerId, bookingId, false, "CANCELLED");
    }
    /**
    * Performs the voidBookingByAdmin operation.
    *
    * @param customerId the customerId value
    * @param bookingId the bookingId value
    */


    public Booking voidBookingByAdmin(int customerId, int bookingId) throws FlightBookingSystemException {
        return cancelBookingInternal(customerId, bookingId, true, "VOIDED_BY_ADMIN");
    }
 /**
  * Performs the cancelBookingInternal operation.
    *
    * @param customerId the customerId value
    * @param bookingId the bookingId value
    * @param waiveFee the waiveFee value
    * @param historyTag the historyTag value
    * @return the operation result
    */


    private Booking cancelBookingInternal(int customerId, int bookingId, boolean waiveFee, String historyTag)
            throws FlightBookingSystemException {
        syncSystemDateToNow();

        Booking b = findBookingByIdAndCustomer(customerId, bookingId);

        final BookingStatus originalStatus = b.getStatus();
        final double originalFee = b.getFeeCharged();
        final List<String> originalHistory = new ArrayList<>(b.getHistory());
        final LocalDate originalLastUpdated = b.getLastUpdated();

        boolean statusUpdated = false;
        boolean outboundRemoved = false;
        boolean returnRemoved = false;

        try {
            final double finalFee;
            if (waiveFee) {
                finalFee = 0.0;
            } else {
                double fee = Math.max(10.0, b.getPricePaid() * 0.10);
                finalFee = Math.round(fee * 100.0) / 100.0;
            }

            b.setFeeCharged(finalFee);
            b.setStatus(BookingStatus.CANCELLED);
            b.addHistory(historyTag);
            b.touch(systemDate);
            statusUpdated = true;

            b.getOutboundFlight().removePassenger(b.getCustomer());
            outboundRemoved = true;

            if (b.isRoundTrip()) {
                b.getReturnFlight().removePassenger(b.getCustomer());
                returnRemoved = true;
            }

            return b;

        } catch (Exception e) {
            try {
                if (returnRemoved && b.isRoundTrip()) {
                    b.getReturnFlight().addPassenger(b.getCustomer());
                }
                if (outboundRemoved) {
                    b.getOutboundFlight().addPassenger(b.getCustomer());
                }
                if (statusUpdated) {
                    b.setFeeCharged(originalFee);
                    b.setStatus(originalStatus);
                    b.replaceHistory(originalHistory);
                    b.touch(originalLastUpdated);
                }
            } catch (Exception rollbackEx) {
                System.err.println("Warning: Rollback failed: " + rollbackEx.getMessage());
            }

            throw new FlightBookingSystemException("Failed to cancel booking: " + e.getMessage());
        }
    }
 /**
  * Performs the updateBooking operation.
    *
    * @param customerId the customerId value
    * @param bookingId the bookingId value
    * @param newOutboundFlightId the newOutboundFlightId value
    * @param newReturnFlightId the newReturnFlightId value
    * @return the operation result
    */


    public Booking updateBooking(int customerId, int bookingId, int newOutboundFlightId, Integer newReturnFlightId)
            throws FlightBookingSystemException {
        syncSystemDateToNow();

        Booking active = findBookingByIdAndCustomer(customerId, bookingId);
        Flight newOutbound = getFlightByID(newOutboundFlightId);

        // DOUBLE-BOOKING PROTECTION for Update Outbound
        if (active.getOutboundFlight().getId() != newOutboundFlightId
                && active.getCustomer().isAlreadyOnFlight(newOutboundFlightId)) {
            throw new FlightBookingSystemException("Customer is already on new flight #" + newOutboundFlightId);
        }

        if (newOutbound.getDepartureDate().isBefore(systemDate)) {
            throw new FlightBookingSystemException("Cannot rebook to a departed flight.");
        }
        if (newOutbound.isFull()) {
            throw new FlightBookingSystemException("New outbound flight is at full capacity.");
        }

        Flight newReturn = null;
        if (newReturnFlightId != null) {
            newReturn = getFlightByID(newReturnFlightId);

            //  DOUBLE-BOOKING PROTECTION for Update Return (handles one-way -> round-trip too)
            Integer oldReturnId = (active.getReturnFlight() == null) ? null : active.getReturnFlight().getId();
            if (oldReturnId == null || oldReturnId != newReturnFlightId) {
                if (active.getCustomer().isAlreadyOnFlight(newReturnFlightId)) {
                    throw new FlightBookingSystemException("Customer is already on new return flight #" + newReturnFlightId);
                }
            }

            if (newReturn.getDepartureDate().isBefore(systemDate)) {
                throw new FlightBookingSystemException("Cannot rebook to a departed return flight.");
            }
            if (newReturn.isFull()) {
                throw new FlightBookingSystemException("New return flight is at full capacity.");
            }

            // --- VALIDATION: Location & Date ---
            if (!newReturn.getDepartureDate().isAfter(newOutbound.getDepartureDate())) {
                throw new FlightBookingSystemException("Return flight must depart after outbound flight.");
            }

            if (!newReturn.getOrigin().equalsIgnoreCase(newOutbound.getDestination())) {
                throw new FlightBookingSystemException("New return flight must start from " + newOutbound.getDestination());
            }

            //  Missing "must return to origin" validation in updateBooking
            if (!newReturn.getDestination().equalsIgnoreCase(newOutbound.getOrigin())) {
                throw new FlightBookingSystemException("New return flight must return to " + newOutbound.getOrigin());
            }
        }

        // Store original state for rollback
        final Flight oldOutbound = active.getOutboundFlight();
        final Flight oldReturn = active.getReturnFlight();
        final double originalFee = active.getFeeCharged();
        final List<String> originalHistory = new ArrayList<>(active.getHistory());
        final LocalDate originalLastUpdated = active.getLastUpdated();

        double fee = Math.max(5.0, active.getPricePaid() * 0.05);
        final double finalFee = Math.round(fee * 100.0) / 100.0;
        final Customer customer = active.getCustomer();

        boolean feeUpdated = false;
        boolean oldOutboundRemoved = false;
        boolean oldReturnRemoved = false;
        boolean newOutboundAdded = false;
        boolean newReturnAdded = false;
        boolean bookingUpdated = false;

        try {
            active.setFeeCharged(finalFee);
            feeUpdated = true;

            oldOutbound.removePassenger(customer);
            oldOutboundRemoved = true;

            if (active.isRoundTrip()) {
                oldReturn.removePassenger(customer);
                oldReturnRemoved = true;
            }

            newOutbound.addPassenger(customer);
            newOutboundAdded = true;

            if (newReturn != null) {
                newReturn.addPassenger(customer);
                newReturnAdded = true;
            }

            active.setOutboundFlight(newOutbound);
            active.setReturnFlight(newReturn);
            active.addHistory("REBOOKED");
            active.touch(systemDate);
            bookingUpdated = true;

            return active;

        } catch (Exception e) {
            try {
                if (bookingUpdated) {
                    active.setOutboundFlight(oldOutbound);
                    active.setReturnFlight(oldReturn);
                    active.replaceHistory(originalHistory);
                    active.touch(originalLastUpdated);
                }
                if (newReturnAdded && newReturn != null) {
                    newReturn.removePassenger(customer);
                }
                if (newOutboundAdded) {
                    newOutbound.removePassenger(customer);
                }
                if (oldReturnRemoved && oldReturn != null) {
                    oldReturn.addPassenger(customer);
                }
                if (oldOutboundRemoved) {
                    oldOutbound.addPassenger(customer);
                }
                if (feeUpdated) {
                    active.setFeeCharged(originalFee);
                }
            } catch (Exception rollbackEx) {
                System.err.println("Warning: Rollback failed: " + rollbackEx.getMessage());
            }

            throw new FlightBookingSystemException("Failed to update booking: " + e.getMessage());
        }
    }
 /**
    * Performs the updateBooking operation.
    *
    * @param customerId the customerId value
    * @param bookingId the bookingId value
    * @param newOutboundFlightId the newOutboundFlightId value
    * @return the operation result
    */


    public Booking updateBooking(int customerId, int bookingId, int newOutboundFlightId) throws FlightBookingSystemException {
        return updateBooking(customerId, bookingId, newOutboundFlightId, null);
    }
    /**
    * Performs the findActiveBooking operation.
    *
    * @param customerId the customerId value
    * @param flightId the flightId value
    * @return the operation result
    */


    private Booking findActiveBooking(int customerId, int flightId) throws FlightBookingSystemException {
        for (Booking b : bookings.values()) {
            if (b.getCustomer().getId() == customerId
                    && b.getOutboundFlight().getId() == flightId
                    && b.getStatus() == BookingStatus.ACTIVE) {
                return b;
            }
        }
        throw new FlightBookingSystemException("No active booking found for that customer and flight.");
    }
    /**
    * Performs the findBookingByIdAndCustomer operation.
    *
    * @param customerId the customerId value
    * @param bookingId the bookingId value
    * @return the operation result
    */


    private Booking findBookingByIdAndCustomer(int customerId, int bookingId)
            throws FlightBookingSystemException {
        Booking b = bookings.get(bookingId);
        if (b == null) throw new FlightBookingSystemException("Booking #" + bookingId + " not found.");
        if (b.getCustomer().getId() != customerId) {
            throw new FlightBookingSystemException("Booking #" + bookingId + " does not belong to customer #" + customerId + ".");
        }
        if (b.getStatus() != BookingStatus.ACTIVE) throw new FlightBookingSystemException("Booking #" + bookingId + " is not active.");
        return b;
    }
    /**
    * Performs the refreshCompletedBookings operation.
    */


    public void refreshCompletedBookings() {
        for (Booking b : bookings.values()) {
            if (b.getStatus() == BookingStatus.ACTIVE) {
                LocalDate completionDate = b.isRoundTrip()
                        ? b.getReturnFlight().getDepartureDate()
                        : b.getOutboundFlight().getDepartureDate();
                if (completionDate.isBefore(systemDate)) {
                    b.setStatus(BookingStatus.COMPLETED);
                    b.addHistory("COMPLETED");
                    b.touch(systemDate);
                }
            }
        }
    }
}
