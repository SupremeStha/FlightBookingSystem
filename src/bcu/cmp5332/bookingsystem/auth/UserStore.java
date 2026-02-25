package bcu.cmp5332.bookingsystem.auth;


import java.io.*;
import java.util.*;

/**
 * Handles persistence of user account data to file storage.
 * Reads and writes user information in a structured text format.
 * Manages the user database file with proper encoding and error handling.
 * Provides methods to load, save, and update user account information.
 */

public class UserStore {

    private static final String SEP = "::";
    private final File file;

    public UserStore(String path) {
        this.file = new File(path);
    }
    /**
    * Performs the loadUsers operation.
    * @return the operation result
    */


    public Map<String, AppUser> loadUsers() throws IOException {
        Map<String, AppUser> users = new HashMap<>();
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(SEP, -1);
                // fn, ln, phone, email, username, password, role, customerId
                if (p.length != 8) continue;

                Role role;
                try {
                    role = Role.valueOf(p[6]);
                } catch (Exception ex) {
                    continue;
                }

                int cid;
                try {
                    cid = Integer.parseInt(p[7]);
                } catch (Exception ex) {
                    cid = -1;
                }

                AppUser u = new AppUser(
                        p[0], p[1], p[2], p[3],
                        p[4], p[5],
                        role, cid
                );

                users.put(u.getUsername().toLowerCase(), u);
            }
        }
        return users;
    }
    /**
    * Performs the saveUsers operation.
    *
    * @param users the users value
    */


    public void saveUsers(Collection<AppUser> users) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (AppUser u : users) {
                if (u.isAdmin()) continue; // admin is fixed

                out.println(
                        u.getFirstName() + SEP +
                                u.getLastName() + SEP +
                                u.getPhone() + SEP +
                                u.getEmail() + SEP +
                                u.getUsername() + SEP +
                                u.getPassword() + SEP +
                                u.getRole() + SEP +
                                u.getCustomerId()
                );
            }
        }
    }
}
