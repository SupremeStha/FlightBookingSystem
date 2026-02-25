package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;

/**
* Represents a AddFlight in the system.
*/

public class AddFlight implements Command {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;
    private final int capacity;
    private final double price;

    public AddFlight(String flightNumber,
                     String origin,
                     String destination,
                     LocalDate departureDate,
                     int capacity,
                     double price) {

        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
    }

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        int id = fbs.nextFlightId();

        Flight flight = new Flight(
                id,
                flightNumber,
                origin,
                destination,
                departureDate,
                capacity,
                price
        );

        fbs.addFlight(flight);
        System.out.println("Flight #" + id + " added.");
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
