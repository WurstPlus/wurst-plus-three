package me.travis.wurstplusthree.util.elements;

import me.travis.wurstplusthree.util.elements.Colour;

import java.awt.*;

public class Rainbow {

    public static Color getColour() {
        return Colour.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1);
    }

}
