package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

@Hack.Registration(name = "Self Trap", description = "when all else fails u can self trap", category = Hack.Category.COMBAT, isListening = false)
public class SelfTrap extends Hack {
    /*
     * k3b skidded this all by himself :D
     */

    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);

    private BlockPos trapPos;

    public void onEnable() {
        if (findInHotbar() == -1) {
            this.disable();
        }
    }

    public void onUpdate() {
        final Vec3d pos = EntityUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        trapPos = new BlockPos(pos.x, pos.y + 2, pos.z);
        if (isTrapped()) {
            if (this.isEnabled()) {
                this.disable();
            } else {
                this.enable();
                return;
            }
        }

        BlockUtil.ValidResult result = BlockUtil.valid(trapPos);

        if (result == BlockUtil.ValidResult.AlreadyBlockThere && !mc.world.getBlockState(trapPos).getMaterial().isReplaceable()) {
            return;
        }

        if (result == BlockUtil.ValidResult.NoNeighbors) {

            BlockPos[] tests = {
                    trapPos.north(),
                    trapPos.south(),
                    trapPos.east(),
                    trapPos.west(),
                    trapPos.up(),
                    trapPos.down().west()
            };

            for (BlockPos pos_ : tests) {
                BlockUtil.ValidResult result_ = BlockUtil.valid(pos_);
                if (result_ == BlockUtil.ValidResult.NoNeighbors || result_ == BlockUtil.ValidResult.NoEntityCollision) continue;
                if (BlockUtil.placeBlock(pos_, findInHotbar(), rotate.getValue(), rotate.getValue(), swing)) {
                    return;
                }
            }
            return;
        }
        BlockUtil.placeBlock(trapPos, findInHotbar(), rotate.getValue(), rotate.getValue(), swing);
    }

    public boolean isTrapped() {
        if (trapPos == null) return false;
        IBlockState state = mc.world.getBlockState(trapPos);
        return state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA;
    }

    private int findInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockEnderChest)
                    return i;
                else if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
    }

}