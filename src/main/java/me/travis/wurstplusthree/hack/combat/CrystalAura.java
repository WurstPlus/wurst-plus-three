package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CrystalAura extends Hack {

    public CrystalAura() {
        super("CrystalAura", "the goods", Category.COMBAT, false, false);
    }

    BooleanSetting place = new BooleanSetting("Place", true, this);
    BooleanSetting breaK = new BooleanSetting("Break", true, this);
    BooleanSetting antiWeakness = new BooleanSetting("AntiWeakness", true, this);

    DoubleSetting breakRange = new DoubleSetting("BreakRange", 4.0, 1.0, 6.0, this);
    DoubleSetting placeRange = new DoubleSetting("PlaceRange", 4.0, 1.0, 6.0, this);
    DoubleSetting breakRangeWall = new DoubleSetting("BreakRangeWall", 3.0, 1.0, 6.0, this);
    DoubleSetting placeRangeWall = new DoubleSetting("PlaceRangeWall", 3.0, 1.0, 6.0, this);

    IntSetting placeDelay = new IntSetting("placeDelay", 0, 0, 10, this);
    IntSetting breakDelay = new IntSetting("breakDelay", 2, 0, 10, this);

    IntSetting minHpPlace = new IntSetting("HPEnemyPlace", 8, 0, 36, this);
    IntSetting minHpBreak = new IntSetting("HPEnemyBreak", 6, 0, 36, this);
    IntSetting maxSelfDamage = new IntSetting("MaxSelfDamage", 6, 0, 36, this);

    EnumSetting rotateMode = new EnumSetting("Rotate", "Off", Arrays.asList("Off", "Packet", "Full"),this);
    BooleanSetting raytrace = new BooleanSetting("Break", true, this);

    BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", true, this);
    BooleanSetting antiSuicide = new BooleanSetting("AntiSuicide", true, this);
    BooleanSetting predict = new BooleanSetting("Predict", true, this);

    EnumSetting fastMode = new EnumSetting("Fast", "Off", Arrays.asList("Off", "Ignore", "Ghost"),this);

    BooleanSetting thirteen = new BooleanSetting("1.13", false, this);

    BooleanSetting faceplace = new BooleanSetting("Tabbott", true, this);
    IntSetting facePlaceHP = new IntSetting("Tabbott HP", 8, 0, 36, this);

    BooleanSetting fuckArmour = new BooleanSetting("Armour Fucker", true, this);
    IntSetting fuckArmourHP = new IntSetting("Armour%", 20, 0, 100, this);

    BooleanSetting stopWhileMining = new BooleanSetting("StopWhenMining", true, this);
    BooleanSetting stopFPWhenSword = new BooleanSetting("StopFaceplaceSword", false, this);

    BooleanSetting placeSwing = new BooleanSetting("placeSwing", false, this);
    EnumSetting breakSwing = new EnumSetting("BreakSwing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);

    EnumSetting mode = new EnumSetting("Render","Pretty",  Arrays.asList("Pretty", "Solid", "Outline"), this);
    IntSetting width = new IntSetting("Width", 1, 1, 10, this);
    ColourSetting renderColour = new ColourSetting("Colour", new Colour(255, 255, 255, 255), this);
    IntSetting ufoAlpha = new IntSetting("UFOAlpha", 255, 0, 255, this);
    BooleanSetting renderDamage = new BooleanSetting("RenderDamage", true, this);

    private final List<EntityEnderCrystal> attemptedCrystals = new ArrayList<>();

    private EntityPlayer ezTarget = null;
    private BlockPos renderBlock = null;

    private double renderDamageVal = 0.0;

    private float yaw;
    private float pitch;

    private boolean alreadyAttacking = false;
    private boolean placeTimeoutFlag = false;
    private boolean isRotating;
    private boolean didAnything;

    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;

    @SubscribeEvent(priority =  EventPriority.HIGH, receiveCanceled = true)
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotateMode.is("Full")) {
            this.doCrystalAura();
        }
    }

    @SubscribeEvent(priority =  EventPriority.HIGH, receiveCanceled = true)
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && isRotating && rotateMode.is("Packet")) {
            final CPacketPlayer p = event.getPacket();
            p.yaw = yaw;
            p.pitch = pitch;
            isRotating = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.predict.getValue() && event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet2 = event.getPacket();
            BlockPos pos = new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ());
            for (EntityPlayer player : mc.world.playerEntities) {
                if (!player.isEntityAlive() || player == mc.player) continue;
                if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) continue;
                if (player.getDistance(mc.player) > 11) continue;
                if (stopFPWhenSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) continue;

                if (!BlockUtil.rayTracePlaceCheck(pos, this.raytrace.getValue() && mc.player.getDistanceSq(pos)
                        > MathsUtil.square(this.placeRangeWall.getValue().floatValue()), 1.0f))
                    continue;

                int miniumDamage;
                if (EntityUtil.getHealth(player) <= facePlaceHP.getValue() && faceplace.getValue() ||
                        CrystalUtil.getArmourFucker(player, fuckArmourHP.getValue()) && fuckArmour.getValue()) {
                    miniumDamage = 2;
                } else {
                    miniumDamage = this.minHpPlace.getValue();
                }

                double targetDamage = CrystalUtil.calculateDamage(pos, player);
                if (targetDamage < miniumDamage) continue;
                double selfDamage = CrystalUtil.calculateDamage(pos, mc.player);
                if (selfDamage > maxSelfDamage.getValue()) continue;
                if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) continue;

                this.attackCrystalPredict(packet2.getEntityID(), pos);
            }
        }
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet4 = event.getPacket();
            for (int id : packet4.getEntityIDs()) {
                Entity entity = mc.world.getEntityByID(id);
                if (!(entity instanceof EntityEnderCrystal)) continue;
                this.attemptedCrystals.remove(entity);
            }
        }
    }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if (!this.rotateMode.is("None")) {
            this.setYawPitch(pos);
        }
        CPacketUseEntity attackPacket = new CPacketUseEntity();
        attackPacket.entityId = entityID;
        attackPacket.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(attackPacket);
        if (this.breaK.getValue()) {
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }
    }

    @Override
    public void onUpdate() {
        if (!this.rotateMode.is("Full")) {
            this.doCrystalAura();
        }
    }

    private void doCrystalAura() {
        if (nullCheck()) {
            this.disable();
            return;
        }

        didAnything = false;
        if (HackUtil.shouldPause(this)) return;

        if (this.place.getValue() && placeDelayCounter > placeTimeout) {
            this.placeCrystal();
        }
        if (this.breaK.getValue() && breakDelayCounter > breakTimeout) {
            this.breakCrystal();
        }

        if (!didAnything) {
            ezTarget = null;
            isRotating = false;
        }

        breakDelayCounter++;
        placeDelayCounter++;

    }

    private void placeCrystal() {
        BlockPos targetBlock = this.getBestBlock();
        if (targetBlock == null) return;

        placeDelayCounter = 0;
        alreadyAttacking = false;
        boolean offhandCheck = false;

        if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && autoSwitch.getValue()) {
                if (this.findCrystalsHotbar() == -1) return;
                mc.player.inventory.currentItem = this.findCrystalsHotbar();
            }
        } else {
            offhandCheck = true;
        }

        didAnything = true;
        setYawPitch(targetBlock);
        if (this.rotateMode.is("Full")) {
            WurstplusThree.ROTATION_MANAGER.lookAtPos(targetBlock);
        }
        BlockUtil.placeCrystalOnBlock(targetBlock, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, placeSwing.getValue());
    }

    private void breakCrystal() {
        EntityEnderCrystal crystal = this.getBestCrystal();
        if (crystal == null) return;

        if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            boolean shouldWeakness = true;

            if (mc.player.isPotionActive(MobEffects.STRENGTH)) {
                if (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() == 2) {
                    shouldWeakness = false;
                }
            }

            if (shouldWeakness) {
                if (!alreadyAttacking) {
                    this.alreadyAttacking = true;
                }

                int newSlot = -1;

                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);

                    if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
                        newSlot = i;
                        mc.playerController.updateController();
                        break;
                    }
                }

                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                }
            }

        }

        didAnything = true;

        setYawPitch(crystal);

        if (this.rotateMode.is("Full")) {
            WurstplusThree.ROTATION_MANAGER.lookAtEntity(crystal);
        }

        EntityUtil.attackEntity(crystal, false, true);

        if (fastMode.is("Ghost")) {
            crystal.setDead();
            attemptedCrystals.add(crystal);
        }

        breakDelayCounter = 0;
    }

    private EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        double miniumDamage;
        EntityEnderCrystal bestCrystal = null;

        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;

            if (mc.player.getDistance(crystal) > (mc.player.canEntityBeSeen(crystal) ? breakRange.getValue()
                    : breakRangeWall.getValue())) continue;

            if (this.raytrace.getValue() && !mc.player.canEntityBeSeen(crystal)) continue;
            if (crystal.isDead) continue;
            if (attemptedCrystals.contains(crystal)) continue;

            for (EntityPlayer player : mc.world.playerEntities) {
                if (!player.isEntityAlive() || player == mc.player) continue;
                if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) continue;
                if (player.getDistance(mc.player) > 11) continue;
                if (stopFPWhenSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) continue;

                if (EntityUtil.getHealth(player) <= facePlaceHP.getValue() && faceplace.getValue() ||
                        CrystalUtil.getArmourFucker(player, fuckArmourHP.getValue()) && fuckArmour.getValue()) {
                    miniumDamage = 2;
                } else {
                    miniumDamage = this.minHpBreak.getValue();
                }

                double targetDamage = CrystalUtil.calculateDamage(crystal, player);
                if (targetDamage < miniumDamage) continue;
                double selfDamage = CrystalUtil.calculateDamage(crystal, mc.player);
                if (selfDamage > maxSelfDamage.getValue()) continue;
                if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) continue;

                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    this.ezTarget = player;
                    bestCrystal = crystal;
                }
            }
        }

        return bestCrystal;
    }

    private BlockPos getBestBlock() {
        if (getBestCrystal() != null && !fastMode.is("Ignore")) {
            placeTimeoutFlag = true;
            return null;
        }

        if (placeTimeoutFlag) {
            placeTimeoutFlag = false;
            return null;
        }

        double bestDamage = 0;
        double miniumDamage;
        BlockPos bestPos = null;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (!player.isEntityAlive() || player == mc.player) continue;
            if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) continue;
            if (player.getDistance(mc.player) > 11) continue;
            if (stopFPWhenSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) continue;

            for (BlockPos blockPos : CrystalUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), true, this.thirteen.getValue())) {
                if (!BlockUtil.rayTracePlaceCheck(blockPos, this.raytrace.getValue() && mc.player.getDistanceSq(blockPos)
                        > MathsUtil.square(this.placeRangeWall.getValue().floatValue()), 1.0f))
                    continue;

                if (EntityUtil.getHealth(player) <= facePlaceHP.getValue() && faceplace.getValue() ||
                        CrystalUtil.getArmourFucker(player, fuckArmourHP.getValue()) && fuckArmour.getValue()) {
                    miniumDamage = 2;
                } else {
                    miniumDamage = this.minHpPlace.getValue();
                }

                double targetDamage = CrystalUtil.calculateDamage(blockPos, player);
                if (targetDamage < miniumDamage) continue;
                double selfDamage = CrystalUtil.calculateDamage(blockPos, mc.player);
                if (selfDamage > maxSelfDamage.getValue()) continue;
                if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) continue;

                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    bestPos = blockPos;
                    ezTarget = player;
                }
            }
        }

        renderDamageVal = bestDamage;
        renderBlock = bestPos;

        return bestPos;
    }

    private int findCrystalsHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private void setYawPitch(EntityEnderCrystal crystal) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
        this.yaw = angle[0];
        this.pitch = angle[1];
    }

    private void setYawPitch(BlockPos pos) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.yaw = angle[0];
        this.pitch = angle[1];
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderBlock == null) return;

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

        RenderUtil.drawBoxESP(renderBlock, renderColour.getColor(), true, renderColour.getColor(), 2f, outline, solid, 200, true, 0, false, false, false, false, 200);

        if (renderDamage.getValue()) {
            RenderUtil.drawText(renderBlock, ((Math.floor(this.renderDamageVal) == this.renderDamageVal) ? Integer.valueOf((int)this.renderDamageVal) : String.format("%.1f", this.renderDamageVal)) + "");
        }
    }

    @Override
    public void onEnable() {
        placeTimeout = this.placeDelay.getValue();
        breakTimeout = this.breakDelay.getValue();
        placeTimeoutFlag = false;
        isRotating = false;
        ezTarget = null;
        this.attemptedCrystals.clear();
    }
}
