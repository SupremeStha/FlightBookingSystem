package bcu.cmp5332.bookingsystem.data;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
* Represents a CustomerDataManager in the flight booking system.
*/

public class CustomerDataManager implements DataManager {

    // Keep your existing location
    private static final String RESOURCE = "./resources/data/customers.txt";

    // Use the same separator everywhere (matches your UserStore style)
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
        if (!file.exists()) {
            // no file yet -> nothing to load
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    // Expected (new format):
                    // id::name::phone::email::deleted
                    // But we also accept older formats:
                    // id::name::phone
                    // id::name::phone::email

                    String[] p = line.split(SEP, -1); // -1 keeps empty fields

                    if (p.length < 3) {
                        System.err.println("WARNING: Skipping customer line " + lineNo + " (too few fields): " + line);
                        continue;
                    }

                    int id = Integer.parseInt(p[0].trim());
                    String name = safe(p[1]);
                    String phone = safe(p[2]);

                    String email = (p.length >= 4) ? safe(p[3]) : "";
                    boolean deleted = (p.length >= 5) && Boolean.parseBoolean(p[4].trim());

                    // If email is missing, do NOT crash. Keep placeholder so UI works.
                    if (email.isEmpty()) {
                        // You can also use "" if you prefer, but placeholder makes it obvious in UI.
                        email = "unknown_" + id + "@example.com";
                        System.err.println("WARNING: Customer line " + lineNo + " missing email. Using: " + email);
                    }

                    Customer c = new Customer(id, name, phone, email);
                    c.setDeleted(deleted);

                    fbs.addCustomer(c);

                } catch (Exception ex) {
                    // ✅ Robust: don't kill whole load because one line is wrong
                    System.err.println("WARNING: Skipping invalid customer on line " + lineNo + ": " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Stores customer data from the given booking system into the customers file.
     *
     * @param fbs the booking system to store data from
     * @throws IOException if an I/O error occurs while writing the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        File file = new File(RESOURCE);
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();

        // If you only want active customers saved, use getActiveCustomers().
        // For proper "soft delete" persistence, we should store INCLUDING deleted.
        List<Customer> customers = fbs.getAllCustomersIncludingDeleted();

        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            out.println("# customers.txt");
            out.println("# format: id::name::phone::email::deleted");

            for (Customer c : customers) {
                out.println(
                        c.getId() + SEP +
                                clean(c.getName()) + SEP +
                                clean(c.getPhone()) + SEP +
                                clean(c.getEmail()) + SEP +
                                c.isDeleted()
                );
            }
        }
    }
    /**
    * Performs the safe operation.
    *
    * @param s the s value
    * @return the operation result
    */


    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
    /**
    * Performs the clean operation.
    *
    * @param s the s value
    * @return the operation result
    */


    private static String clean(String s) {
        // prevent breaking the format if someone types "::" in a field
        if (s == null) return "";
        return s.replace(SEP, " ").trim();
    }
}
