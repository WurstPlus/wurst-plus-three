package me.travis.wurstplusthree.hack.hacks.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.elements.Colour;

@Hack.Registration(name = "Hud Editor", description = "swag custom Hud editor", category = Hack.Category.CLIENT, isListening = true)
public class HudEditor extends Hack {
    public static HudEditor INSTANCE;

    public ColourSetting fontColor = new ColourSetting("FontColor", new Colour(255,255,255,255), this);
    public BooleanSetting grid = new BooleanSetting("Grid", false, this);
    public IntSetting vLines = new IntSetting("Vertical Lines", 10, 0, 50, this, s -> grid.getValue());
    public IntSetting hLines = new IntSetting("Horizontal Lines", 10, 0, 50, this, s -> grid.getValue());
    public ColourSetting gridColor = new ColourSetting("Grid Color", new Colour(255,255,255,255), this, s -> grid.getValue());
    public BooleanSetting alignment = new BooleanSetting("Alignment Lines", true ,this);
    public ColourSetting alignmentColor = new ColourSetting("Alignment Color", new Colour(255, 200, 0, 140), this, s -> alignment.getValue());

    public HudEditor(){
        INSTANCE = this;
    }

    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.HUDGUI);
        this.disable();
    }
}
