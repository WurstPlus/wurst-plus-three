package me.travis.wurstplusthree.hack.hacks.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;

@Hack.Registration(name = "Hud Editor", description = "swag custom Hud editor", category = Hack.Category.CLIENT, isListening = true)
public class HudEditor extends Hack {
    public static HudEditor INSTANCE;

    public BooleanSetting grid = new BooleanSetting("Grid", true, this);
    public IntSetting vLines = new IntSetting("Vertical Lines", 10, 0, 50, this, s -> grid.getValue());
    public IntSetting hLines = new IntSetting("Horizontal Lines", 10, 0, 50, this, s -> grid.getValue());

    public HudEditor(){
        INSTANCE = this;
    }

    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.HUDGUI);
        this.disable();
    }
}
