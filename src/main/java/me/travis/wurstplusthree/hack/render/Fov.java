package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.IntSetting;

public class Fov extends Hack {

    public Fov() {
        super("Fov", "Fov", Category.RENDER, false);
    }

    IntSetting fov = new IntSetting("Fov", 130, 90, 179, this);

    @Override
    public void onUpdate() {
        mc.gameSettings.fovSetting = (float) fov.getValue();
    }
}
