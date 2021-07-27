package me.travis.wurstplusthree.hack.hacks.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.elements.Colour;
import org.lwjgl.input.Keyboard;

@Hack.Registration(name = "HudEditor", description = "sxcy hud by wallhacks_", category = Hack.Category.CLIENT, isListening = true, bind = Keyboard.KEY_BACKSLASH)
public class HudEditor extends Hack {
    public static HudEditor INSTANCE;
    public ColourSetting backGround = new ColourSetting("BackGround", new Colour(0, 0, 0, 20), this);
    public ColourSetting outLine = new ColourSetting("OutLine", new Colour(30, 200, 100), this);
    public HudEditor() {
        INSTANCE = this;
    }

    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.HUDEDITOR);
        this.disable();
    }
}
