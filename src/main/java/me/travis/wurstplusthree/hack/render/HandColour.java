package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.util.elements.Colour;

public class HandColour extends Hack {

    public static HandColour INSTANCE;

    public HandColour() {
        super("Hand Colour", "colours hands", Category.RENDER, false);
        INSTANCE = this;
    }

    public ColourSetting colour = new ColourSetting("Colour", new Colour(255, 255, 255, 150), this);
    public DoubleSetting alpha = new DoubleSetting("Alpha", 200.0, 0.0, 255.0, this);

}
