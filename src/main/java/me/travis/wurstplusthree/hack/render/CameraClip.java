package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.DoubleSetting;

public class CameraClip extends Hack {

    public static CameraClip INSTANCE;

    public CameraClip() {
        super("Camera Clip", "f5 mode", Category.RENDER,  false);
        INSTANCE = this;
    }

    public DoubleSetting distance = new DoubleSetting("Distance", 10.0, -10.0, 50.0, this);

}
