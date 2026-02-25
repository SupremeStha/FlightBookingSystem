package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Help command that displays role-based help messages.
* Shows different commands based on whether the user is an admin or customer.
*/

public class Help implements Command {

    private AppUser currentUser;
    /**
    * Constructor for Help command with user context.
    *
    * @param user the current authenticated user
    */

    public Help(AppUser user) {
        this.currentUser = user;
    }

    /**
    * Executes the command operation.
    *
    * @param flightBookingSystem the flightBookingSystem value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        if (currentUser != null && currentUser.isAdmin()) {
            System.out.println(getAdminHelpMessage());
        } else if (currentUser != null) {
            // Regular user (customer)
            System.out.println(getCustomerHelpMessage());
        } else {
            // Fallback for no user (shouldn't happen in normal flow)
            System.out.println("Available commands:");
            System.out.println(" login <username> <password>       - login");
            System.out.println(" register                          - register new user");
            System.out.println(" help                              - show this help");
            System.out.println(" exit                              - save and exit");
        }
    }
    /**
    * Returns help message for ADMIN users with all available commands.
    * Updated to include booking-by-ID and round-trip support.
    *
    * @return formatted help message string for admin role
    */

    private String getAdminHelpMessage() {
        return "Commands (ADMIN):\n"
                + "  FLIGHT COMMANDS:\n"
                + "\tlistflights                                      list future flights\n"
                + "\tshowflight [id]                                  show flight details\n"
                + "\taddflight                                        add a flight (interactive)\n"
                + "\tdeleteflight [id]                                hide a flight\n"
                + "\n"
                + "  CUSTOMER COMMANDS:\n"
                + "\tlistcustomers                                    list customers\n"
                + "\tshowcustomer [id]                                show customer details\n"
                + "\taddcustomer                                      add a customer (interactive)\n"
                + "\tdeletecustomer [id]                              hide a customer\n"
                + "\n"
                + "  BOOKING COMMANDS:\n"
                + "\taddbooking [custId] [outFlightId]                add one-way booking\n"
                + "\taddbooking [custId] [outFlightId] [retFlightId]  add round-trip booking\n"
                + "\tupdatebooking [custId] [bookingId] [newFlightId]           update to one-way\n"
                + "\tupdatebooking [custId] [bookingId] [outId] [retId]         update to round-trip\n"
                + "\tcancelbooking [custId] [bookingId]                cancel booking\n"
                + "\tlistbookings                                     list ALL bookings\n"
                + "\tbookinghistory [bookingId]                       view booking history\n"
                + "\n"
                + "  UTILITY COMMANDS:\n"
                + "\thelp                                             show this help message\n"
                + "\tlogout                                           logout and save\n";
    }
    /**
    * Returns help message for CUSTOMER users with limited commands.
    * Updated to include booking-by-ID and round-trip support.
    *
    * @return formatted help message string for customer role
    */

    private String getCustomerHelpMessage() {
        return "Commands (User):\n"
                + "  FLIGHT COMMANDS:\n"
                + "\tlistflights                                      list future flights\n"
                + "\tshowflight [id]                                  show flight details\n"
                + "\n"
                + "  MY ACCOUNT:\n"
                + "\tmydetails                                        show your profile + your ID\n"
                + "\tmybookings                                       list your bookings\n"
                + "\n"
                + "  BOOKING COMMANDS:\n"
                + "\tbook [flightId]                                  book one-way flight\n"
                + "\tbook [outFlightId] [retFlightId]                 book round-trip\n"
                + "\trebook [bookingId] [newFlightId]                 rebook to one-way\n"
                + "\trebook [bookingId] [outId] [retId]               rebook to round-trip\n"
                + "\tcancel [bookingId]                               cancel your booking\n"
                + "\tbookinghistory [bookingId]                       view booking history\n"
                + "\n"
                + "  UTILITY COMMANDS:\n"
                + "\thelp                                             show this help message\n"
                + "\tlogout                                           logout and save\n";
    }
}