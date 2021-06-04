package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.PushEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.util.MathsUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

@Hack.Registration(name = "Freecam", description = "lets u see freely", category = Hack.Category.PLAYER, isListening = false)
public class Freecam extends Hack {

    public DoubleSetting speed = new DoubleSetting("Speed", 0.5, 0.1, 5.0, this);
    public BooleanSetting view = new BooleanSetting("View", false, this);
    public BooleanSetting packet = new BooleanSetting("Packet", false, this);

    private AxisAlignedBB oldBoundingBox;
    private EntityOtherPlayerMP entity;
    private Vec3d position;
    private Entity riding;
    private float yaw;
    private float pitch;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }

        this.oldBoundingBox = mc.player.getEntityBoundingBox();
        mc.player.setEntityBoundingBox(new AxisAlignedBB(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.posX, mc.player.posY, mc.player.posZ));
        if (mc.player.getRidingEntity() != null) {
            this.riding = mc.player.getRidingEntity();
            mc.player.dismountRidingEntity();
        }
        (this.entity = new EntityOtherPlayerMP(mc.world, mc.session.getProfile())).copyLocationAndAnglesFrom(mc.player);
        this.entity.rotationYaw = mc.player.rotationYaw;
        this.entity.rotationYawHead = mc.player.rotationYawHead;
        this.entity.inventory.copyInventory(mc.player.inventory);
        mc.world.addEntityToWorld(69420, this.entity);
        this.position = mc.player.getPositionVector();
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;
        mc.player.noClip = true;
    }

    @Override
    public void onDisable() {
        if (nullCheck()) return;
        mc.player.setEntityBoundingBox(this.oldBoundingBox);
        if (this.riding != null) {
            mc.player.startRiding(this.riding, true);
        }
        if (this.entity != null) {
            mc.world.removeEntity(this.entity);
        }
        if (this.position != null) {
            mc.player.setPosition(this.position.x, this.position.y, this.position.z);
        }
        mc.player.rotationYaw = this.yaw;
        mc.player.rotationPitch = this.pitch;
        mc.player.noClip = false;
    }

    @Override
    public void onUpdate() {
        mc.player.noClip = true;
        mc.player.setVelocity(0.0, 0.0, 0.0);
        mc.player.jumpMovementFactor = this.speed.getValue().floatValue();
        final double[] dir = MathsUtil.directionSpeed(this.speed.getValue());
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }
        mc.player.setSprinting(false);
        if (this.view.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = this.speed.getValue() * -MathsUtil.degToRad(mc.player.rotationPitch) * mc.player.movementInput.moveForward;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            final EntityPlayerSP player = mc.player;
            player.motionY += this.speed.getValue();
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            final EntityPlayerSP player2 = mc.player;
            player2.motionY -= this.speed.getValue();
        }
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    @CommitEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (this.packet.getValue()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                event.setCancelled(true);
            }
        } else if (!(event.getPacket() instanceof CPacketUseEntity) && !(event.getPacket() instanceof CPacketPlayerTryUseItem) && !(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) && !(event.getPacket() instanceof CPacketPlayer) && !(event.getPacket() instanceof CPacketVehicleMove) && !(event.getPacket() instanceof CPacketChatMessage) && !(event.getPacket() instanceof CPacketKeepAlive)) {
            event.setCancelled(true);
        }
    }

    @CommitEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSetPassengers) {
            final SPacketSetPassengers packet = event.getPacket();
            final Entity riding = Freecam.mc.world.getEntityByID(packet.getEntityId());
            if (riding != null && riding == this.riding) {
                this.riding = null;
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet2 = event.getPacket();
            if (this.packet.getValue()) {
                if (this.entity != null) {
                    this.entity.setPositionAndRotation(packet2.getX(), packet2.getY(), packet2.getZ(), packet2.getYaw(), packet2.getPitch());
                }
                this.position = new Vec3d(packet2.getX(), packet2.getY(), packet2.getZ());
                Freecam.mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet2.getTeleportId()));
            }
            event.setCancelled(true);
        }
    }

    @CommitEvent
    public void onPush(final PushEvent event) {
        if (event.getStage() == 1) {
            event.setCancelled(true);
        }
    }

}
