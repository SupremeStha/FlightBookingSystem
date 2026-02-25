package bcu.cmp5332.bookingsystem.main;


import bcu.cmp5332.bookingsystem.auth.AppUser;
import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.Session;
import bcu.cmp5332.bookingsystem.auth.UserStore;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.Help;
import bcu.cmp5332.bookingsystem.commands.LoadGUI;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Entry point for the Flight Booking System application.
 * Provides a CLI menu to login/register, and an option to launch the GUI.
 * @author Group C5
 */

public class Main {
    /**
    * Performs the main operation.
    *
    * @param args the args value
    */


    public static void main(String[] args) {

        FlightBookingSystem fbs;
        try {
            fbs = FlightBookingSystemData.load();
        } catch (Exception ex) {
            System.out.println("ERROR: Failed to load data: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        fbs.syncSystemDateToNow();

        AuthService auth = new AuthService(new UserStore("resources/data/users.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                System.out.println("\n======================================");
                System.out.println("   Flight Booking System (CLI)       ");
                System.out.println("======================================");
                System.out.println("1) Login (CLI)");
                System.out.println("2) Register (CLI)");
                System.out.println("3) Open GUI");
                System.out.println("0) Exit");
                System.out.print("Choose: ");

                String choice = br.readLine();
                if (choice == null) break;
                choice = choice.trim();

                if (choice.equals("0")) {
                    FlightBookingSystemData.storeAtomic(fbs);
                    System.out.println("Saved. Goodbye!");
                    return;
                }

                if (choice.equals("2")) {
                    doRegisterCLI(br, auth, fbs);
                    continue;
                }
                if (choice.equals("3")) {

                    new LoadGUI().execute(fbs);
                    System.out.println(" GUI opened. You can continue using CLI too (or close GUI).");
                    continue;
                }
                if (!choice.equals("1")) {
                    System.out.println("Invalid choice.");
                    continue;
                }

                AppUser user = doLoginCLI(br, auth);
                if (user == null) continue; // login failed; show menu again

                // after login → role-based command loop
                runRoleConsole(br, fbs, user);

                // user logged out; go back to start menu
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
    }
    /**
    * Performs the doRegisterCLI operation.
    *
    * @param br the br value
    * @param auth the auth value
    * @param fbs the fbs value
    */


    private static void doRegisterCLI(BufferedReader br, AuthService auth, FlightBookingSystem fbs) throws IOException {
        System.out.println("\n--- Register (User) ---");
        System.out.print("First Name: "); String fn = br.readLine();
        System.out.print("Last Name: ");  String ln = br.readLine();
        System.out.print("Phone: ");      String phone = br.readLine();
        System.out.print("Email: ");      String email = br.readLine();
        System.out.print("Username: ");   String username = br.readLine();
        System.out.print("Password: ");   String pass = br.readLine();
        System.out.print("Confirm Password: "); String cpass = br.readLine();

        try {
            auth.registerUser(fbs, fn, ln, phone, email, username, pass, cpass);
            System.out.println("Registered successfully. Now login.");
        } catch (AuthService.AuthException ex) {
            System.out.println(" Register failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Register failed: " + ex.getMessage());
        }
    }
    /**
    * Performs the doLoginCLI operation.
    *
    * @param br the br value
    * @param auth the auth value
    * @return the operation result
    */


    private static AppUser doLoginCLI(BufferedReader br, AuthService auth) throws IOException {
        System.out.println("\n--- Login ---");
        System.out.println("Admin login: username=Admin, password=admin123");
        System.out.print("Username: ");
        String u = br.readLine();
        System.out.print("Password: ");
        String p = br.readLine();

        try {
            AppUser user = auth.login(u, p);
            Session.login(user);
            System.out.println("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
            return user;
        } catch (AuthService.AuthException ex) {
            System.out.println("Login failed: " + ex.getMessage());
            return null;
        }
    }
    /**
    * Performs the runRoleConsole operation.
    *
    * @param br the br value
    * @param fbs the fbs value
    * @param user the user value
    */


    private static void runRoleConsole(BufferedReader br, FlightBookingSystem fbs, AppUser user) throws IOException {
        System.out.println("\n--------------------------------------");
        System.out.println("Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
        System.out.println("System Date (auto): " + fbs.getSystemDate());
        System.out.println("--------------------------------------");

        printRoleHelp(user, fbs);

        while (true) {
            try {
                System.out.print(user.getRole() + "> ");
                String line = br.readLine();
                if (line == null) return;

                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("logout")) {
                    FlightBookingSystemData.storeAtomic(fbs);
                    Session.logout();
                    System.out.println("Logged out.");
                    return;
                }

                if (line.equalsIgnoreCase("help")) {
                    printRoleHelp(user, fbs);
                    continue;
                }

                // parse + execute with role restrictions
                Command cmd = CommandParser.parse(line, user);
                fbs.syncSystemDateToNow();
                cmd.execute(fbs);

                if (cmd.changesState()) {
                    FlightBookingSystemData.storeAtomic(fbs);
                }

            } catch (FlightBookingSystemException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    /**
    * Prints role-based help message by executing the Help command.
    * This eliminates code duplication - help messages are only defined in Help.java
    *
    * @param user the current authenticated user
    * @param fbs the flight booking system
    */

    private static void printRoleHelp(AppUser user, FlightBookingSystem fbs) {
        try {
            Help helpCommand = new Help(user);
            helpCommand.execute(fbs);
        } catch (Exception ex) {
            System.out.println("Error displaying help: " + ex.getMessage());
        }
    }
}