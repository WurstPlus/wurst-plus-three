package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.PushEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoKnockback extends Hack {

    public NoKnockback() {
        super("NoKnockback", "makes u stay in place", Category.PLAYER, false, false);
    }

    public BooleanSetting noPush = new BooleanSetting("NoPush", true, this);
    public BooleanSetting bobbers = new BooleanSetting("Bobbers", true, this);
    public BooleanSetting water = new BooleanSetting("Water", true, this);
    public BooleanSetting blocks = new BooleanSetting("Blocks", true, this);
    public BooleanSetting ice = new BooleanSetting("Ice", true, this);

    @Override
    public void onUpdate() {
        if (this.ice.getValue()) {
            Blocks.ICE.setDefaultSlipperiness(0.6f);
            Blocks.FROSTED_ICE.setDefaultSlipperiness(0.6f);
            Blocks.PACKED_ICE.setDefaultSlipperiness(0.6f);
        }
    }

    @Override
    public void onDisable() {
        if (this.ice.getValue()) {
            Blocks.ICE.setDefaultSlipperiness(0.98f);
            Blocks.FROSTED_ICE.setDefaultSlipperiness(0.98f);
            Blocks.PACKED_ICE.setDefaultSlipperiness(0.98f);
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getStage() == 0 && mc.player != null) {
            Entity entity;
            SPacketEntityStatus packet;
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.entityId) {
                    event.setCanceled(true);
                    return;
                }
            }
            if (event.getPacket() instanceof SPacketEntityStatus && this.bobbers.getValue()
                    && (packet = event.getPacket()).getOpCode() == 31
                    && (entity = packet.getEntity(mc.world)) instanceof EntityFishHook) {
                EntityFishHook fishHook = (EntityFishHook) entity;
                if (fishHook.caughtEntity == mc.player) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 0 && this.noPush.getValue() && event.entity.equals(mc.player)) {
            event.setCanceled(true);
        } else if (event.getStage() == 1 && this.blocks.getValue()) {
            event.setCanceled(true);
        } else if (event.getStage() == 2 && this.water.getValue() && mc.player != null
                && mc.player.equals(event.entity)) {
            event.setCanceled(true);
        }
    }

}
