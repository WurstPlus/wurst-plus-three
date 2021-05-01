package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.elements.Colour;

/**
 * @author megyn
 * @since 28/04/2021
 */

@Hack.Registration(name = "PitbullEsp", description = "makes everyones skin pitbull", category = Hack.Category.RENDER, isListening = false)
public class Pitbull extends Hack {

    public static Pitbull INSTANCE;

    public Pitbull(){
        INSTANCE = this;
    }

    public ColourSetting texture = new ColourSetting("Texture",new Colour(255,255,255, 255), this);

}
