package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.MoveEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.MobEffects;

import java.util.Arrays;

@Hack.Registration(name = "Speed", description = "makes u go faster", category = Hack.Category.PLAYER, isListening = false)
public class Speed extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Strafe", Arrays.asList("Strafe", "Fake", "YPort"), this);
    DoubleSetting yPortSpeed = new DoubleSetting("YPort Speed", 0.06, 0.01, 0.15, this, s -> mode.is("YPort"));
    DoubleSetting jumpHeight = new DoubleSetting("Jump Height", 0.41, 0.0, 1.0, this, s -> mode.is("Strafe"));
    DoubleSetting timerVal = new DoubleSetting("Timer Speed", 1.15, 1.0, 1.5, this, s -> mode.is("Strafe"));

    private boolean slowdown;
    private double playerSpeed;
    private final Timer timer = new Timer();

    @Override
    public void onEnable() {
        playerSpeed = PlayerUtil.getBaseMoveSpeed();
    }

    @Override
    public void onDisable() {
        EntityUtil.resetTimer();
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) {
            this.disable();
            return;
        }

        if (mode.is("YPort")) {
            this.handleYPortSpeed();
        }

    }

    private void handleYPortSpeed() {
        if (!PlayerUtil.isMoving(mc.player) || mc.player.isInWater() && mc.player.isInLava() || mc.player.collidedHorizontally) {
            return;
        }

        if (mc.player.onGround) {
            EntityUtil.setTimer(1.15f);
            mc.player.jump();
            PlayerUtil.setSpeed(mc.player, PlayerUtil.getBaseMoveSpeed() + yPortSpeed.getValue());
        } else {
            mc.player.motionY = -1;
            EntityUtil.resetTimer();
        }
    }

    @CommitEvent
    public void onMove(MoveEvent event) {
        if (mc.player.isInLava() || mc.player.isInWater() || mc.player.isOnLadder() || mc.player.isInWeb) {
            return;
        }

        if (mode.getValue().equalsIgnoreCase("Strafe")) {
            double speedY = jumpHeight.getValue();

            if (mc.player.onGround && PlayerUtil.isMoving(mc.player) && timer.passedMs(300)) {
                EntityUtil.setTimer(timerVal.getValue().floatValue());
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    speedY += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                event.setY(mc.player.motionY = speedY);
                playerSpeed = PlayerUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0, -0.5, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.9 : 1.901);
                slowdown = true;
                timer.reset();
            } else {
                EntityUtil.resetTimer();
                if (slowdown || mc.player.collidedHorizontally) {
                    playerSpeed -= (EntityUtil.isColliding(0, -0.8, 0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.4 : 0.7 * (playerSpeed = PlayerUtil.getBaseMoveSpeed());
                    slowdown = false;
                } else {
                    playerSpeed -= playerSpeed / 159.0;
                }
            }
            playerSpeed = Math.max(playerSpeed, PlayerUtil.getBaseMoveSpeed());
            double[] dir = PlayerUtil.forward(playerSpeed);
            event.setX(dir[0]);
            event.setZ(dir[1]);
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue();
    }
}
