package me.travis.wurstplusthree.gui;

import java.awt.*;

public class Rainbow {

    public static int rgb;
    public static int r;
    public static int g;
    public static int b;

    static float hue = 0.01f;

    public static void updateRainbow() {
        rgb = Color.HSBtoRGB(hue, 0.8f, 0.8f);
        r = (rgb >>> 16) & 0xFF;
        g = (rgb >>> 8) & 0xFF;
        b = rgb & 0xFF;
        hue += 0.0004f;
        if (hue > 1) hue = 0;
    }

}
