package me.travis.wurstplusthree.hack.hacks.player;

import io.netty.util.internal.ConcurrentSet;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.PushEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Madmegsox1
 * @since 21/07/2021
 */

@Hack.Registration(name = "Phase", description = "bots use this", category = Hack.Category.PLAYER, priority = HackPriority.Highest)
public class Phase extends Hack {

    DoubleSetting loops = new DoubleSetting("Loops", 0.5, 0.0, 5.0, this);
    BooleanSetting bypass = new BooleanSetting("Bypass", true, this);


    private final Set<CPacketPlayer> packets = new ConcurrentSet<>();
    private final Map<Integer, IDtime> teleportmap = new ConcurrentHashMap<>();
    private int flightCounter = 0;
    private int teleportID = 0;


    @CommitEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        mc.player.setVelocity(0.0, 0.0, 0.0);
        double speed;
        boolean checkCollisionBoxes = this.checkHitBoxes();
        speed = mc.player.movementInput.jump && (checkCollisionBoxes || !isMoving()) ? !checkCollisionBoxes ? this.resetCounter(10) ? -0.032 : 0.062 : 0.062 : mc.player.movementInput.sneak ? -0.062 : !checkCollisionBoxes ? this.resetCounter(4) ? -0.04 : 0.0 : 0.0;
        if (checkCollisionBoxes && isMoving() && speed != 0.0) {
            double antiFactor = 2.5;
            speed /= antiFactor;
        }
        double[] strafing = this.getMotion(checkCollisionBoxes ? 0.031 : 0.26);
        double loops = bypass.getValue() ? this.loops.getValue() : 0.0;
        for (int i = 1; i < loops + 1; ++i) {
            double extraFactor = 1.0;
            mc.player.motionX = strafing[0] * (double)i * extraFactor;
            mc.player.motionY = speed * (double)i;
            mc.player.motionZ = strafing[1] * (double)i * extraFactor;
            this.sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
        }
    }

    @CommitEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove(event.getPacket())) {
            event.setCancelled(true);
        }
    }

    @CommitEvent
    public void onPushOutOfBlocks(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCancelled(true);
        }
    }

    @CommitEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !nullCheck()) {
            SPacketPlayerPosLook packet = event.getPacket();
            if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
                this.teleportmap.remove(packet.getTeleportId());
            }
            this.teleportID = packet.getTeleportId();
        }
    }


    private boolean resetCounter(int counter) {
        if (++this.flightCounter >= counter) {
            this.flightCounter = 0;
            return true;
        }
        return false;
    }

    private double[] getMotion(double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    private void sendPackets(double x, double y, double z) {
        Vec3d vec = new Vec3d(x, y, z);
        Vec3d position = mc.player.getPositionVector().add(vec);
        Vec3d outOfBoundsVec = this.outOfBoundsVec(position);
        this.packetSender(new CPacketPlayer.Position(position.x, position.y, position.z, mc.player.onGround));
        this.packetSender(new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, mc.player.onGround));
        this.teleportPacket(position);
    }

    private void teleportPacket(Vec3d pos) {
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(++this.teleportID));
        this.teleportmap.put(this.teleportID, new IDtime(pos, new Timer()));
    }

    private Vec3d outOfBoundsVec(Vec3d position) {
        return position.add(0.0, 1337.0, 0.0);
    }

    private void packetSender(CPacketPlayer packet) {
        this.packets.add(packet);
        mc.player.connection.sendPacket(packet);
    }

    public static class IDtime {
        private final Vec3d pos;
        private final Timer timer;

        public IDtime(Vec3d pos, Timer timer) {
            this.pos = pos;
            this.timer = timer;
            this.timer.reset();
        }

        public Vec3d getPos() {
            return this.pos;
        }

        public Timer getTimer() {
            return this.timer;
        }
    }


    private boolean checkHitBoxes() {
        return !mc.world.getCollisionBoxes( mc.player, mc.player.getEntityBoundingBox().expand(-0.0, -0.1, -0.0)).isEmpty();
    }

    public static boolean isMoving() {
        return (double) mc.player.moveForward != 0.0 || (double) mc.player.moveStrafing != 0.0;
    }
}
