package me.travis.wurstplusthree.hack.hacks.render;

import io.netty.util.internal.ConcurrentSet;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

@Hack.Registration(name = "Void Esp", description = "see where the void is", category = Hack.Category.RENDER, isListening = false)
public class VoidESP extends Hack {

    IntSetting range = new IntSetting("Range", 10, 0, 50, this);
    IntSetting yLevel = new IntSetting("SlefY", 20, 0, 255, this);
    EnumSetting mode = new EnumSetting("Render","Pretty",  Arrays.asList("Pretty", "Solid", "Outline"), this);
    ColourSetting colour = new ColourSetting("Colour", new Colour(200, 255, 200, 100), this);

    private final ConcurrentSet<BlockPos> voidHoles = new ConcurrentSet<>();

    @Override
    public void onUpdate() {
        if(nullCheck())return;
        voidHoles.clear();

        if (mc.player.dimension == 1) {
            return;
        }
        if (mc.player.getPosition().getY() > yLevel.getValue()) {
            return;
        }

        List<BlockPos> blockPosList = BlockUtil.getCircle(PlayerUtil.getPlayerPos(), 0, range.getValue(), false);

        for (BlockPos blockPos : blockPosList) {
            if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK)) {
                continue;
            }
            if (isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            voidHoles.add(blockPos);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
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

        for (BlockPos hole : this.voidHoles) {
            RenderUtil.drawBoxESP(hole, colour.getValue(), colour.getValue(), 2f, outline, solid, true);
        }
    }

    private boolean isAnyBedrock(BlockPos origin, BlockPos[] offset) {
        for (BlockPos pos : offset) {
            if (mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }

    private static class Offsets {
        static final BlockPos[] center = {
                new BlockPos(0, 0, 0),
                new BlockPos(0, 1, 0),
                new BlockPos(0, 2, 0)
        };
    }

    @Override
    public void onEnable() {
        voidHoles.clear();
    }
}
