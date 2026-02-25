package bcu.cmp5332.bookingsystem.gui;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
* Represents a UIUtil in the flight booking system.
*/

public final class UIUtil {
    private UIUtil(){}
    /**
    * Sets the modernlaf.
    */


    public static void setModernLAF() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
    }
    /**
    * Performs the base operation.
    * @return the operation result
    */

    public static Font h1() { return new Font("SansSerif", Font.BOLD, 20); }
    /**
    * Performs the base operation.
    * @return the font result
    */

    public static Font base() { return new Font("SansSerif", Font.PLAIN, 13); }
    /**
    * Performs the msg operation.
    *
    * @param parent the parent value
    * @param text the text value
    */


    public static void msg(Component parent, String text) {
        JOptionPane.showMessageDialog(parent, text);
    }
    /**
    * Performs the confirm operation.
    *
    * @param parent the parent value
    * @param text the text value
    * @return the operation result
    */


    public static int confirm(Component parent, String text) {
        return JOptionPane.showConfirmDialog(parent, text, "Confirm", JOptionPane.YES_NO_OPTION);
    }
/**
* Restrict a text field to digits only with a maximum length.
* This prevents users from typing/pasting more than maxDigits.
*/

public static void limitToDigits(JTextField field, int maxDigits) {
    if (field == null) return;
    if (maxDigits < 1) return;

    if (field.getDocument() instanceof AbstractDocument) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DigitLimitFilter(maxDigits));
    }
}
/**
* Represents a DigitLimitFilter in the system.
*/

private static final class DigitLimitFilter extends DocumentFilter {
    private final int maxDigits;

    private DigitLimitFilter(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    /**
    * Performs insert string operation.
    *
    * @param fb the fb value to use
    * @param offset the offset value to use
    * @param string the string value to use
    * @param attr the attr value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null) return;
        replace(fb, offset, 0, string, attr);
    }

    /**
     * Performs the replace operation.
    *
    * @param fb the fb value to use
    * @param offset the offset value to use
    * @param length the length value to use
    * @param text the text value to use
    * @param attrs the attrs value to use
    * @throws FlightBookingSystemException if an error occurs
    */
    @Override

    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null) {
            super.replace(fb, offset, length, null, attrs);
            return;
        }

        // Digits only
        if (!text.chars().allMatch(Character::isDigit)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        Document doc = fb.getDocument();
        String current = doc.getText(0, doc.getLength());
        StringBuilder sb = new StringBuilder(current);
        sb.replace(offset, offset + length, text);

        if (sb.length() > maxDigits) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        super.replace(fb, offset, length, text, attrs);
    }
}
}
