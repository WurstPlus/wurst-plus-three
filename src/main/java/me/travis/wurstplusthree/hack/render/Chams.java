package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.util.elements.Colour;

public class Chams extends Hack {

    public static Chams INSTANCE;

    public Chams() {
        super("Chams", "Chams", Category.RENDER, false);
        INSTANCE = this;
    }

    public BooleanSetting textured = new BooleanSetting("Textured", false, this);
    public BooleanSetting coloured = new BooleanSetting("Coloured", false, this);
    public BooleanSetting xqz = new BooleanSetting("XQZ", false, this);

    public ColourSetting colour = new ColourSetting("Colour", new Colour(255, 255, 255, 150), this);
    public ColourSetting hiddenColour = new ColourSetting("Hidden Colour", new Colour(255, 255, 255, 150), this);

}
