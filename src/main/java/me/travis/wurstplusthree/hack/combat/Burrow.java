package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.*;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.Arrays;

public class Burrow extends Hack {

    public Burrow() {
        super("Burrow", "wank", Category.COMBAT, false);
    }

    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    BooleanSetting instant = new BooleanSetting("Instant", true, this);
    EnumSetting type = new EnumSetting("Type", "Packet", Arrays.asList("Packet", "Normal"), this);
    EnumSetting block = new EnumSetting("Block", "All", Arrays.asList("All", "EChest", "Chest"), this);
    DoubleSetting force = new DoubleSetting("Force", 1.5, 0.0, 10.0, this);

    int swapBlock = -1;
    BlockPos oldPos;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        oldPos = PlayerUtil.getPlayerPos();
        switch (block.getValue()) {
            case "All":
                swapBlock = PlayerUtil.findObiInHotbar();
                break;
            case "EChest":
                swapBlock = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
                break;
            case "Chest":
                swapBlock = InventoryUtil.findHotbarBlock(BlockChest.class);
        }
        if (swapBlock == -1) {
            this.disable();
            return;
        }
        if (instant.getValue()) {
            this.setTimer(50f);
        }
        if (type.is("Normal")) {
            mc.player.jump();
        }
    }

    @Override
    public void onUpdate() {
        if (type.is("Normal")) {
            if (mc.player.posY > (oldPos.getY() + 1.04)) {
                int old = mc.player.inventory.currentItem;
                this.switchToSlot(swapBlock);
                BlockUtil.placeBlock(oldPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
                this.switchToSlot(old);
                mc.player.motionY = force.value;
                this.disable();
            }
        } else {
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.41999998688698,
                            mc.player.posZ,
                            true
                    )
            );
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 0.7531999805211997,
                            mc.player.posZ,
                            true
                    )
            );
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.00133597911214,
                            mc.player.posZ,
                            true
                    )
            );
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + 1.16610926093821,
                            mc.player.posZ,
                            true
                    )
            );
            int old = mc.player.inventory.currentItem;
            this.switchToSlot(swapBlock);
            BlockUtil.placeBlock(oldPos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
            this.switchToSlot(old);
            mc.player.connection.sendPacket(
                    new CPacketPlayer.Position(
                            mc.player.posX,
                            mc.player.posY + force.getValue(),
                            mc.player.posZ,
                            false
                    )
            );
            this.disable();
        }
    }

    @Override
    public void onDisable(){
        if(instant.getValue() && !nullCheck()){
            this.setTimer(1f);
        }
    }

    public void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    private void setTimer(float value) {
        try {
            Field timer = Minecraft.class.getDeclaredField(MappingUtil.timer);
            timer.setAccessible(true);
            Field tickLength = Timer.class.getDeclaredField(MappingUtil.tickLength);
            tickLength.setAccessible(true);
            tickLength.setFloat(timer.get(mc), 50.0F / value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
