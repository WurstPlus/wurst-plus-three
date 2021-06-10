package me.travis.wurstplusthree.util;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BotUtil implements Globals {

    public static boolean isAheadStepable() {
        return true;
    }

    public static boolean isAheadJumpable() {
        BlockPos currentGround = PlayerUtil.getPlayerPos().down();
        Direction currentDirection = getDirection();
        if (!canJumpOverBlock(currentGround)) {
            return false;
        }
        switch (currentDirection) {
            case NORTH:
                if (mc.world.getBlockState(currentGround.north()).getBlock() == Blocks.AIR) {
                    BlockPos checkPos = currentGround.north();
                    if (!canJumpOverBlock(checkPos)) {
                        return false;
                    }
                    for (int i = 0; i < 3; i++) {
                        checkPos = checkPos.north();
                        if (mc.world.getBlockState(checkPos.north()).getBlock() != Blocks.AIR) {
                            return true;
                        }
                        if (!canJumpOverBlock(checkPos)) {
                            return false;
                        }
                    }
                }
                return false;
            case EAST:
                if (mc.world.getBlockState(currentGround.east()).getBlock() == Blocks.AIR) {
                    BlockPos checkPos = currentGround.east();
                    if (!canJumpOverBlock(checkPos)) {
                        return false;
                    }
                    for (int i = 0; i < 3; i++) {
                        checkPos = checkPos.east();
                        if (mc.world.getBlockState(checkPos.east()).getBlock() != Blocks.AIR) {
                            return true;
                        }
                        if (!canJumpOverBlock(checkPos)) {
                            return false;
                        }
                    }
                }
                return false;
            case SOUTH:
                if (mc.world.getBlockState(currentGround.south()).getBlock() == Blocks.AIR) {
                    BlockPos checkPos = currentGround.south();
                    if (!canJumpOverBlock(checkPos)) {
                        return false;
                    }
                    for (int i = 0; i < 3; i++) {
                        checkPos = checkPos.south();
                        if (mc.world.getBlockState(checkPos.south()).getBlock() != Blocks.AIR) {
                            return true;
                        }
                        if (!canJumpOverBlock(checkPos)) {
                            return false;
                        }
                    }
                }
                return false;
            case WEST:
                if (mc.world.getBlockState(currentGround.west()).getBlock() == Blocks.AIR) {
                    BlockPos checkPos = currentGround.west();
                    if (!canJumpOverBlock(checkPos)) {
                        return false;
                    }
                    for (int i = 0; i < 3; i++) {
                        checkPos = checkPos.west();
                        if (mc.world.getBlockState(checkPos.west()).getBlock() != Blocks.AIR) {
                            return true;
                        }
                        if (!canJumpOverBlock(checkPos)) {
                            return false;
                        }
                    }
                }
                return false;
        }
        return false;
    }

/*    private static boolean canFitAboveBlock(BlockPos pos) {
        return mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR;
    }*/

    private static boolean canJumpOverBlock(BlockPos pos) {
        return mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR
                && mc.world.getBlockState(pos.up().up().up()).getBlock() == Blocks.AIR;
    }

    private static Direction getDirection() {
        int dirnumber = MathHelper.floor((double) (RotationUtil.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
        if (dirnumber == 0) {
            return Direction.SOUTH;
        }
        if (dirnumber == 1) {
            return Direction.WEST;
        }
        if (dirnumber == 2) {
            return Direction.NORTH;
        }
        return Direction.EAST;
    }

    enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

}
