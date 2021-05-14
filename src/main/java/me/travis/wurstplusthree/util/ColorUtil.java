package me.travis.wurstplusthree.util;


import java.awt.*;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 * -> from my client (Glacier)
 */

public class ColorUtil {


    public static Color releasedDynamicRainbow(final int delay, int alpha) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360.0;

       Color c = Color.getHSBColor((float) (rainbowState / 360.0), 1f, 1f);
       return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
    /*
    public static Color getSinColor(final Color c1, final Color c2, final int delay) {

    }
    // max(1, abs(sin x/3)3)-1
     */
}
