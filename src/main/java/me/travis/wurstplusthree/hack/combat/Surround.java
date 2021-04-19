package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class Surround extends Hack {

    public Surround() {
        super("Surround", "Surrounds u", Category.COMBAT, false, false);
    }

    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    BooleanSetting hybrid = new BooleanSetting("Hybrid", true, this);
    BooleanSetting toggle = new BooleanSetting("Toggle", true, this);
    BooleanSetting center = new BooleanSetting("Center", false, this);
    BooleanSetting blockHead = new BooleanSetting("Block Face", false, this);
    IntSetting tickForPlace = new IntSetting("Blocks Per Tick", 2, 1, 8, this);
    IntSetting timeoutTicks = new IntSetting("Timeout Ticks", 20, 10, 50, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);

    private int yLevel = 0;
    private int tickRuns = 0;
    private int offsetStep = 0;

    private Vec3d centerBlock = Vec3d.ZERO;

    Vec3d[] surroundTargets = {
            new Vec3d(  1,   0,   0),
            new Vec3d(  0,   0,   1),
            new Vec3d(- 1,   0,   0),
            new Vec3d(  0,   0, - 1),
            new Vec3d(  1, - 1,   0),
            new Vec3d(  0, - 1,   1),
            new Vec3d(- 1, - 1,   0),
            new Vec3d(  0, - 1, - 1),
            new Vec3d(  0, - 1,   0)
    };

    Vec3d[] surroundTargetsFace = {
            new Vec3d(  1,   1,   0),
            new Vec3d(  0,   1,   1),
            new Vec3d(- 1,   1,   0),
            new Vec3d(  0,   1, - 1),
            new Vec3d(  1,   0,   0),
            new Vec3d(  0,   0,   1),
            new Vec3d(- 1,   0,   0),
            new Vec3d(  0,   0, - 1),
            new Vec3d(  1, - 1,   0),
            new Vec3d(  0, - 1,   1),
            new Vec3d(- 1, - 1,   0),
            new Vec3d(  0, - 1, - 1),
            new Vec3d(  0, - 1,   0)
    };

    @Override
    public void onEnable() {
        if (mc.player != null) {
            yLevel = (int) Math.round(mc.player.posY);
            centerBlock = this.getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);

            if (center.getValue()) {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
        }
    }

    private boolean disableCheck() {
        if (PlayerUtil.findObiInHotbar() == -1 || nullCheck()) {
            this.disable();
            return true;
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (this.disableCheck()) {
            return;
        }
        if (centerBlock != Vec3d.ZERO && center.getValue()) {
            double x_diff = Math.abs(centerBlock.x - mc.player.posX);
            double z_diff = Math.abs(centerBlock.z - mc.player.posZ);
            if (x_diff <= 0.1 && z_diff <= 0.1) {
                centerBlock = Vec3d.ZERO;
            } else {
                double motion_x = centerBlock.x - mc.player.posX;
                double motion_z = centerBlock.z - mc.player.posZ;
                mc.player.motionX = motion_x / 2;
                mc.player.motionZ = motion_z / 2;
            }
        }

        if ((int) Math.round(mc.player.posY) != yLevel && this.hybrid.getValue()) {
            this.disable();
            return;
        }

        if (!this.toggle.getValue() && this.tickRuns >= this.timeoutTicks.getValue()) {
            this.tickRuns = 0;
            this.disable();
            return;
        }

        int blocks_placed = 0;

        while (blocks_placed < this.tickForPlace.getValue()) {

            if (this.offsetStep >= (blockHead.getValue() ? this.surroundTargetsFace.length : this.surroundTargets.length)) {
                this.offsetStep = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(blockHead.getValue() ? this.surroundTargetsFace[offsetStep]
                    : this.surroundTargets[offsetStep]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(),
                    offsetPos.getY(), offsetPos.getZ());

            boolean try_to_place = true;

            if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                try_to_place = false;
            }

            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                try_to_place = false;
                break;
            }

            if (try_to_place && BlockUtil.placeBlock(targetPos, PlayerUtil.findObiInHotbar(), rotate.getValue(), rotate.getValue(), swing)) {
                blocks_placed++;
            }

            offsetStep++;

        }

        this.tickRuns++;
    }

    public Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

}
