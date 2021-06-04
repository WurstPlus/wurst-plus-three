package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.KeyEvent;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.PushEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Hack.Registration(name = "No Knockback", description = "makes u stay in place", category = Hack.Category.PLAYER, isListening = false)
public class NoKnockback extends Hack {

    private static final KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};

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
        if (mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiVideoSettings || mc.currentScreen instanceof GuiScreenOptionsSounds || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu) {
            for (KeyBinding bind : keys) {
                KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
            }
        } else if (mc.currentScreen == null) {
            for (KeyBinding bind : keys) {
                if (Keyboard.isKeyDown(bind.getKeyCode())) continue;
                KeyBinding.setKeyBindState(bind.getKeyCode(), false);
            }
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

    @CommitEvent
    public void onKeyEvent(KeyEvent event) {
        if (event.getStage() == 0 && !(mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }

    @CommitEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getStage() == 0 && mc.player != null) {
            Entity entity;
            SPacketEntityStatus packet;
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                if (((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.entityId) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (event.getPacket() instanceof SPacketEntityStatus && this.bobbers.getValue()
                    && (packet = event.getPacket()).getOpCode() == 31
                    && (entity = packet.getEntity(mc.world)) instanceof EntityFishHook) {
                EntityFishHook fishHook = (EntityFishHook) entity;
                if (fishHook.caughtEntity == mc.player) {
                    event.setCancelled(true);
                }
            }
            if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion velocity_ = event.getPacket();
                velocity_.motionX *= 0;
                velocity_.motionY *= 0;
                velocity_.motionZ *= 0;
            }
        }
    }

    @CommitEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 0 && this.noPush.getValue() && event.entity.equals(mc.player)) {
            event.setCancelled(true);
        } else if (event.getStage() == 1 && this.blocks.getValue()) {
            event.setCancelled(true);
        } else if (event.getStage() == 2 && this.water.getValue() && mc.player != null
                && mc.player.equals(event.entity)) {
            event.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }

    // retarded fix idk why it needs it
    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
}
