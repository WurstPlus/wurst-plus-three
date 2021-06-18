package me.travis.wurstplusthree.hack.hacks.client;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;

@Hack.Registration(name = "Hud Editor", description = "swag custom Hud editor", category = Hack.Category.CLIENT, isListening = true)
public class HudEditor extends Hack {
    public static HudEditor INSTANCE;

    public BooleanSetting grid = new BooleanSetting("Grid", true, this);
    public IntSetting gridSize = new IntSetting("Grid Size", 50, 20, 150, this, s -> grid.getValue());

    public HudEditor(){
        INSTANCE = this;
    }

    @Override
    public void onEnable(){
        mc.displayGuiScreen(WurstplusThree.HUDGUI);
        this.disable();
    }
}
