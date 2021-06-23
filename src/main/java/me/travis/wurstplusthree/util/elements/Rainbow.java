package me.travis.wurstplusthree.util.elements;

import java.awt.*;

public class Rainbow {

    public static Color getColour() {
        return Colour.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1);
    }

    public static Color getFurtherColour(int offset) {
        return Colour.fromHSB(((System.currentTimeMillis() + offset) % (360 * 32)) / (360f * 32), 1, 1);
    }

}
