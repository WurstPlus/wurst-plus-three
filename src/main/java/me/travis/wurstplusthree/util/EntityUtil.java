package me.travis.wurstplusthree.util;

import me.travis.wurstplusthree.WurstplusThree;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityUtil implements Globals {

    public static boolean canEntityFeetBeSeen(final Entity entityIn) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posX + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static boolean isAboveBlock(final Entity entity, final BlockPos blockPos) {
        return entity.posY >= blockPos.getY();
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static boolean isAboveLiquid(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double n = entity.posY + 0.01;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (EntityUtil.mc.world.getBlockState(new BlockPos(i, (int) n, j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkForLiquid(final Entity entity, final boolean b) {
        if (entity == null) {
            return false;
        }
        final double posY = entity.posY;
        double n;
        if (b) {
            n = 0.03;
        } else if (entity instanceof EntityPlayer) {
            n = 0.2;
        } else {
            n = 0.5;
        }
        final double n2 = posY - n;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (EntityUtil.mc.world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkCollide() {
        return !mc.player.isSneaking() && (mc.player.getRidingEntity() == null || mc.player.getRidingEntity().fallDistance < 3.0f) && mc.player.fallDistance < 3.0f;
    }

    public static boolean isOnLiquid(final double offset) {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }
        final AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0) : mc.player.getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0);
        boolean onLiquid = false;
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0); ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0); ++z) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathsUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && mc.player != null) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static void attackEntity(final Entity entity, final boolean packet, final boolean swingArm) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
        if (swingArm) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    public static void attackEntity(final Entity entity, final boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
    }

    public static BlockPos getFlooredPos(Entity e) {
        return new BlockPos(Math.floor(e.posX), Math.floor(e.posY), Math.floor(e.posZ));
    }

    public static boolean isInHole(Entity entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        for (BlockPos pos : new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.OBSIDIAN) continue;
            return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        for (BlockPos pos : new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.BEDROCK) continue;
            return false;
        }
        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        for (BlockPos pos : new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN))
                continue;
            return false;
        }
        return true;
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static boolean isLiving(final Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos((mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().posX : mc.player.posX, (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().posY : mc.player.posY, (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().posZ : mc.player.posZ);
    }

    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static java.util.List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plusY, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    public static boolean holding32k(final EntityPlayer player) {
        return is32k(player.getHeldItemMainhand());
    }

    public static boolean is32k(final ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getTagCompound() == null) {
            return false;
        }
        final NBTTagList enchants = (NBTTagList) stack.getTagCompound().getTag("ench");
        if (enchants == null) {
            return false;
        }
        int i = 0;
        while (i < enchants.tagCount()) {
            final NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                final int lvl = enchant.getInteger("lvl");
                if (lvl >= 42) {
                    return true;
                }
                break;
            } else {
                ++i;
            }
        }
        return false;
    }

    public static float getHealth(final Entity entity) {
        if (entity.isEntityAlive()) {
            final EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static boolean isntValid(final Entity entity, final double range) {
        return entity == null || !entity.isEntityAlive() || entity.equals(mc.player) || (entity instanceof EntityPlayer && WurstplusThree.FRIEND_MANAGER.isFriend(entity.getName())) || mc.player.getDistanceSq(entity) > (range * range);
    }

    public static Color getColor(final Entity entity, final int red, final int green, final int blue, final int alpha, final boolean colorFriends) {
        Color color = new Color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
        if (entity instanceof EntityPlayer) {
            if (colorFriends && WurstplusThree.FRIEND_MANAGER.isFriend(entity.getName())) {
                color = new Color(0.33f, 1.0f, 1.0f, alpha / 255.0f);
            }
            if (colorFriends && WurstplusThree.ENEMY_MANAGER.isEnemy(entity.getName())) {
                color = new Color(1, 0.33f, 1.0f, alpha / 255.0f);
            }
        }
        return color;
    }

    public static void setTimer(float speed) {
        mc.timer.tickLength = 50.0f / speed;
    }

    public static void resetTimer() {
        mc.timer.tickLength = 50;
    }

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (mc.player != null) {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }
        return block;
    }

    public static boolean isInLiquid() {
        if (mc.player != null) {
            if (mc.player.fallDistance >= 3.0f) {
                return false;
            }
            boolean inLiquid = false;
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
            return inLiquid;
        }
        return false;
    }

    public static double sigmoid(final double x){
        return (1/(1+Math.pow(Math.E, (-1*x))));
    }

    public static double predictPos(final double motion, final double threshold){
        double m = Math.abs(motion);
        m = m - threshold;
        m = sigmoid(m);
        return m;
    }


}
