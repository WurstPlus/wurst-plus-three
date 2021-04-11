package me.travis.wurstplusthree.hack.render;

import com.google.common.collect.Sets;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HoleESP extends Hack {

    public HoleESP() {
        super("HoleESP", "Shows Holes", Category.RENDER, false, false);
    }

    IntSetting range = new IntSetting("Range", 5, 1, 20, this);
    EnumSetting customHoles = new EnumSetting("Show", "Single", Arrays.asList("Single", "Double"), this);
    EnumSetting type = new EnumSetting("Render","Both",  Arrays.asList("Outline", "Fill", "Both"), this);
    EnumSetting mode = new EnumSetting("Mode", "Air",  Arrays.asList("Air", "Ground", "Flat", "Slab", "Double"),this);
    BooleanSetting hideOwn = new BooleanSetting("Hide Own", false, this);
    DoubleSetting slabHeight = new DoubleSetting("Slab Height", 0.5, 0.1, 1.5, this);
    IntSetting width = new IntSetting("Width", 1, 1, 10, this);
    ColourSetting bedrockColor = new ColourSetting("Bedrock Color", new Colour(0, 255, 0), this);
    ColourSetting obsidianColor = new ColourSetting("Obsidian Color", new Colour(255, 0, 0), this);
    IntSetting ufoAlpha = new IntSetting("UFOAlpha", 255, 0, 255, this);

    private final ConcurrentHashMap<AxisAlignedBB, Colour> holes = new ConcurrentHashMap<>();

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
                    colour = new Colour(bedrockColor.getValue(), 255);
                } else {
                    colour = new Colour(obsidianColor.getValue(), 255);
                }

                if (customHoles.is("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                    holes.put(centreBlocks, colour);
                } else if (customHoles.is("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                    holes.put(centreBlocks, colour);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    holes.put(centreBlocks, colour);
                }
            }
        });
    }

    private void renderHoles(AxisAlignedBB hole, Colour color) {
        switch (type.getValue()) {
            case "Outline": {
                renderOutline(hole, color);
                break;
            }
            case "Fill": {
                renderFill(hole, color);
                break;
            }
            case "Both": {
                renderOutline(hole, color);
                renderFill(hole, color);
                break;
            }
        }
    }

    private void renderFill(AxisAlignedBB hole, Colour color) {
        Colour fillColor = new Colour(color, 50);
        int ufoAlpha = (this.ufoAlpha.getValue() * 50) / 255;

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryUtil.Quad.ALL);
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.offset(0, -1, 0), true, 1, new Colour(fillColor, ufoAlpha), fillColor.getAlpha(), GeometryUtil.Quad.ALL);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryUtil.Quad.DOWN);
                break;
            }
            case "Slab": {
                RenderUtil.drawBox(hole, false, slabHeight.getValue(), fillColor, ufoAlpha, GeometryUtil.Quad.ALL);
                break;
            }
            case "Double": {
                RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1), true, 2, fillColor, ufoAlpha, GeometryUtil.Quad.ALL);
                break;
            }
        }
    }

    private void renderOutline(AxisAlignedBB hole, Colour color) {
        Colour outlineColor = new Colour(color, 255);

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                RenderUtil.drawBoundingBox(hole, width.getValue(), outlineColor, ufoAlpha.getValue());
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.offset(0, -1, 0), width.getValue(), new Colour(outlineColor, ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryUtil.Quad.DOWN);
                break;
            }
            case "Slab": {
                RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + slabHeight.getValue()), width.getValue(), outlineColor, ufoAlpha.getValue());
                break;
            }
            case "Double": {
                RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1), width.getValue(), outlineColor, ufoAlpha.getValue());
                break;
            }
        }
    }

}
