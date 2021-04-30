package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.elements.Colour;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 */

public class Pitbull extends Hack {

    public static Pitbull INSTANCE;

    public Pitbull(){
        super("Pitbull", "makes everyones skin pitbull",  Category.MISC, false);
        INSTANCE = this;
    }

    public ColourSetting texture = new ColourSetting("Texture",new Colour(255,255,255, 255), this);

}
