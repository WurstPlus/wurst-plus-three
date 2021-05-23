package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import me.travis.wurstplusthree.util.Globals;
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
    
    public static AnvilAura INSTANCE;
    
    public AnvilAura() {
        INSTANCE = this;
    }

    EnumSetting mode = new EnumSetting("Mode", "Others", Arrays.asList("Self", "Others"), this);
    IntSetting ammount = new IntSetting("Ammount", 1, 1, 2, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    BooleanSetting breakAnvil = new BooleanSetting("Break anvils", true, this);
    BooleanSetting airplace = new BooleanSetting("Airplace", false, this);
    IntSetting range = new IntSetting("Range", 3, 0, 6, this);
    IntSetting bpt = new IntSetting("Blocks per tick", 4, 0, 10, this);
    IntSetting placeDelay = new IntSetting("Place Delay", 4, 0, 10, this);
    IntSetting layers = new IntSetting("Layers", 3, 1, 5, this);

    private int placedAmmount;
    
    @Override
    public void onEnable() {
        this.placedAmmount = 0;
        if (InventoryUtil.findHotbarBlock(BlockAnvil.class) == -1 || PlayerUtil.findObiInHotbar() == -1) {
            this.disable();
        }
    }

    @Override
    public void onTick() {
        EntityPlayer target = mode.is("Self") ? mc.player : this.getTarget();
        placedAmmount = 0;
        
        if (mode.is("Self")) {
            if (airplace.getValue() && !target.isAirBorne) {
                placeAnvil(new BlockPos(EntityUtil.getFlooredPos(target)).up(range.getValue()));
            }
        }    
        if (mode.is("Self") && mc.world.getBlockState(target.getPosition().up()) != Blocks.AIR) {
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
                if (airplace.getValue() || mc.world.getBlockState(target.getPosition().up(3).south()) != Blocks.AIR) {
                	placeAnvil(target.getPosition().up(3));
                } else {
                	placeObi(target.getPosition().up(3).south());
                	placeAnvil(target.getPosition().up(3));
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


