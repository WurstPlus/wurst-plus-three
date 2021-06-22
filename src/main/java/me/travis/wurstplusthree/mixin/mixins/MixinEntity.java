package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PushEvent;
import me.travis.wurstplusthree.event.events.StepEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(value={Entity.class})
public abstract class MixinEntity {
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    public boolean noClip;
    @Shadow
    public float prevDistanceWalkedModified;
    @Shadow
    public World world;
    @Shadow
    @Final
    private double[] pistonDeltas;
    @Shadow
    private long pistonDeltasGameTime;
    @Shadow
    protected boolean isInWeb;
    @Shadow
    public float stepHeight;
    @Shadow
    public boolean collidedHorizontally;
    @Shadow
    public boolean collidedVertically;
    @Shadow
    public boolean collided;
    @Shadow
    public float distanceWalkedModified;
    @Shadow
    public float distanceWalkedOnStepModified;
    @Shadow
    private int fire;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private float nextFlap;
    @Shadow
    protected Random rand;

    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract boolean isRiding();

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract void setEntityBoundingBox(AxisAlignedBB var1);

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Shadow
    public abstract void resetPositionToBB();

    @Shadow
    protected abstract void updateFallState(double var1, boolean var3, IBlockState var4, BlockPos var5);

    @Shadow
    protected abstract boolean canTriggerWalking();

    @Shadow
    public abstract boolean isInWater();

    @Shadow
    public abstract boolean isBeingRidden();

    @Shadow
    public abstract Entity getControllingPassenger();

    @Shadow
    public abstract void playSound(SoundEvent var1, float var2, float var3);

    @Shadow
    protected abstract void doBlockCollisions();

    @Shadow
    public abstract boolean isWet();

    @Shadow
    protected abstract void playStepSound(BlockPos var1, Block var2);

    @Shadow
    protected abstract SoundEvent getSwimSound();

    @Shadow
    protected abstract float playFlySound(float var1);

    @Shadow
    protected abstract boolean makeFlySound();

    @Shadow
    public abstract void addEntityCrashInfo(CrashReportCategory var1);

    @Shadow
    protected abstract void dealFireDamage(int var1);

    @Shadow
    public abstract void setFire(int var1);

    @Shadow
    protected abstract int getFireImmuneTicks();

    @Shadow
    public abstract boolean isBurning();

    @Shadow
    public abstract int getMaxInPortalTime();

    @Redirect(method={"onEntityUpdate"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;getMaxInPortalTime()I"))
    private int getMaxInPortalTimeHook(Entity entity) {
        return this.getMaxInPortalTime();
    }

    @Redirect(method={"applyEntityCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(Entity entity, double x, double y, double z) {
        PushEvent event = new PushEvent(entity, x, y, z, true);
        WurstplusThree.EVENT_PROCESSOR.addEventListener(event);
        if (!event.isCancelled()) {
            entity.motionX += event.x;
            entity.motionY += event.y;
            entity.motionZ += event.z;
            entity.isAirBorne = event.airbone;
        }
    }
}

