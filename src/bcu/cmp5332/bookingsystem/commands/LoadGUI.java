package bcu.cmp5332.bookingsystem.commands;


import bcu.cmp5332.bookingsystem.gui.LoginWindow;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.SwingUtilities;

/**
* Represents a LoadGUI in the flight booking system.
*/

public class LoadGUI implements Command {

    /**
    * Executes the command operation.
    *
    * @param fbs the fbs value to use
    */
    @Override

    public void execute(FlightBookingSystem fbs) {
        SwingUtilities.invokeLater(() -> new LoginWindow(fbs));
    }

    /**
    * Performs changes state operation.
    * @return the boolean result
    */
    @Override

    public boolean changesState() {
        return false;
    }
}

