package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.CrystalUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

@Hack.Registration(name = "Target Details", description = "shows status of dude", category = Hack.Category.RENDER, isListening = false)
public class TargetDetails extends Hack {

    BooleanSetting showFucked = new BooleanSetting("Fucked", true, this);
    BooleanSetting onePointT = new BooleanSetting("1.13+", false, this);
    BooleanSetting showBurrow = new BooleanSetting("Burrowed", true, this);

    ColourSetting fuckedColour = new ColourSetting("Fucked Colour", new Colour(255, 20, 20, 150), this, s -> showFucked.getValue());
    ColourSetting burrowedColour = new ColourSetting("Burrowed Colour", new Colour(20, 255, 255, 150), this, s -> showBurrow.getValue());
    EnumSetting mode = new EnumSetting("Render","Pretty",  Arrays.asList("Pretty", "Solid", "Outline"), this);

    private final ArrayList<BlockPos> fuckedBlocks = new ArrayList<>();
    private final ArrayList<BlockPos> burrowedBlocks = new ArrayList<>();

    @Override
    public void onEnable() {
        fuckedBlocks.clear();
        burrowedBlocks.clear();
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        this.fuckedBlocks.clear();
        this.burrowedBlocks.clear();
        this.getFuckedPlayers();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (showFucked.getValue()) {
            if (!this.fuckedBlocks.isEmpty()) {
                this.fuckedBlocks.forEach(this::renderFuckedBlock);
            }
        }
        if (showBurrow.getValue()) {
            if (!this.burrowedBlocks.isEmpty()) {
                this.burrowedBlocks.forEach(this::renderBurrowedBlock);
            }
        }
    }

    private void renderBurrowedBlock(BlockPos pos) {
        Colour color = burrowedColour.getValue();

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

        RenderUtil.drawBoxESP(pos, color, color, 2f, outline, solid, true);
    }

    private void renderFuckedBlock(BlockPos pos) {
        Colour color = fuckedColour.getValue();

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

        RenderUtil.drawBoxESP(pos, color, color, 2f, outline, solid, true);
    }

    private void getFuckedPlayers() {
        fuckedBlocks.clear();
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player || WurstplusThree.FRIEND_MANAGER.isFriend(player.getName()) || !EntityUtil.isLiving(player)) continue;
            if (this.isBurrowed(player)) {
                this.burrowedBlocks.add(new BlockPos(player.posX, player.posY, player.posZ));
            } else if (this.isFucked(player)) {
                this.fuckedBlocks.add(new BlockPos(player.posX, player.posY, player.posZ));
            }
        }
    }

    private boolean isFucked(EntityPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY - 1, player.posZ);
        if (CrystalUtil.canPlaceCrystal(pos.south(), true, onePointT.getValue()) || (CrystalUtil.canPlaceCrystal(pos.south().south(), true, onePointT.getValue()) && mc.world.getBlockState(pos.add(0, 1, 1)).getBlock() == Blocks.AIR)) {
            return true;
        }
        if (CrystalUtil.canPlaceCrystal(pos.east(), true, onePointT.getValue()) || (CrystalUtil.canPlaceCrystal(pos.east().east(), true, onePointT.getValue()) && mc.world.getBlockState(pos.add(1, 1, 0)).getBlock() == Blocks.AIR)) {
            return true;
        }
        if (CrystalUtil.canPlaceCrystal(pos.west(), true, onePointT.getValue()) || (CrystalUtil.canPlaceCrystal(pos.west().west(), true, onePointT.getValue()) && mc.world.getBlockState(pos.add(-1, 1, 0)).getBlock() == Blocks.AIR)) {
            return true;
        }
        return CrystalUtil.canPlaceCrystal(pos.north(), true, onePointT.getValue()) || (CrystalUtil.canPlaceCrystal(pos.north().north(), true, onePointT.getValue()) && mc.world.getBlockState(pos.add(0, 1, -1)).getBlock() == Blocks.AIR);

    }

    private boolean isBurrowed(EntityPlayer player) {
        BlockPos pos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY+0.2), Math.floor(player.posZ));
        return mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.CHEST;
    }

}
