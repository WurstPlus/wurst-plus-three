package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Hack.Registration(name = "Surround", description = "surrounds u", category = Hack.Category.COMBAT, isListening = false)
public class Surround extends Hack {

    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);
    BooleanSetting hybrid = new BooleanSetting("Hybrid", true, this);
    BooleanSetting packet = new BooleanSetting("Packet", true, this);
    BooleanSetting center = new BooleanSetting("Center", true, this);
    BooleanSetting blockHead = new BooleanSetting("Block Face", false, this);
    IntSetting tickForPlace = new IntSetting("Blocks Per Tick", 2, 1, 8, this);
    IntSetting timeoutTicks = new IntSetting("Timeout Ticks", 20, 0, 50, this);

    private int yLevel = 0;
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set<Vec3d> extendingBlocks = new HashSet<>();
    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private BlockPos startPos;
    private boolean didPlace = false;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private boolean offHand = false;
    private int ticksPassed = 0;

    @Override
    public void onEnable() {
        if (mc.player != null) {
            yLevel = (int) Math.round(mc.player.posY);
            this.startPos = EntityUtil.getRoundedBlockPos(Surround.mc.player);
            if (center.getValue()) {
                double y = mc.player.getPosition().getY();
                double x = mc.player.getPosition().getX();
                double z = mc.player.getPosition().getZ();

                Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
                Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
                Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
                Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);
                if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(minusPlus)) {
                    x = mc.player.getPosition().getX() + 0.5;
                    z = mc.player.getPosition().getZ() + 0.5;
                    centerPlayer(x, y, z);
                }
                if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(minusPlus)) {
                    x = mc.player.getPosition().getX() + 0.5;
                    z = mc.player.getPosition().getZ() - 0.5;
                    centerPlayer(x, y, z);
                }
                if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(minusPlus)) {
                    x = mc.player.getPosition().getX() - 0.5;
                    z = mc.player.getPosition().getZ() - 0.5;
                    centerPlayer(x, y, z);
                }
                if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(minusMinus)) {
                    x = mc.player.getPosition().getX() - 0.5;
                    z = mc.player.getPosition().getZ() + 0.5;
                    centerPlayer(x, y, z);
                }
            }
            this.ticksPassed = 0;
            this.retries.clear();
            this.retryTimer.reset();
            this.lastHotbarSlot = mc.player.inventory.currentItem;
        }
    }

    @Override
    public void onTick() {
        if ((int) Math.round(mc.player.posY) != yLevel && hybrid.getValue()) {
            this.disable();
            return;
        }
        this.doFeetPlace();
        this.ticksPassed++;
    }

    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        if (!EntityUtil.isSafe(mc.player, 0, true)) {
            this.placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(mc.player, 0, true), true, false, false);
        } else if (!EntityUtil.isSafe(mc.player, -1, false)) {
            this.placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(mc.player, -1, false), false, false, true);
        }
        if (this.blockHead.getValue()) {
            this.placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(mc.player, 1, false), false, false, false);
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (Vec3d extendingBlock : this.extendingBlocks) {
                array[i] = extendingBlock;
                ++i;
            }
            int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, true), true, false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
            this.extendingBlocks.clear();
        }
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : EntityUtil.getUnsafeBlockArray(mc.player, 0, true)) {
                if (!vec3d.equals(pos)) continue;
                ++matches;
            }
        }
        if (matches == 2) {
            return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        boolean gotHelp;
        block5:
        for (Vec3d vec3d : vec3ds) {
            gotHelp = true;
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, false)) {
                case 1: {
                    if (this.retries.get(position) == null || this.retries.get(position) < 4) {
                        this.placeBlock(position);
                        this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
                        this.retryTimer.reset();
                        continue block5;
                    }
                    if (mc.player.motionX != 0 || mc.player.motionZ != 0 || isExtending || this.extenders >= 1) continue block5;
                    this.placeBlocks(mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    continue block5;
                }
                case 2: {
                    if (!hasHelpingBlocks) continue block5;
                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (!isHelping) continue block5;
                    return true;
                }
            }
        }
        return false;
    }

    private double getDst(Vec3d vec) {
        return mc.player.getPositionVector().distanceTo(vec);
    }

    private void centerPlayer(double x, double y, double z) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }

    private boolean check() {
        if (nullCheck()) {
            return true;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
            return true;
        }
        this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        int obbySlot1 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (obbySlot1 == -1 && !this.offHand && echestSlot == -1) {
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot1 && mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = mc.player.inventory.currentItem;
        }
        if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
            this.disable();
            return true;
        }
        return ticksPassed > timeoutTicks.getValue() && !hybrid.getValue();
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.tickForPlace.getValue()) {
            int originalSlot = mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }
            mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking);
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
            this.didPlace = true;
            this.placements++;
        }
    }

    public Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }

}
