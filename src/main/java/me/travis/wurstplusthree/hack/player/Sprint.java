package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.MoveEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.EnumSetting;

import java.util.Arrays;

@Hack.Registration(name = "Sprint", description = "sprints automatically", category = Hack.Category.PLAYER, isListening = false)
public class Sprint extends Hack {

    public EnumSetting mode = new EnumSetting("Mode", "Legit", Arrays.asList("legit", "Rage"), this);

    @CommitEvent
    public void onMove(MoveEvent event) {
        if (event.getStage() == 1 && this.mode.is("Rage") && (mc.player.movementInput.moveForward != 0f ||
                mc.player.moveStrafing != 0f)) {
            event.setCancelled(true);
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
        return mode.getValue();
    }
}
