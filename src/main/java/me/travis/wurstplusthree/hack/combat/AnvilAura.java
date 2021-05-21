package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

// TODO : FIX THIS

@Hack.Registration(name = "Anvil Aura", description = "drops anvils on people/urself", category = Hack.Category.COMBAT, isListening = false)
public class AnvilAura extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Others", Arrays.asList("Self", "Others"), this);
    IntSetting ammount = new IntSetting("Ammount", 1, 1, 2, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    BooleanSetting breakAnvil = new BooleanSetting("Break anvils", true, this);
    BooleanSetting airplace = new BooleanSetting("Airplace", false, this);
    IntSetting range = new IntSetting("Range", 4, 0, 6, this);

    private int placedAmmount;
    private boolean trapped;

    @Override
    public void onEnable() {
        this.placedAmmount = 0;
        this.trapped = false;
        if (InventoryUtil.findHotbarBlock(BlockAnvil.class) == -1 || PlayerUtil.findObiInHotbar() == -1) {
            this.disable();
        }
    }

    @Override
    public void onTick() {
        EntityPlayer target = mode.is("Self") ? mc.player : this.getTarget();
        
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
        		placeAnvil(target.getPosition().up(range.getValue() / 2));
        	}
        }
        
        else {
        	if (mc.world.getBlockState(target.getPosition()) == Blocks.ANVIL) {
        		
        	}
        	
        	for (Vec3d vec : EntityUtil.getUnsafeBlockArray(target, 4, true)) {
        		placeObi(new BlockPos(vec.x, vec.y, vec.z));
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
        if (mc.world.getBlockState(pos.up(2)).getBlock() != Blocks.AIR) return false;
        if (mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR) return false;
        return mc.world.getBlockState(pos.down()).getBlock() == Blocks.AIR || ammount.getValue() != 1;
    }

    private void switchToSlot(final int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

}
