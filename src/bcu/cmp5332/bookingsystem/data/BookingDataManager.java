package bcu.cmp5332.bookingsystem.data;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
*
* File format (NEW):
* id::custId::outboundFlightId::returnFlightId::bookingDate::pricePaid::feeCharged::status::lastUpdated::history
*
* returnFlightId can be "null" or empty for one-way bookings
*/

public class BookingDataManager implements DataManager {

    public final String RESOURCE = "./resources/data/bookings.txt";
    private static final String SEP = "::";

    /**
    * Performs load data operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {

        File file = new File(RESOURCE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;

                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    String[] p = line.split(SEP, -1);

                    // NEW format (with return flight):
                    // id::custId::outboundFlightId::returnFlightId::bookingDate::pricePaid::feeCharged::status::lastUpdated::history
                    
                    // OLD format (backward compatible):
                    // id::custId::flightId::bookingDate::pricePaid::feeCharged::status::lastUpdated::history

                    // Minimum required fields
                    if (p.length < 4) {
                        throw new FlightBookingSystemException("Not enough fields.");
                    }

                    int bookingId = Integer.parseInt(p[0].trim());
                    int customerId = Integer.parseInt(p[1].trim());
                    
                    // Load customer
                    Customer c = fbs.getCustomerByIDIncludingDeleted(customerId);

                    // Determine if this is NEW format (with return flight) or OLD format
                    // We check if field 3 (index 3) looks like a date or an ID
                    boolean isNewFormat = false;
                    Integer returnFlightId = null;
                    int outboundFlightId;
                    LocalDate bookingDate;

                    // Try to parse field at index 3 as date - if it fails, it's new format
                    try {
                        LocalDate.parse(p[3].trim());
                        // Successfully parsed as date - OLD format
                        isNewFormat = false;
                        outboundFlightId = Integer.parseInt(p[2].trim());
                        bookingDate = LocalDate.parse(p[3].trim());
                    } catch (Exception e) {
                        // Failed to parse as date - NEW format
                        isNewFormat = true;
                        outboundFlightId = Integer.parseInt(p[2].trim());
                        
                        // Parse return flight ID (can be "null" or empty)
                        String returnFlightStr = p[3].trim();
                        if (!returnFlightStr.isEmpty() && !returnFlightStr.equalsIgnoreCase("null")) {
                            returnFlightId = Integer.parseInt(returnFlightStr);
                        }
                        
                        if (p.length < 5) {
                            throw new FlightBookingSystemException("Not enough fields for new format.");
                        }
                        bookingDate = LocalDate.parse(p[4].trim());
                    }

                    // Load flights
                    Flight outboundFlight = fbs.getFlightByIDIncludingDeleted(outboundFlightId);
                    Flight returnFlight = null;
                    if (returnFlightId != null) {
                        returnFlight = fbs.getFlightByIDIncludingDeleted(returnFlightId);
                    }

                    // Defaults
                    double pricePaid = 0.0;
                    double feeCharged = 0.0;
                    BookingStatus status = BookingStatus.ACTIVE;
                    LocalDate lastUpdated = bookingDate;
                    List<String> history = new ArrayList<>();
                    history.add("BOOKED");

                    // Parse remaining fields based on format
                    int startIndex = isNewFormat ? 5 : 4;

                    // Price paid
                    if (p.length > startIndex) {
                        pricePaid = parseDoubleSafe(p[startIndex], 0.0);
                    }

                    // Fee charged or status
                    if (p.length > startIndex + 1) {
                        if (isBookingStatusToken(p[startIndex + 1])) {
                            status = BookingStatus.valueOf(p[startIndex + 1].trim().toUpperCase());
                        } else {
                            feeCharged = parseDoubleSafe(p[startIndex + 1], 0.0);
                        }
                    }

                    // Status or last updated
                    if (p.length > startIndex + 2) {
                        if (isBookingStatusToken(p[startIndex + 2])) {
                            status = BookingStatus.valueOf(p[startIndex + 2].trim().toUpperCase());
                        } else {
                            lastUpdated = parseDateSafe(p[startIndex + 2], lastUpdated);
                        }
                    }

                    // Last updated
                    if (p.length > startIndex + 3) {
                        lastUpdated = parseDateSafe(p[startIndex + 3], lastUpdated);
                    }

                    // History
                    if (p.length > startIndex + 4) {
                        String[] tail = Arrays.copyOfRange(p, startIndex + 4, p.length);
                        String historyStr = String.join(SEP, tail).trim();
                        history = parseHistory(historyStr);
                    }

                    // Build booking with outbound and optional return flight
                    Booking b = new Booking(
                            bookingId,
                            c,
                            outboundFlight,
                            returnFlight,
                            bookingDate,
                            pricePaid,
                            feeCharged,
                            status,
                            lastUpdated
                    );

                    // Set history
                    b.replaceHistory(history);
                    b.touch(lastUpdated);

                    // Add booking to system
                    fbs.addBookingFromData(b);

                } catch (Exception ex) {
                    throw new FlightBookingSystemException(
                            "Unable to parse booking on line " + lineNo + ": " + ex.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Stores booking data from the given booking system into the bookings file.
     *
     * @param fbs the booking system to store data from
     * @throws IOException if an I/O error occurs while writing the file
     */
    @Override

    public void storeData(FlightBookingSystem fbs) throws IOException {

        File file = new File(RESOURCE);
        file.getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (Booking b : fbs.getAllBookings()) {

                // NEW format:
                // id::custId::outboundFlightId::returnFlightId::bookingDate::pricePaid::feeCharged::status::lastUpdated::history
                
                String returnFlightIdStr = b.isRoundTrip() 
                    ? String.valueOf(b.getReturnFlight().getId()) 
                    : "null";

                out.println(
                        b.getId() + SEP +
                        b.getCustomer().getId() + SEP +
                        b.getOutboundFlight().getId() + SEP +
                        returnFlightIdStr + SEP +
                        b.getBookingDate() + SEP +
                        b.getPricePaid() + SEP +
                        b.getFeeCharged() + SEP +
                        b.getStatus().name() + SEP +
                        b.getLastUpdated() + SEP +
                        b.getHistoryString()
                );
            }
        }
    }

    // ---------------- helpers ----------------
    /**
    * Checks if the specified condition is true.
    *
    * @param s the s value
    * @return the operation result
    */


    private static boolean isBookingStatusToken(String s) {
        if (s == null) return false;
        String t = s.trim();
        if (t.isEmpty()) return false;
        for (BookingStatus bs : BookingStatus.values()) {
            if (bs.name().equalsIgnoreCase(t)) return true;
        }
        return false;
    }
    /**
    * Performs the parseDoubleSafe operation.
    *
    * @param s the s value
    * @param def the def value
    * @return the operation result
    */


    private static double parseDoubleSafe(String s, double def) {
        try { return Double.parseDouble(s.trim()); }
        catch (Exception e) { return def; }
    }
    /**
    * Performs the parseDateSafe operation.
    *
    * @param s the s value
    * @param def the def value
    * @return the operation result
    */


    private static LocalDate parseDateSafe(String s, LocalDate def) {
        try { return LocalDate.parse(s.trim()); }
        catch (Exception e) { return def; }
    }
    /**
    * Performs the parseHistory operation.
    *
    * @param historyStr the historyStr value
    * @return the operation result
    */


    private static List<String> parseHistory(String historyStr) {
        if (historyStr == null) return Collections.singletonList("BOOKED");

        String h = historyStr.trim();
        if (h.isEmpty()) return Collections.singletonList("BOOKED");

        // Accept either:
        // "BOOKED -> REBOOKED -> CANCELLED"
        // or "BOOKED->REBOOKED->CANCELLED"
        String[] parts = h.split("\\s*->\\s*");

        List<String> list = new ArrayList<>();
        for (String part : parts) {
            String e = part.trim();
            if (!e.isEmpty()) list.add(e);
        }

        if (list.isEmpty()) list.add("BOOKED");
        return list;
    }
}
