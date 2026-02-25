package bcu.cmp5332.bookingsystem.gui;


import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
* Represents a SimpleDocumentListener in the flight booking system.
*/

public class SimpleDocumentListener implements DocumentListener {

    private final Runnable onChange;

    public SimpleDocumentListener(Runnable onChange) {
        this.onChange = onChange;
    }
    /**
    * Performs insert update operation.
    *
    * @param e the e value to use
    */
    @Override public void insertUpdate(DocumentEvent e) { onChange.run(); }
    /**
    * Removes the update from the collection.
    *
    * @param e the e value to use
    */
    @Override public void removeUpdate(DocumentEvent e) { onChange.run(); }
    /**
    * Performs changed update operation.
    *
    * @param e the e value to use
    */
    @Override public void changedUpdate(DocumentEvent e) { onChange.run(); }
}
