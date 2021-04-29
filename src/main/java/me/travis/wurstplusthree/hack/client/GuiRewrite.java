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

public class GuiRewrite extends Hack{
    public static GuiRewrite INSTANCE;

    public ColourSetting bg = new ColourSetting("Background", new Colour(207, 121, 45), this);
    public ColourSetting buttonColor = new ColourSetting("Button", new Colour(224, 156, 96), this);
    public ColourSetting lineColor = new ColourSetting("Line", new Colour(255, 122, 5), this);
    public ColourSetting fontColor = new ColourSetting("Font", new Colour(255,255,255), this);
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false, this);
    public IntSetting rainbowDelay = new IntSetting("RainbowDelay", 100, 0, 5000, this);


    public GuiRewrite(){
        super("GuiRewrite", "swag custom gui", Category.CLIENT, true);
        setBind(Keyboard.KEY_0);
        INSTANCE = this;
    }



    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.GUI2);
        this.disable();
    }
}
