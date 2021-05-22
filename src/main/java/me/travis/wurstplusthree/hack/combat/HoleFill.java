package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Hack.Registration(name = "Hole Fill", description = "fills holes", category = Hack.Category.COMBAT, isListening = false)
public class HoleFill extends Hack {

    IntSetting range = new IntSetting("Range", 3, 1, 6, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    BooleanSetting toggle = new BooleanSetting("Toggle", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    EnumSetting mode = new EnumSetting("Render", "None", Arrays.asList("None", "Pretty", "Solid", "Outline"), this);
    ColourSetting renderFillColour = new ColourSetting("Fill Colour", new Colour(0, 0, 0, 255), this);
    ColourSetting renderBoxColour = new ColourSetting("Air Colour", new Colour(255, 255, 255, 255), this);

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
        if (holes.isEmpty() && toggle.getValue()) {
            this.disable();
            return;
        } else {
            this.findNewHoles();
        }
        double bestDistance = 10;
        for (BlockPos pos : new ArrayList<>(holes)) {
            for (EntityPlayer target : mc.world.playerEntities) {
                if (pos == null) continue;
                double distance = target.getDistance(pos.getX(), pos.getY(), pos.getZ());
                if (distance > 4) continue;
                BlockUtil.ValidResult result = BlockUtil.valid(pos);
                if (result != BlockUtil.ValidResult.Ok) {
                    holes.remove(pos);
                    continue;
                }
                if (distance < bestDistance) {
                    posToFill = pos;
                }
            }
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
        for (BlockPos pos : EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range.getValue(), range.getValue(), false, true, 0)) {
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
            for (BlockPos seems_blocks : new BlockPos[]{
                    new BlockPos(0, -1, 0),
                    new BlockPos(0, 0, -1),
                    new BlockPos(1, 0, 0),
                    new BlockPos(0, 0, 1),
                    new BlockPos(-1, 0, 0)
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

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.holes.isEmpty() || mode.is("None")) return;
        boolean outline = false;
        boolean solid = false;
        switch (mode.getValue()) {
            case "Pretty":
                outline = true;
                solid = true;
                break;
            case "Solid":
                outline = false;
                solid = true;
                break;
            case "Outline":
                outline = true;
                solid = false;
                break;
        }
        for (BlockPos renderBlock : holes) {
            if(mc.world.getBlockState(renderBlock).getBlock() == Blocks.AIR) {
                RenderUtil.drawBoxESP(renderBlock, new Colour(renderBoxColour.getValue().getRed(), renderBoxColour.getValue().getGreen(), renderBoxColour.getValue().getBlue(), 40),
                        renderBoxColour.getValue()
                        , 1, outline, solid, true);
            }
            else {
                RenderUtil.drawBoxESP(renderBlock, new Colour(renderFillColour.getValue().getRed(), renderFillColour.getValue().getGreen(), renderFillColour.getValue().getBlue(), 40),
                        renderFillColour.getValue()
                        , 1, outline, solid, true);
            }
        }
    }

}
