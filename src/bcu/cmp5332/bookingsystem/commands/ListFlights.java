package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.util.List;

/**
* Represents a ListFlights in the flight booking system.
*/

public class ListFlights implements Command {

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        fbs.refreshCompletedBookings();

        List<Flight> flights = fbs.getActiveFutureFlights();
        if (flights.isEmpty()) {
            System.out.println("No future flights available.");
            return;
        }

        System.out.println("System Date: " + fbs.getSystemDate());
        for (Flight f : flights) {
            double currentPrice = fbs.calculateCurrentPrice(f);
            System.out.println(f.getDetailsShort() +
                    " | seats left: " + f.getSeatsLeft() + "/" + f.getCapacity() +
                    " | price today: " + String.format("%.2f", currentPrice));
        }
    }
}
