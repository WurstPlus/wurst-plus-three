package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.*;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

@Hack.Registration(name = "Anvil Aura", description = "drops anvils on people/urself", category = Hack.Category.COMBAT, isListening = false)
public class AnvilAura extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Others", Arrays.asList("Self", "Others"), this);
    EnumSetting switchMode = new EnumSetting("Switch", "Packet", Arrays.asList("Normal", "Packet"), this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    BooleanSetting airplace = new BooleanSetting("Airplace", false, this);
    IntSetting range = new IntSetting("Range", 6, 0, 10, this);
    IntSetting bpt = new IntSetting("Blocks per tick", 4, 0, 10, this);
    IntSetting placeDelay = new IntSetting("Place Delay", 4, 0, 20, this);
    IntSetting layers = new IntSetting("Layers", 3, 1, 5, this);

    private int ticksPassed;
    
    @Override
    public void onEnable() {
        if (InventoryUtil.findHotbarBlock(BlockAnvil.class) == -1 || PlayerUtil.findObiInHotbar() == -1) {
        	ClientMessage.sendErrorMessage("Cannot find resources in hotbar");
            this.disable();
        } ticksPassed = placeDelay.getValue();
    }

    @Override
    public void onTick() {
        EntityPlayer target = mode.is("Self") ? mc.player : this.getTarget();
        int placedAmmount = 0;
        
        if (target == null) {
            return;
        }  
        if (mode.is("Self") && mc.world.getBlockState(target.getPosition()).getBlock() == Blocks.ANVIL) {
            if (airplace.getValue()) {
                placeAnvil(EntityUtil.getFlooredPos(target).up(2));
            } else {
                if (!BlockUtil.canPlaceBlock(target.getPosition().up(3))) {
                    for (int i = 0; i < 3; i ++) {
                        BlockPos pos = new BlockPos(target.posX - 1, target.posY - i - 1, target.posZ);
                        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR) placeObi(pos);
                    }
                }
                if (ticksPassed == placeDelay.getValue()) {
	                if (airplace.getValue() || mc.world.getBlockState(target.getPosition().up(3).south()).getBlock() != Blocks.AIR) {
	                	placeAnvil(target.getPosition().up(3));
	                } else {
	                	placeObi(target.getPosition().up(3).south());
	                	placeAnvil(target.getPosition().up(3));
	                }
	                ticksPassed = 0;
                } else {
                	ticksPassed++;
                }
            }
        } else {
            if (mc.world.getBlockState(target.getPosition()).getBlock() == Blocks.ANVIL) {
                this.breakBlock(target.getPosition());
            } else {
                for (int i = 0; placedAmmount < this.bpt.getValue() || i > layers.getValue(); i ++) {
                    if (mc.world.getBlockState(new BlockPos(target.posX - 1, target.posY + i, target.posZ)).getBlock() == Blocks.AIR) {
						placeObi(new BlockPos(target.posX - 1, target.posY + i, target.posZ));
                        placedAmmount++;
                    } if (mc.world.getBlockState(new BlockPos(target.posX, target.posY + i, target.posZ - 1)).getBlock() == Blocks.AIR) {
                        placeObi(new BlockPos(target.posX, target.posY + i, target.posZ - 1));
                        placedAmmount++;
                    } if (mc.world.getBlockState(new BlockPos(target.posX + 1, target.posY + i, target.posZ)).getBlock() == Blocks.AIR) {
                        placeObi(new BlockPos(target.posX + 1, target.posY + i, target.posZ));
                        placedAmmount++;
                    } if (mc.world.getBlockState(new BlockPos(target.posX, target.posY + i, target.posZ + 1)).getBlock() == Blocks.AIR) {
                        placeObi(new BlockPos(target.posX, target.posY + i, target.posZ + 1));
                        placedAmmount++;
                    }
                } placeAnvil(target.getPosition().up(layers.getValue()));
            }
        }
    }

    private void placeAnvil(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        this.switchToSlot(InventoryUtil.findHotbarBlock(BlockAnvil.class));
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
        this.switchToSlot(old);
    }

    private void placeObi(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        this.switchToSlot(PlayerUtil.findObiInHotbar());
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), true, false);
        this.switchToSlot(old);
    }
    
    private void breakBlock(BlockPos pos) {
        int old = mc.player.inventory.currentItem;
        for (int i = 0; i < 9; i ++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                this.switchToSlot(i);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, mc.player.getHorizontalFacing()));
                break;
            }
        } this.switchToSlot(old);
    }

    private EntityPlayer getTarget() {
        EntityPlayer target = null;
        double shortestRange = range.getValue();
        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.player.getDistance(player) < shortestRange && this.isValid(player)) {
            	shortestRange = mc.player.getDistance(player);
            	target = player;
            }
        }
        return target;
    }

    private boolean isValid(EntityPlayer player) {
        BlockPos pos = EntityUtil.getFlooredPos(player);
        return mc.world.getBlockState(pos.up(2)).getBlock() == Blocks.AIR || mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR;
    }

    private void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        if (switchMode.is("Normal")) {
        	mc.player.inventory.currentItem = slot;
        	mc.playerController.updateController();
        }
    }

}


