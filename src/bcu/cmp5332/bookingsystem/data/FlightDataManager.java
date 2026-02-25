package bcu.cmp5332.bookingsystem.data;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;

/**
* Represents a FlightDataManager in the flight booking system.
*/

public class FlightDataManager implements DataManager {

    private static final String FILE = "resources/data/flights.txt";

    /**
    * Performs load data operation.
    *
    * @param fbs the fbs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        File file = new File(FILE);
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            int lineNo = 1;

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) { lineNo++; continue; }

                String[] p = line.split(SEPARATOR, -1);

                try {
                    int id = Integer.parseInt(p[0]);
                    String number = p[1];
                    String origin = p[2];
                    String destination = p[3];
                    LocalDate date = LocalDate.parse(p[4]);

                    int capacity = (p.length > 5 && !p[5].isEmpty())
                            ? Integer.parseInt(p[5])
                            : 100;

                    double price = (p.length > 6 && !p[6].isEmpty())
                            ? Double.parseDouble(p[6])
                            : 100.0;

                    boolean deleted = (p.length > 7 && !p[7].isEmpty())
                            && Boolean.parseBoolean(p[7]);

                    Flight f = new Flight(id, number, origin, destination, date, capacity, price);
                    f.setDeleted(deleted);

                    fbs.addFlight(f);

                } catch (Exception ex) {
                    throw new FlightBookingSystemException(
                            "Unable to parse flight on line " + lineNo + ": " + ex.getMessage()
                    );
                }

                lineNo++;
            }
        }
    }

    /**
     * Stores flight data from the given booking system into the flights file.
     *
     * @param fbs the booking system to store data from
     * @throws IOException if an I/O error occurs while writing the file
     */
    @Override

    public void storeData(FlightBookingSystem fbs) throws IOException {
        File tmp = new File(FILE + ".tmp");

        try (PrintWriter out = new PrintWriter(new FileWriter(tmp))) {
            for (Flight f : fbs.getAllFlightsIncludingDeleted()) {
                out.println(
                        f.getId() + SEPARATOR +
                                f.getFlightNumber() + SEPARATOR +
                                f.getOrigin() + SEPARATOR +
                                f.getDestination() + SEPARATOR +
                                f.getDepartureDate() + SEPARATOR +
                                f.getCapacity() + SEPARATOR +
                                f.getBasePrice() + SEPARATOR +
                                f.isDeleted()
                );
            }
        }

        File real = new File(FILE);
        if (real.exists() && !real.delete()) {
            throw new IOException("Cannot overwrite " + FILE);
        }

        if (!tmp.renameTo(real)) {
            throw new IOException("Cannot rename temp file to " + FILE);
        }
    }
}
