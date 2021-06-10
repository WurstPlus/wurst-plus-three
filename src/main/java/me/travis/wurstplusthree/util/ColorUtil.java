package me.travis.wurstplusthree.util;


import java.awt.*;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 * -> from my client (Glacier)
 */

public class ColorUtil {

    public static Color shadeColour(Color color, int precent) {
        int r = (color.getRed() * (100 + precent) / 100);
        int g = (color.getGreen() * (100 + precent) / 100);
        int b = (color.getBlue() * (100 + precent) / 100);
        return new Color(r,g,b);
    }

    public static int shadeColour(int color, int precent) {
        int r = (((color & 0xFF0000) >> 16) * (100 + precent) / 100);
        int g = (((color & 0xFF00) >> 8) * (100 + precent) / 100);
        int b = ((color & 0xFF) * (100 + precent) / 100);
        return new Color(r,g,b).hashCode();
    }

    public static Color releasedDynamicRainbow(final int delay, int alpha) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360.0;

        Color c = Color.getHSBColor((float) (rainbowState / 360.0), 1f, 1f);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public static Color getSinState(final Color c1, final double delay, final int a, final type t) {
        double sineState;
        sineState = Math.sin(2400 - System.currentTimeMillis() / delay) * Math.sin(2400 - System.currentTimeMillis() / delay);
        float[] hsb = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        Color c = null;
        switch (t) {
            case HUE:
                sineState /= hsb[0];
                sineState = Math.min(1.0, sineState);
                c = Color.getHSBColor((float) sineState, 1f, 1f);
                break;
            case SATURATION:
                sineState /= hsb[1];
                sineState = Math.min(1.0, sineState);
                c = Color.getHSBColor(hsb[0], (float) sineState, 1f);
                break;
            case BRIGHTNESS:
                sineState /= hsb[2];
                sineState = Math.min(1.0, sineState);
                c = Color.getHSBColor(hsb[0], 1f, (float) sineState);
                break;
            case SPECIAL:
                sineState /= hsb[1];
                sineState = Math.min(1.0, sineState);
                c = Color.getHSBColor(hsb[0], 1f, (float) sineState);
                break;
        }
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    public static Color getSinState(final Color c1, final Color c2, final double delay, final int a) {
        double sineState;
        sineState = Math.sin(2400 - System.currentTimeMillis() / delay) * Math.sin(2400 - System.currentTimeMillis() / delay);
        float[] hsb = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
        Color c;
        sineState /= hsb[0];
        sineState *= sineState/hsb2[0];
        sineState = Math.min(1.0, sineState);
        c = Color.getHSBColor((float) sineState, 1f, 1f);

        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }


    public enum type {
        HUE,
        SATURATION,
        BRIGHTNESS,
        SPECIAL
    }


}



