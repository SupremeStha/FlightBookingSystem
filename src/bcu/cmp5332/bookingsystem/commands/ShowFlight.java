package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
* Represents a ShowFlight in the flight booking system.
*/

public class ShowFlight implements Command {

    private final int flightId;

    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Flight f = fbs.getFlightByID(flightId);
        System.out.println(f.getDetailsLong());
        System.out.println("Passengers: " + f.getPassengers().size());
        f.getPassengers().forEach(p -> System.out.println(" - " + p.getId() + ": " + p.getName()));
    }
}
