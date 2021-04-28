package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.IntSetting;

public class Fov extends Hack {

    public Fov() {
        super("Fov", "tabbott mode", Category.RENDER, false);
    }

    IntSetting fov = new IntSetting("Fov", 130, 90, 179, this);
    float fovOld;

    @Override
    public void onEnable(){
        fovOld = mc.gameSettings.fovSetting;
    }

    @Override
    public void onUpdate() {
        mc.gameSettings.fovSetting = (float) fov.getValue();
    }

    @Override
    public void onDisable(){
        mc.gameSettings.fovSetting = fovOld;
    }
}
