package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.BotUtil;

@Hack.Registration(name = "Bot", description = "pvp bot", category = Hack.Category.COMBAT, isListening = false)
public class Bot extends Hack {

    private MovementStage currentMovementStage;

    @Override
    public void onEnable() {
        // reset movement
        mc.player.rotationYaw = 0;
        mc.player.rotationYawHead = 0;
        mc.player.rotationPitch = 0;
        // set stage to default
        this.currentMovementStage = MovementStage.WANDER;
    }

    @Override
    public void onDisable() {
        this.currentMovementStage = null;
    }

    @Override
    public void onTick() {
        switch (this.currentMovementStage) {
            case STILL:
                break;
            case WANDER:
                doWander();
                break;
            case RUN:
                break;
            case CHASE:
                break;
        }
    }

    private void doWander() {
        if (!WurstplusThree.HACKS.ishackEnabled("Step")) {
            WurstplusThree.HACKS.enablehack("Step");
        }
        if (!mc.player.isSprinting()) {
            mc.player.setSprinting(true);
        }
        mc.gameSettings.keyBindForward.pressed = true;
        if (BotUtil.isAheadJumpable()) {
            mc.player.jump();
        }
        if (!BotUtil.isAheadStepable()) {
            mc.player.rotationYaw += 90;
        }
    }

    private enum MovementStage {
        WANDER,
        STILL,
        RUN,
        CHASE
    }

}
