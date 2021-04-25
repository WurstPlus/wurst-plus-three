package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoleFill extends Hack {

    public HoleFill() {
        super("Hole Fill", "fills holes", Category.COMBAT, false);
    }

    IntSetting range = new IntSetting("Range", 3, 1, 6, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);

    private final List<BlockPos> holes = new ArrayList<>();

    @Override
    public void onEnable() {
        holes.clear();
    }

    @Override
    public void onUpdate() {
        this.holes.clear();
        this.findNewHoles();

        BlockPos posToFill = null;

        for (BlockPos pos : new ArrayList<>(holes)) {
            if (pos == null) continue;
            BlockUtil.ValidResult result = BlockUtil.valid(pos);
            if (result != BlockUtil.ValidResult.Ok) {
                holes.remove(pos);
                continue;
            }
            posToFill = pos;
            break;
        }

        if (PlayerUtil.findObiInHotbar() == -1) {
            this.disable();
            return;
        }
        if (posToFill != null) {
            if (BlockUtil.placeBlock(posToFill, PlayerUtil.findObiInHotbar(), rotate.getValue(), rotate.getValue(), swing)) {
                holes.remove(posToFill);
            }
        }

    }

    public void findNewHoles() {

        holes.clear();

        for (BlockPos pos : EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range.getValue(), (int) range.getValue(), false, true, 0)) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            boolean possible = true;

            for (BlockPos seems_blocks : new BlockPos[] {
                    new BlockPos( 0, -1,  0),
                    new BlockPos( 0,  0, -1),
                    new BlockPos( 1,  0,  0),
                    new BlockPos( 0,  0,  1),
                    new BlockPos(-1,  0,  0)
            }) {
                Block block = mc.world.getBlockState(pos.add(seems_blocks)).getBlock();

                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    possible = false;
                    break;
                }
            }

            if (possible) {
                holes.add(pos);
            }
        }
    }

}
