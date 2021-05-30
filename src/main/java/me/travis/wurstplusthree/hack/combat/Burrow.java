package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.MappingUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.Arrays;

@Hack.Registration(name = "Burrow", description = "fills ur lower-half with a block", category = Hack.Category.COMBAT, isListening = false)
public class Burrow extends Hack {

    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);

    EnumSetting type = new EnumSetting("Type", "Packet", Arrays.asList("Packet", "Normal"), this);
    EnumSetting block = new EnumSetting("Block", "All", Arrays.asList("All", "EChest", "Chest", "WhiteList"), this);
    DoubleSetting force = new DoubleSetting("Force", 1.5, -5.0, 10.0, this);
    BooleanSetting instant = new BooleanSetting("Instant", true, this, s -> type.is("Normal"));
    BooleanSetting center = new BooleanSetting("Center", false, this);
    BooleanSetting bypass = new BooleanSetting("Bypass", false, this);

    int swapBlock = -1;
    Vec3d centerBlock = Vec3d.ZERO;
    BlockPos oldPos;
    Block blockW = Blocks.OBSIDIAN;
    boolean flag;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        flag = false;
        if (CrystalAura.INSTANCE.isEnabled()) {
            flag = true;
            CrystalAura.INSTANCE.disable();
        }

        mc.player.motionX = 0;
        mc.player.motionZ = 0;

        centerBlock = this.getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (centerBlock != Vec3d.ZERO && center.getValue()) {
            double x_diff = Math.abs(centerBlock.x - mc.player.posX);
            double z_diff = Math.abs(centerBlock.z - mc.player.posZ);
            if (x_diff <= 0.1 && z_diff <= 0.1) {
                centerBlock = Vec3d.ZERO;
            } else {
                double motion_x = centerBlock.x - mc.player.posX;
                double motion_z = centerBlock.z - mc.player.posZ;
                mc.player.motionX = motion_x / 2;
                mc.player.motionZ = motion_z / 2;
            }
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
                break;
            case "WhiteList":
                swapBlock = InventoryUtil.findHotbarBlock(blockW.getClass());
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
            if(bypass.getValue() && !mc.player.isSneaking()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.setSneaking(true);
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.player.setSneaking(false);
                mc.playerController.updateController();
            }
            this.disable();
        }
    }

    @Override
    public void onDisable(){
        if(instant.getValue() && !nullCheck()){
            this.setTimer(1f);
        }
        if (flag) {
            CrystalAura.INSTANCE.enable();
        }
    }

    private void switchToSlot(final int slot) {
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

    private Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

    @Override
    public String getDisplayInfo() {
        return this.type.getValue();
    }

    public void setBlock(Block b){
        this.blockW = b;
    }

    public Block getBlock(){
        return this.blockW;
    }

}
