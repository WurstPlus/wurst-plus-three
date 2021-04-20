package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.MoveEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class Sprint extends Hack {

    public Sprint() {
        super("Sprint", "Sprints Automatically", Category.PLAYER, false);
    }

    public EnumSetting mode = new EnumSetting("Mode", "Legit", Arrays.asList("legit", "Rage"), this);

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (event.getStage() == 1 && this.mode.is("Rage") && (mc.player.movementInput.moveForward != 0f ||
                mc.player.moveStrafing != 0f)) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (mode.is("Legit")) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.setSprinting(true);
            }
        } else {
            mc.player.setSprinting(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValueName();
    }
}
