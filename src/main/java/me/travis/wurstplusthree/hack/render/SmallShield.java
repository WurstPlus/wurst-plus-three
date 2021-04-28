package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.hack.Hack;

public class SmallShield extends Hack {

    public SmallShield() {
        super("Small Shield", "trvsf moment", Category.RENDER, false);
    }

    @Override
    public void onUpdate() {
        mc.entityRenderer.itemRenderer.equippedProgressOffHand = -1;
    }
}
