package me.travis.wurstplusthree.hack.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.elements.Colour;
import org.lwjgl.input.Keyboard;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class Gui extends Hack{
    public static Gui INSTANCE;

    public ColourSetting buttonColor = new ColourSetting("Button", new Colour(224, 156, 96, 255), this);
    public ColourSetting lineColor = new ColourSetting("Line", new Colour(255, 122, 5, 255), this);
    public ColourSetting fontColor = new ColourSetting("Font", new Colour(255,255,255, 255), this);
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false, this);
    public IntSetting rainbowDelay = new IntSetting("RainbowDelay", 100, 0, 5000, this);
    public IntSetting scrollSpeed = new IntSetting("ScrollSpeed", 15, 1, 100, this);
    public BooleanSetting blur = new BooleanSetting("Blur", true, this);
    public BooleanSetting animation = new BooleanSetting("Animation", true, this);
    public IntSetting animationStages = new IntSetting("AnimationStages", 250, 1, 1000, this);

    public Gui(){
        super("Gui", "swag custom gui", Category.CLIENT, true);
        setBind(Keyboard.KEY_RSHIFT);
        INSTANCE = this;
    }

    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.GUI2);
        this.disable();
    }
}
