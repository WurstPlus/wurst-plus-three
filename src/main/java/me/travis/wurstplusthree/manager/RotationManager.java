package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.hacks.misc.NoRotate;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.MathsUtil;
import me.travis.wurstplusthree.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RotationManager implements Globals {

    private float yaw;
    private float pitch;
    private float spoofedYaw;
    private float spoofedPitch;

    public void onPacketReceive(SPacketPlayerPosLook p) {
        spoofedPitch = p.getPitch();
        spoofedYaw = p.getYaw();
        if (NoRotate.INSTANCE.isEnabled()) {
            p.pitch = pitch;
            p.yaw = yaw;
        }
    }

    public float getSpoofedYaw() {
        return MathsUtil.wrapDegrees(spoofedYaw);
    }

    public float getSpoofedPitch() {
        return spoofedPitch;
    }

    public void updateRotations() {
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;
    }

    public void restoreRotations() {
        mc.player.rotationYaw = this.yaw;
        mc.player.rotationYawHead = this.yaw;
        mc.player.rotationPitch = this.pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPlayerYaw(float yaw) {
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    public void lookAtEntity(Entity entity) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionEyes(mc.getRenderPartialTicks()));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void onPacketSend(PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            spoofedPitch = ((CPacketPlayer) event.getPacket()).getPitch(0);
            spoofedYaw = ((CPacketPlayer) event.getPacket()).getYaw(0);
        }
    }

    public void setPlayerPitch(float pitch) {
        mc.player.rotationPitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getDirection4D() {
        return RotationUtil.getDirection4D();
    }

    public String getDirection4D(boolean northRed) {
        return RotationUtil.getDirection4D(northRed);
    }

}
