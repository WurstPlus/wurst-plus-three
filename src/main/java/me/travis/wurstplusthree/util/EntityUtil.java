package me.travis.wurstplusthree.util;

import me.travis.wurstplusthree.WurstplusThree;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
        return EntityUtil.isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return EntityUtil.isBedrockHole(blockPos) || EntityUtil.isObbyHole(blockPos) || EntityUtil.isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = EntityUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.OBSIDIAN) continue;
            return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = EntityUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && touchingState.getBlock() == Blocks.BEDROCK) continue;
            return false;
        }
        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks;
        for (BlockPos pos : touchingBlocks = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()}) {
            IBlockState touchingState = EntityUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.AIR && (touchingState.getBlock() == Blocks.BEDROCK || touchingState.getBlock() == Blocks.OBSIDIAN))
                continue;
            return false;
        }
        return true;
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return EntityUtil.getInterpolatedPos(entity, partialTicks).subtract(EntityUtil.mc.getRenderManager().renderPosX, EntityUtil.mc.getRenderManager().renderPosY, EntityUtil.mc.getRenderManager().renderPosZ);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(EntityUtil.getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return EntityUtil.getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static boolean isLiving(final Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos((EntityUtil.mc.player.getRidingEntity() != null) ? EntityUtil.mc.player.getRidingEntity().posX : EntityUtil.mc.player.posX, (EntityUtil.mc.player.getRidingEntity() != null) ? EntityUtil.mc.player.getRidingEntity().posY : EntityUtil.mc.player.posY, (EntityUtil.mc.player.getRidingEntity() != null) ? EntityUtil.mc.player.getRidingEntity().posZ : EntityUtil.mc.player.posZ);
    }

    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static java.util.List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
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

}
