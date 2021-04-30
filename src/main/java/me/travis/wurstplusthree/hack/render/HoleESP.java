package me.travis.wurstplusthree.hack.render;

import com.google.common.collect.Sets;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.HoleUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Hack.Registration(name = "Hole ESP", description = "shows holes", category = Hack.Category.RENDER, isListening = false)
public class HoleESP extends Hack {

    IntSetting range = new IntSetting("Range", 5, 1, 20, this);
    EnumSetting customHoles = new EnumSetting("Show", "Single", Arrays.asList("Single", "Double"), this);
    EnumSetting mode = new EnumSetting("Render","Pretty",  Arrays.asList("Pretty", "Solid", "Outline"), this);
    BooleanSetting hideOwn = new BooleanSetting("Hide Own", false, this);
    ColourSetting bedrockColor = new ColourSetting("Bedrock Color", new Colour(0, 255, 0, 100), this);
    ColourSetting obsidianColor = new ColourSetting("Obsidian Color", new Colour(255, 0, 0, 100), this);

    private final ConcurrentHashMap<BlockPos, Colour> holes = new ConcurrentHashMap<>();

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.holes.isEmpty()) return;
        this.holes.forEach(this::renderHoles);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        this.holes.clear();
        int range = this.range.getValue();

        HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);

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

        possibleHoles.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {

                HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null)
                    return;

                Colour colour;
                if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                    colour = bedrockColor.getValue();
                } else {
                    colour = obsidianColor.getValue();
                }

                if (customHoles.is("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                    holes.put(pos, colour);
                } else if (customHoles.is("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                    holes.put(pos, colour);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    holes.put(pos, colour);
                }
            }
        });
    }

    private void renderHoles(BlockPos hole, Colour color) {
        if (hideOwn.getValue() && hole.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
            return;
        }

        boolean outline = false;
        boolean solid = false;

        if (mode.is("Pretty")) {
            outline = true;
            solid   = true;
        }

        if (mode.is("Solid")) {
            outline = false;
            solid   = true;
        }

        if (mode.is("Outline")) {
            outline = true;
            solid   = false;
        }

        RenderUtil.drawBoxESP(hole, color, color, 2f, outline, solid, true);

    }

    @Override
    public String getDisplayInfo() {
        return "" + holes.size();
    }

}
