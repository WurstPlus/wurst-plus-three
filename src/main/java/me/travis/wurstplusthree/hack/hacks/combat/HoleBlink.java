package me.travis.wurstplusthree.hack.hacks.combat;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

@Hack.Registration(name = "Hole Blink", description = "walks to hole", category = Hack.Category.COMBAT, priority = HackPriority.High)
public class HoleBlink extends Hack {
    private BooleanSetting blink = new BooleanSetting("Blink", true, this);
    private DoubleSetting range = new DoubleSetting("MaxRange", 2.0, 0.0, 20.0, this);
    private EnumSetting priority = new EnumSetting("Priority", "Middle", Arrays.asList("Close", "Far", "Middle"), this);
    BlockPos target;

    @Override
    public void onEnable() {
        if (BaritoneAPI.getProvider().getPrimaryBaritone() == null) {
            ClientMessage.sendMessage("No baritone found, get baritone on");
            ClientMessage.sendMessage("https://github.com/cabaletta/baritone");
            this.disable();
        }
        target = null;
        for (BlockPos pos : EntityUtil.getSphere(mc.player.getPosition(), range.getValue().floatValue(), range.getValue().intValue(), false, true, 0)) {
            if (getBlock(pos) instanceof BlockAir && getBlock(pos.up()) instanceof BlockAir && getBlock(pos.up().up()) instanceof BlockAir && HoleUtil.isHole(pos, true, false).getType() != HoleUtil.HoleType.NONE && PlayerUtil.getPlayerPos().getX() != pos.getX() && PlayerUtil.getPlayerPos().getY() != pos.getY() && PlayerUtil.getPlayerPos().getZ() != pos.getZ())  {
                if (priority.is("Far")) {
                    if (target == null || (mc.player.getDistanceSq(target) < mc.player.getDistanceSq(pos) && Math.sqrt(mc.player.getDistanceSq(pos)) < range.getValue()))
                        target = pos;
                } else if (priority.is("Close")) {
                    if (target == null || mc.player.getDistanceSq(target) > mc.player.getDistanceSq(pos))
                        target = pos;
                } else if (priority.is("Middle")) {
                    if (target == null || Math.abs(Math.sqrt(mc.player.getDistanceSq(pos)) - range.getValue() / 2) < Math.abs(Math.sqrt(mc.player.getDistanceSq(target)) - range.getValue() / 2))
                        target = pos;
                }
            }
        }
        if (target != null) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(target));
            if (blink.getValue())
                WurstplusThree.HACKS.enablehack("Blink");

        } else {
            ClientMessage.sendMessage("Couldn't find hole");
            this.disable();
        }
    }

    private Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    @Override
    public void onDisable() {
        if (blink.getValue())
            WurstplusThree.HACKS.disablehack("Blink");
        if (BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive())
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoal(null);
    }

    @Override
    public void onUpdate() {
        if (!BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
            ClientMessage.sendMessage("Reached target... or failed to reach it idk");
            this.disable();
            return;
        }
    }
}