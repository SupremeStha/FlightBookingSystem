package bcu.cmp5332.bookingsystem.main;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.commands.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

/**
* Represents a CommandParser in the flight booking system.
*/

public class CommandParser {
    /**
    * Performs the parse operation.
    *
    * @param line the line value
    * @param user the user value
    * @return the operation result
    */


    public static Command parse(String line, AppUser user) throws IOException, FlightBookingSystemException {
        line = line.trim();
        if (line.isEmpty()) throw new FlightBookingSystemException("Invalid command.");

        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        // ---------------- 1 ARGUMENT (Commands with no parameters) ----------------
        if (parts.length == 1) {
            switch (cmd) {
                case "listflights": return new ListFlights();
                case "listbookings":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new ListBookings();
                case "mybookings":
                    if (user.isAdmin()) throw new FlightBookingSystemException("This is for users only.");
                    return new MyBookings(user.getCustomerId());
                case "mydetails":
                    if (user.isAdmin()) throw new FlightBookingSystemException("This is for users only.");
                    return new MyDetails(user.getCustomerId());
                case "listcustomers":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new ListCustomers();
                case "addflight":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return interactiveAddFlight();
                case "addcustomer":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return interactiveAddCustomer();
                default:
                    throw new FlightBookingSystemException("Invalid command.");
            }
        }

        // ---------------- 2 ARGUMENTS (Commands with 1 parameter) ----------------
        if (parts.length == 2) {
            int id = parseId(parts[1]);
            switch (cmd) {
                case "showflight": return new ShowFlight(id);
                case "showcustomer":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new ShowCustomer(id);
                case "deleteflight":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new DeleteFlight(id);
                case "deletecustomer":
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new DeleteCustomer(id);
                case "book":
                    if (user.isAdmin()) throw new FlightBookingSystemException("Use addbooking as admin.");
                    return new AddBooking(user.getCustomerId(), id);
                case "cancel":
                    if (user.isAdmin()) throw new FlightBookingSystemException("Use cancelbooking as admin.");
                    return new CancelBooking(user.getCustomerId(), id);
                case "bookinghistory":
                    return new BookingHistory(id, user);
                default:
                    throw new FlightBookingSystemException("Invalid command.");
            }
        }

        // ---------------- 3 ARGUMENTS ----------------
        if (parts.length == 3) {
            int a = parseId(parts[1]);
            int b = parseId(parts[2]);

            switch (cmd) {
                case "book": // USER: book [outboundId] [returnId]
                    if (user.isAdmin()) throw new FlightBookingSystemException("Use addbooking as admin.");
                    return new AddBooking(user.getCustomerId(), a, b);
                case "rebook": // USER: rebook [bookingId] [newFlightId]
                    if (user.isAdmin()) throw new FlightBookingSystemException("Use updatebooking as admin.");
                    return new UpdateBooking(user.getCustomerId(), a, b);
                case "addbooking": // ADMIN: addbooking [customerId] [flightId]
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new AddBooking(a, b);
                case "cancelbooking": // ADMIN: cancelbooking [customerId] [bookingId]
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new CancelBooking(a, b);
                case "voidbooking": // ADMIN: voidbooking [customerId] [bookingId] (no fee)
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new VoidBooking(a, b);
                default:
                    throw new FlightBookingSystemException("Invalid command.");
            }
        }

        // ---------------- 4 ARGUMENTS ----------------
        if (parts.length == 4) {
            int a = parseId(parts[1]);
            int b = parseId(parts[2]);
            int c = parseId(parts[3]);

            switch (cmd) {
                case "addbooking": // ADMIN: addbooking [customerId] [outboundId] [returnId]
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new AddBooking(a, b, c);
                case "updatebooking": // ADMIN: updatebooking [customerId] [bookingId] [newOutboundId]
                    if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                    return new UpdateBooking(a, b, c);
                case "rebook": // USER: rebook [bookingId] [outboundId] [returnId]
                    if (user.isAdmin()) throw new FlightBookingSystemException("Use updatebooking as admin.");
                    return new UpdateBooking(user.getCustomerId(), a, b, c);
                default:
                    throw new FlightBookingSystemException("Invalid command.");
            }
        }

        // ---------------- 5 ARGUMENTS ----------------
        if (parts.length == 5) {
            if (cmd.equals("updatebooking")) { // ADMIN: updatebooking [cId] [bId] [outId] [retId]
                if (!user.isAdmin()) throw new FlightBookingSystemException("Admin only.");
                int cId = parseId(parts[1]);
                int bId = parseId(parts[2]);
                int outId = parseId(parts[3]);
                int retId = parseId(parts[4]);
                return new UpdateBooking(cId, bId, outId, retId);
            }
        }

        throw new FlightBookingSystemException("Invalid command or argument count.");
    }
    /**
    * Performs the parseId operation.
    *
    * @param s the s value
    * @return the operation result
    */


    private static int parseId(String s) throws FlightBookingSystemException {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid numeric ID: " + s);
        }
    }
    /**
    * Performs the interactiveAddFlight operation.
    * @return the operation result
    */


    private static Command interactiveAddFlight() throws IOException, FlightBookingSystemException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Flight Number: ");
        String flightNumber = reader.readLine().trim();
        System.out.print("Origin: ");
        String origin = reader.readLine().trim();
        System.out.print("Destination: ");
        String destination = reader.readLine().trim();

        LocalDate departureDate;
        try {
            System.out.print("Departure Date (YYYY-MM-DD): ");
            departureDate = LocalDate.parse(reader.readLine().trim());
        } catch (Exception ex) {
            throw new FlightBookingSystemException("Invalid date format. Use YYYY-MM-DD.");
        }

        int capacity;
        try {
            System.out.print("Capacity: ");
            capacity = Integer.parseInt(reader.readLine().trim());
            if (capacity <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            throw new FlightBookingSystemException("Capacity must be a positive integer.");
        }

        double price;
        try {
            System.out.print("Base Price: ");
            price = Double.parseDouble(reader.readLine().trim());
            if (price <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            throw new FlightBookingSystemException("Price must be a positive number.");
        }

        return new AddFlight(flightNumber, origin, destination, departureDate, capacity, price);
    }
    /**
    * Performs the interactiveAddCustomer operation.
    * @return the operation result
    */


    private static Command interactiveAddCustomer() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("--- Add Customer Account (Admin) ---");
        System.out.print("First name: ");
        String first = reader.readLine().trim();

        System.out.print("Last name: ");
        String last = reader.readLine().trim();

        System.out.print("Phone (10 digits): ");
        String phone = reader.readLine().trim();

        System.out.print("Email: ");
        String email = reader.readLine().trim();

        System.out.print("Username: ");
        String username = reader.readLine().trim();

        System.out.print("Password: ");
        String pass = reader.readLine();

        System.out.print("Confirm password: ");
        String confirm = reader.readLine();

        return new AddCustomer(first, last, phone, email, username, pass, confirm);
    }
}