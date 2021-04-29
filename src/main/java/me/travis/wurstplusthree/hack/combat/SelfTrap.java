package me.travis.wurstplusthree.hack.combat;

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

import java.util.*;

public class SelfTrap extends Hack {

    public SelfTrap() {
        super("SelfTrap", "when all else fails u can self trap", Hack.Category.COMBAT, false);
    }

    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);


    private BlockPos trap_pos;


    public void onEnable() {
        if (find_in_hotbar() == -1) {
            this.disable();
            return;
        }
    }


    public void onUpdate() {
        final Vec3d pos = EntityUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
        trap_pos = new BlockPos(pos.x, pos.y + 2, pos.z);
        if (is_trapped()) {

            if (this.isEnabled()) {
                this.disable();
            } else {
                this.enable();
                return;
            }

        }

        BlockUtil.ValidResult result = BlockUtil.valid(trap_pos);

        if (result == BlockUtil.ValidResult.AlreadyBlockThere && !mc.world.getBlockState(trap_pos).getMaterial().isReplaceable()) {
            return;
        }

        if (result == BlockUtil.ValidResult.NoNeighbors) {

            BlockPos[] tests = {
                    trap_pos.north(),
                    trap_pos.south(),
                    trap_pos.east(),
                    trap_pos.west(),
                    trap_pos.up(),
                    trap_pos.down().west() // ????? salhack is weird and i dont care enough to remove this. who the fuck uses this shit anyways fr fucking jumpy
            };

            for (BlockPos pos_ : tests) {

                BlockUtil.ValidResult result_ = BlockUtil.valid(pos_);

                if (result_ == BlockUtil.ValidResult.NoNeighbors || result_ == BlockUtil.ValidResult.NoEntityCollision) continue;

                if (BlockUtil.placeBlock(pos_, find_in_hotbar(), rotate.getValue(), rotate.getValue(), swing)) {
                    return;
                }

            }

            return;

        }

        BlockUtil.placeBlock(trap_pos, find_in_hotbar(), rotate.getValue(), rotate.getValue(), swing);

    }

    public boolean is_trapped() {

        if (trap_pos == null) return false;

        IBlockState state = mc.world.getBlockState(trap_pos);

        return state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA;

    }

    private int find_in_hotbar() {

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