package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;

public class Sprint extends Hack {

    public Sprint() {
        super("Sprint", "Sprints Automatically", Category.PLAYER, false, false);
    }

    BooleanSetting legit = new BooleanSetting("legit", false, this);

    @Override
    public void onUpdate() {
        if (legit.getValue()) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.setSprinting(true);
            }
        } else {
            mc.player.setSprinting(true);
        }
    }
}
