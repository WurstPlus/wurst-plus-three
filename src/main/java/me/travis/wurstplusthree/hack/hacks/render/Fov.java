package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.IntSetting;

@Hack.Registration(name = "Fov", description = "tabbott mode", category = Hack.Category.RENDER, isListening = false)
public class Fov extends Hack {

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
