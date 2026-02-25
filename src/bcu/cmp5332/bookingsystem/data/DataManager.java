package bcu.cmp5332.bookingsystem.data;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;

/**
* Interface representing DataManager.
*/

public interface DataManager {
    
    public static final String SEPARATOR = "::";

    /**
     * Loads data into the given booking system.
     *
     * @param fbs the system to load data into
     * @throws IOException if an I/O error occurs
     * @throws FlightBookingSystemException if the data is invalid or cannot be loaded
     */

    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException;
    /**
     * Stores the current state of the booking system.
     *
     * @param fbs the system to store
     * @throws IOException if an I/O error occurs
     */


    public void storeData(FlightBookingSystem fbs) throws IOException;
    
}
