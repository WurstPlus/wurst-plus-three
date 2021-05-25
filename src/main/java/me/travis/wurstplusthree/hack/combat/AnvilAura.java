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

// TODO : FIX THIS

// UNFINISHED

@Hack.Registration(name = "Anvil Aura", description = "drops anvils on people/urself", category = Hack.Category.COMBAT, isListening = false)
public class AnvilAura extends Hack implements Globals {

    EnumSetting mode = new EnumSetting("Mode", "Others", Arrays.asList("Self", "Others"), this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    BooleanSetting breakAnvil = new BooleanSetting("Break anvils", true, this);
    BooleanSetting airplace = new BooleanSetting("Airplace", false, this);
    IntSetting range = new IntSetting("Range", 3, 0, 6, this);
    IntSetting bpt = new IntSetting("Blocks per tick", 4, 0, 10, this);
    IntSetting placeDelay = new IntSetting("Place Delay", 4, 0, 20, this);
    IntSetting layers = new IntSetting("Layers", 3, 1, 5, this);
    
    private static int placedAmmount = 0;
    private static int tickspassed = 0;
    
    @Override
    public void onEnable() {
        if (InventoryUtil.findHotbarBlock(BlockAnvil.class) == -1 || PlayerUtil.findObiInHotbar() == -1) {
            this.disable();
            tickspassed = 0;
        }
        
        if (mode.is("Self")) {
        	placeAnvil(new BlockPos(mc.player.getPosition().up(3)));
        }
    }

    @Override
    public void onTick() {
        EntityPlayer target = mode.is("Self") ? mc.player : this.getTarget();
        placedAmmount = 0;
        
        if (target == null) return;
        
        if (mode.is("Self")) {
            if (airplace.getValue() && !target.isAirBorne) {
                placeAnvil(new BlockPos(EntityUtil.getFlooredPos(target)).up(range.getValue()));
            }
        }    
        if (mode.is("Self")) {
            if (airplace.getValue()) {
                placeAnvil(EntityUtil.getFlooredPos(target).up(range.getValue()));
            } else {
                if (!BlockUtil.canPlaceBlock(target.getPosition().up(3))) {
                    for (int i = 0; i < range.getValue() / 2; i ++) {
                        BlockPos pos = new BlockPos(target.posX - 1, target.posY - i - 1, target.posZ);
                        if (mc.world.getBlockState(pos) != Blocks.AIR) {
                            placeObi(pos);
                        }
                    }
                }
                if (tickspassed == 0) {
	                if (airplace.getValue() || mc.world.getBlockState(target.getPosition().up(3).south()) != Blocks.AIR) {
	                	placeAnvil(target.getPosition().up(3));
	                } else {
	                	placeObi(target.getPosition().up(3).south());
	                	placeAnvil(target.getPosition().up(3));
	                }
	                tickspassed ++;
                } else if (tickspassed == placeDelay.getValue()) {
                	tickspassed = 0;
                } else {
                	tickspassed ++;
                }
            }
        }
        
        else {
            if (mc.world.getBlockState(target.getPosition()) == Blocks.ANVIL) {
                this.breakBlock(target.getPosition());
            } else {
                for (int i = 0; placedAmmount < this.bpt.getValue(); i ++) {
                    if (i > layers.getValue()) break;
                    if (mc.world.getBlockState(new BlockPos(target.posX - 1, target.posY + i, target.posZ)) == Blocks.AIR) {
						placeObi(new BlockPos(target.posX - 1, target.posY + i, target.posZ));
                        placedAmmount ++;
                    } if (mc.world.getBlockState(new BlockPos(target.posX, target.posY + i, target.posZ - 1)) == Blocks.AIR) {
                        placeObi(new BlockPos(target.posX, target.posY + i, target.posZ - 1));
                        placedAmmount ++;
                    } if (mc.world.getBlockState(new BlockPos(target.posX + 1, target.posY + i, target.posZ)) == Blocks.AIR) {
                        placeObi(new BlockPos(target.posX + 1, target.posY + i, target.posZ));
                        placedAmmount ++;
                    } if (mc.world.getBlockState(new BlockPos(target.posX, target.posY + i, target.posZ + 1)) == Blocks.AIR) {
                        placeObi(new BlockPos(target.posX, target.posY + i, target.posZ + 1));
                        placedAmmount ++;
                    }
                } placeAnvil(target.getPosition().up(3));
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
        boolean found = false;
        for (int i = 0; i < 9 && !found; i ++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                found = true;
                this.switchToSlot(i);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, null));
                break;
            }
        } this.switchToSlot(old);
    }

    private EntityPlayer getTarget() {
        EntityPlayer target = null;
        double shortestRange = 10;
        for (EntityPlayer player : mc.world.playerEntities) {
            double range = mc.player.getDistance(player);
            if (range > this.range.getValue() || !EntityUtil.isInHole(player) || !this.isValid(player)) continue;
            if (range < shortestRange) {
                target = player;
                shortestRange = range;
            }
        }
        return target;
    }

    private boolean isValid(EntityPlayer player) {
        BlockPos pos = EntityUtil.getFlooredPos(player);
        if (mc.world.getBlockState(pos.up(2)).getBlock() != Blocks.AIR || mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR) return false;
        return mc.world.getBlockState(pos.down()).getBlock() == Blocks.AIR || ammount.getValue() != 1;
    }

    private void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

}


