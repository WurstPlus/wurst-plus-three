package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.hack.Hack;

@Hack.Registration(name = "SmallShield", description = "trvsf moment", category = Hack.Category.RENDER, isListening = false)
public class SmallShield extends Hack {

    @Override
    public void onUpdate() {
        mc.entityRenderer.itemRenderer.equippedProgressOffHand = -1;
    }

}
