package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.JesusEvent;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Arrays;

@Hack.Registration(name = "Jesus", description = "its jebus, say hello jebus SHGDFYGSDKJHFGSDHJ", category = Hack.Category.PLAYER, isListening = false)
public class Jesus extends Hack {

    public static Jesus INSTANCE;

    public Jesus() {
        INSTANCE = this;
    }

    public static AxisAlignedBB offset = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9999, 1.0);
    public EnumSetting mode = new EnumSetting("Mode", "Vanilla", Arrays.asList("Vanilla", "Trampoline", "Bounce", "Normal"), this);
    public BooleanSetting cancelVehicle = new BooleanSetting("No Vehcile", false, this);

    public EnumSetting eventMode = new EnumSetting("Event", "Pre", Arrays.asList("Pre", "Post", "All"), this);
    public BooleanSetting fall = new BooleanSetting("No Fall", false, this);

    private boolean grounded = false;

    @CommitEvent
    public void updateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (nullCheck() || WurstplusThree.HACKS.ishackEnabled("Freecam")) {
            return;
        }
        if (!(event.getStage() != 0 || mode.is("Trampoline") || mc.player.isSneaking() || mc.player.noClip || mc.gameSettings.keyBindJump.isKeyDown() || !EntityUtil.isInLiquid())) {
            mc.player.motionY = 0.1f;
        }
        if (event.getStage() == 0 && mode.is("Trampoline") && !eventMode.is("Post")) {
            this.doTrampoline();
        } else if (event.getStage() == 1 && mode.is("Trampoline") && !eventMode.is("Pre")) {
            this.doTrampoline();
        }
    }

    @CommitEvent
    public void sendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && WurstplusThree.HACKS.ishackEnabled("Freecam")
                && (mode.is("Bounce") || mode.is("Normal"))
                && mc.player.getRidingEntity() == null && !mc.gameSettings.keyBindJump.isKeyDown()) {
            CPacketPlayer packet = event.getPacket();
            if (!EntityUtil.isInLiquid() && EntityUtil.isOnLiquid(0.05f) && EntityUtil.checkCollide() && mc.player.ticksExisted % 3 == 0) {
                packet.y -= 0.05f;
            }
        }
    }

    @CommitEvent
    public void onLiquidCollision(JesusEvent event) {
        if (nullCheck() || WurstplusThree.HACKS.ishackEnabled("Freecam")) {
            return;
        }
        if (event.getStage() == 0 && !this.mode.is("Trampoline") && mc.world != null && mc.player != null
                && EntityUtil.checkCollide() && !(mc.player.motionY >= (double) 0.1f) && (double) event.getPos().getY() < mc.player.posY - (double) 0.05f) {
            if (mc.player.getRidingEntity() != null) {
                event.setBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.95f, 1.0));
            } else {
                event.setBoundingBox(Block.FULL_BLOCK_AABB);
            }
            event.setCancelled(true);
        }
    }

    @CommitEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (this.cancelVehicle.getValue() && event.getPacket() instanceof SPacketMoveVehicle) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.value;
    }

    private void doTrampoline() {
        if (mc.player.isSneaking()) {
            return;
        }
        if (EntityUtil.isAboveLiquid(mc.player) && !mc.player.isSneaking() && !mc.gameSettings.keyBindJump.pressed) {
            mc.player.motionY = 0.1;
            return;
        }
        if (mc.player.onGround || mc.player.isOnLadder()) {
            this.grounded = false;
        }
        if (mc.player.motionY > 0.0) {
            if (mc.player.motionY < 0.03 && this.grounded) {
                mc.player.motionY += 0.06713;
            } else if (mc.player.motionY <= 0.05 && this.grounded) {
                mc.player.motionY *= 1.20000000999;
                mc.player.motionY += 0.06;
            } else if (mc.player.motionY <= 0.08 && this.grounded) {
                mc.player.motionY *= 1.20000003;
                mc.player.motionY += 0.055;
            } else if (mc.player.motionY <= 0.112 && this.grounded) {
                mc.player.motionY += 0.0535;
            } else if (this.grounded) {
                mc.player.motionY *= 1.000000000002;
                mc.player.motionY += 0.0517;
            }
        }
        if (this.grounded && mc.player.motionY < 0.0 && mc.player.motionY > -0.3) {
            mc.player.motionY += 0.045835;
        }
        if (!this.fall.getValue().booleanValue()) {
            mc.player.fallDistance = 0.0f;
        }
        if (!EntityUtil.checkForLiquid(mc.player, true)) {
            return;
        }
        if (EntityUtil.checkForLiquid(mc.player, true)) {
            mc.player.motionY = 0.5;
        }
        this.grounded = true;
    }

}
