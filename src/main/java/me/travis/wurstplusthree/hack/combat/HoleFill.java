package me.travis.wurstplusthree.hack.combat;

import com.google.common.collect.Sets;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Hack.Registration(name = "Hole Fill", description = "fills holes", category = Hack.Category.COMBAT, isListening = false)
public class HoleFill extends Hack {

    IntSetting range = new IntSetting("Range", 3, 1, 6, this);
    IntSetting holesPerSecond = new IntSetting("HPS", 3, 1, 6, this);
    EnumSetting fillMode = new EnumSetting("Mode", "Normal", Arrays.asList("Normal", "Smart", "Auto"), this);
    IntSetting smartRange = new IntSetting("Auto Range", 2, 1, 5, this);
    BooleanSetting doubleHoles = new BooleanSetting("Double Fill", false, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    BooleanSetting toggle = new BooleanSetting("Toggle", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);

    @Override
    public void onUpdate() {
        List<BlockPos> holes = findHoles();
        BlockPos posToFill = null;
        if (holes.isEmpty() && toggle.getValue()) {
            this.disable();
            return;
        }
        for (int i = 0; i < holesPerSecond.getValue(); i++) {
            double bestDistance = 10;
            for (BlockPos pos : new ArrayList<>(holes)) {
                BlockUtil.ValidResult result = BlockUtil.valid(pos);
                if (result != BlockUtil.ValidResult.Ok) {
                    holes.remove(pos);
                    continue;
                }
                if (!fillMode.is("Normal")) {
                    for (EntityPlayer target : mc.world.playerEntities) {
                        double distance = target.getDistance(pos.getX(), pos.getY(), pos.getZ());
                        if (target == mc.player) continue;
                        if (WurstplusThree.FRIEND_MANAGER.isFriend(mc.player.getName())) continue;
                        if (distance > (fillMode.is("Auto") ? smartRange.getValue() : 5)) continue;
                        if (distance < bestDistance) {
                            posToFill = pos;
                        }
                    }
                } else {
                    posToFill = pos;
                }
            }
            if (PlayerUtil.findObiInHotbar() == -1) {
                return;
            }
            if (posToFill != null) {
                BlockUtil.placeBlock(posToFill, PlayerUtil.findObiInHotbar(), rotate.getValue(), rotate.getValue(), swing);
                holes.remove(posToFill);
            }
        }
    }

    public List<BlockPos> findHoles() {
        int range = this.range.getValue();

        HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);
        List<BlockPos> holes = new ArrayList<>();

        for (BlockPos pos : blockPosList) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                possibleHoles.add(pos);
            }
        }

        for (BlockPos pos : possibleHoles) {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {

                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null)
                    continue;
                if (holeType == HoleUtil.HoleType.DOUBLE && !doubleHoles.getValue())
                    continue;

                holes.add(pos);
            }
        }

        return holes;
    }

}
