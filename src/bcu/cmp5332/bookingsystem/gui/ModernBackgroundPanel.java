package bcu.cmp5332.bookingsystem.gui;


import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
* Represents a ModernBackgroundPanel in the flight booking system.
*/

public class ModernBackgroundPanel extends JPanel {

    private Image bgImg;

    public ModernBackgroundPanel(String resourcePath) {
        setOpaque(true);

        // Try loading from classpath
        URL url = ModernBackgroundPanel.class.getResource(resourcePath);

        if (url != null) {
            bgImg = new ImageIcon(url).getImage();
        } else {
            System.out.println("Background NOT FOUND on classpath: " + resourcePath);
            System.out.println("Make sure it's inside resources/ and resources is marked Resources Root.");
        }
    }

    /**
    * Performs paint component operation.
    *
    * @param g the g value to use
    */
    @Override

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImg != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        } else {
            // fallback gradient
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(
                    0, 0, new Color(25, 28, 40),
                    0, getHeight(), new Color(70, 90, 140)
            ));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
