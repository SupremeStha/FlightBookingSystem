package bcu.cmp5332.bookingsystem.data;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
* Represents a FlightBookingSystemData in the flight booking system.
*/

public class FlightBookingSystemData {

    private static final List<DataManager> dataManagers = new ArrayList<>();

    static {
        dataManagers.add(new FlightDataManager());
        dataManagers.add(new CustomerDataManager());
        dataManagers.add(new BookingDataManager());
    }

    // Load all data
    /**
    * Performs the load operation.
    * @return the flightbookingsystem result
    * @throws FlightBookingSystemException if an error occurs
    */

    public static FlightBookingSystem load()
            throws IOException, FlightBookingSystemException {

        FlightBookingSystem fbs = new FlightBookingSystem();
        for (DataManager dm : dataManagers) {
            dm.loadData(fbs);
        }
        return fbs;
    }

    // Basic store (skeleton-compatible)
    /**
    * Performs the store operation.
    *
    * @param fbs the fbs value to use
    *
    */

    public static void store(FlightBookingSystem fbs) throws IOException {
        for (DataManager dm : dataManagers) {
            dm.storeData(fbs);
        }
    }
    /**
    * Store data with automatic backup and rollback protection.
    * If the save fails at any point, the previous state is automatically restored.
    *
    * @param fbs The flight booking system to save
    * @throws IOException if both save and rollback fail
    */

    public static void storeAtomic(FlightBookingSystem fbs) throws IOException {
        DataBackup backup = new DataBackup();
        
        try {
            // Create backup before saving
            backup.createBackup();
            
            // Perform the save operation
            store(fbs);
            
            // Ensure data folder exists (safe-guard)
            File dir = new File("resources/data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Commit the backup (marks it as successful)
            backup.commit();
            
        } catch (Exception e) {
            // If save failed, rollback to previous state
            try {
                backup.rollback();
                System.err.println("Save failed. Data restored from backup.");
            } catch (IOException rollbackEx) {
                System.err.println("CRITICAL: Save failed AND rollback failed!");
                System.err.println("Save error: " + e.getMessage());
                System.err.println("Rollback error: " + rollbackEx.getMessage());
                throw new IOException("Save and rollback both failed", e);
            }
            
            // Re-throw original exception
            throw new IOException("Save failed: " + e.getMessage(), e);
        }
    }
}
