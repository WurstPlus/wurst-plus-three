package me.travis.wurstplusthree.hack.misc;

import io.netty.util.internal.ConcurrentSet;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.travis.wurstplusthree.util.PlayerUtil.getPlayerPos;

/**
 * @author Madmegsox1
 * @since 28/04/2021
 *  - not china code i promise!
 */
@Hack.Registration(name = "Anti Void", description = "stops dumb ppl (you) falling into the void", category = Hack.Category.MISC, isListening = false)
public class AntiVoid extends Hack {

    private ConcurrentSet<BlockPos> voidHoles;

    public void onUpdate(){
        if (mc.player.getPosition().getY() > 1) {
            return;
        }
        if (voidHoles == null) {
            voidHoles = new ConcurrentSet<>();
        }
        else {
            voidHoles.clear();
        }

        List<BlockPos> blockPosList = PlayerUtil.getSphere(getPlayerPos(), 10, 6, false, true, 0);

        for (BlockPos blockPos : blockPosList) {
            if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN)) {
                continue;
            }
            if (isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            voidHoles.add(blockPos);
        }


        if(voidHoles.contains(new BlockPos(getPlayerPos().getX(), getPlayerPos().getY() - 2, getPlayerPos().getZ()))){
            mc.player.motionY = 0.1;
        }
    }

    private boolean isAnyBedrock(BlockPos origin, BlockPos[] offset) {
        for (BlockPos pos : offset) {
            if (mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(origin.add(pos)).getBlock().equals(Blocks.OBSIDIAN)) {
                return true;
            }
        } return false;
    }

    private static class Offsets {
        static final BlockPos[] center = {
                new BlockPos(0, 0, 0),
                new BlockPos(0, 1, 0),
                new BlockPos(0, 2, 0)
        };
    }
}
