package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hack.hacks.misc.AutoClip;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Hack.Registration(name = "Crystal Aura", description = "the goods", category = Hack.Category.COMBAT, priority = HackPriority.Highest)
public final class CrystalAura extends Hack {

    public static CrystalAura INSTANCE;

    public CrystalAura() {
        INSTANCE = this;
    }

    //ranges
    private final ParentSetting ranges = new ParentSetting("Ranges", this);
    private final DoubleSetting breakRange = new DoubleSetting("Break Range", 5.0, 0.0, 6.0, ranges);
    private final DoubleSetting placeRange = new DoubleSetting("Place Range", 5.0, 0.0, 6.0, ranges);
    private final DoubleSetting breakRangeWall = new DoubleSetting("Break Range Wall", 3.0, 0.0, 6.0, ranges);
    private final DoubleSetting placeRangeWall = new DoubleSetting("Place Range Wall", 3.0, 0.0, 6.0, ranges);
    private final DoubleSetting targetRange = new DoubleSetting("Target Range", 15.0, 0.0, 20.0, ranges);

    //delay
    private final ParentSetting delays = new ParentSetting("Delays", this);
    private final IntSetting placeDelay = new IntSetting("Place Delay", 0, 0, 10, delays);
    private final IntSetting breakDelay = new IntSetting("Break Delay", 0, 0, 10, delays);

    //Damages
    private final ParentSetting damages = new ParentSetting("Damages", this);
    private final IntSetting minPlace = new IntSetting("MinPlace", 9, 0, 36, damages);
    private final IntSetting minBreak = new IntSetting("MinBreak", 9, 0, 36, damages);
    private final BooleanSetting ignoreSelfDamage = new BooleanSetting("Ignore Self Damage", false, damages);
    private final IntSetting maxSelfPlace = new IntSetting("MaxSelfPlace", 5, 0, 36, damages, s -> !ignoreSelfDamage.getValue());
    private final IntSetting maxSelfBreak = new IntSetting("MaxSelfBreak", 5, 0, 36, damages, s -> !ignoreSelfDamage.getValue());
    private final BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true, damages);


    //general
    private final ParentSetting general = new ParentSetting("General", this);
    public final EnumSetting rotateMode = new EnumSetting("Rotate", "Off", Arrays.asList("Off", "Break", "Place", "Both"), general);
    public final IntSetting maxYaw = new IntSetting("MaxYaw", 180, 0, 180, general);
    private final BooleanSetting raytrace = new BooleanSetting("Raytrace", false, general);
    private final EnumSetting fastMode = new EnumSetting("Fast", "Ignore", Arrays.asList("Off", "Ignore", "Ghost", "Sound"), general);
    public final EnumSetting autoSwitch = new EnumSetting("Switch", "None", Arrays.asList("Allways", "NoGap", "None", "Silent"), general);
    private final BooleanSetting silentSwitchHand = new BooleanSetting("Silent Hand Activation", true, general, s -> autoSwitch.is("Silent"));
    private final BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true, general);
    private final IntSetting maxCrystals = new IntSetting("MaxCrystal", 1, 1, 4, general);
    private final BooleanSetting ignoreTerrain = new BooleanSetting("Terrain Trace", true, general);
    private final EnumSetting crystalLogic = new EnumSetting("Placements", "Damage", Arrays.asList("Damage", "Nearby", "Safe"), general);
    private final BooleanSetting thirteen = new BooleanSetting("1.13", false, general);
    private final BooleanSetting attackPacket = new BooleanSetting("AttackPacket", true, general);
    private final BooleanSetting packetSafe = new BooleanSetting("Packet Safe", true, general);
    private final EnumSetting arrayListMode = new EnumSetting("Array List Mode", "Latency", Arrays.asList("Latency", "Player", "CPS"), general);
    private final BooleanSetting debug = new BooleanSetting("Debug", false, general);

    //thread
    private final ParentSetting Thread = new ParentSetting("Thread", this);
    private final BooleanSetting threaded = new BooleanSetting("Threaded", false, Thread);
    private final BooleanSetting threadAttack = new BooleanSetting("Thread Attack", false, Thread);

    //predict
    private final ParentSetting predict = new ParentSetting("Predict", this);
    private final BooleanSetting predictCrystal = new BooleanSetting("Predict Crystal", true, predict);
    private final BooleanSetting predictBlock = new BooleanSetting("Predict Block", true, predict);
    private final EnumSetting predictTeleport = new EnumSetting("P Teleport", "Sound", Arrays.asList("Sound", "Packet", "None"), predict);
    private final BooleanSetting entityPredict = new BooleanSetting("Entity Predict", true, predict, v -> rotateMode.is("Off"));
    private final IntSetting predictedTicks = new IntSetting("Predict Ticks", 2, 0, 5, predict, s -> entityPredict.getValue() && rotateMode.is("Off"));

    //feet obi stuff
    private final ParentSetting FeetObi = new ParentSetting("ObifeetMode", this);
    private final BooleanSetting palceObiFeet = new BooleanSetting("Enabled", false, FeetObi);
    private final BooleanSetting ObiYCheck = new BooleanSetting("YCheck", false, FeetObi);
    private final BooleanSetting rotateObiFeet = new BooleanSetting("Rotate", false, FeetObi);
    private final IntSetting timeoutTicksObiFeet = new IntSetting("Timeout", 3, 0, 5, FeetObi);

    //faceplace/tabbott stuff
    private final ParentSetting faceplace = new ParentSetting("Tabbott", this);
    private final IntSetting facePlaceHP = new IntSetting("TabbottHP", 0, 0, 36, faceplace);
    private final IntSetting facePlaceDelay = new IntSetting("TabbottDelay", 0, 0, 10, faceplace);
    private final KeySetting fpbind = new KeySetting("TabbottBind", -1, faceplace);
    private final IntSetting fuckArmourHP = new IntSetting("Armour%", 0, 0, 100, faceplace);

    //render
    private final ParentSetting render = new ParentSetting("Render", this);
    private final EnumSetting when = new EnumSetting("When", "Place", Arrays.asList("Place", "Break", "Both", "Never"), render);
    private final EnumSetting mode = new EnumSetting("Mode", "Pretty", Arrays.asList("Pretty", "Solid", "Outline"), render);
    private final EnumSetting fade = new EnumSetting("Fade", "Fast", Arrays.asList("Fast", "Medium", "Slow", "Off"), render);
    private final BooleanSetting flat = new BooleanSetting("Flat", false, render);
    private final DoubleSetting hight = new DoubleSetting("FlatHeight", 0.2, -2.0, 2.0, render, s -> flat.getValue());
    private final IntSetting width = new IntSetting("Width", 1, 1, 10, render);
    private final ColourSetting renderFillColour = new ColourSetting("FillColour", new Colour(0, 0, 0, 255), render);
    private final ColourSetting renderBoxColour = new ColourSetting("BoxColour", new Colour(255, 255, 255, 255), render);
    private final BooleanSetting renderDamage = new BooleanSetting("RenderDamage", true, render);
    private final EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), render);
    private final BooleanSetting placeSwing = new BooleanSetting("Place Swing", true, render);

    private final List<EntityEnderCrystal> attemptedCrystals = new ArrayList<>();
    private ArrayList<RenderPos> renderMap = new ArrayList<>();
    private ArrayList<BlockPos> currentTargets = new ArrayList<>();
    public EntityPlayer ezTarget = null;
    private final Timer crystalsPlacedTimer = new Timer();
    private boolean alreadyAttacking;
    private boolean placeTimeoutFlag;
    private boolean hasPacketBroke;
    private boolean didAnything;
    private boolean facePlacing;

    private long start = 0;
    private long crystalLatency;

    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;
    private int facePlaceDelayCounter;
    private int obiFeetCounter;
    private int crystalsPlaced;

    public BlockPos staticPos;
    public EntityEnderCrystal staticEnderCrystal;

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onUpdateWalkingPlayerEvent(@NotNull UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && !this.rotateMode.is("Off")) {
            this.doCrystalAura();
        }
    }

    @Override
    public void onUpdate() {
        if (this.rotateMode.is("Off")) {
            this.doCrystalAura();
        }
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onPacketSend(@NotNull PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK
                && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
            if (this.fastMode.is("Ghost")) {
                Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).setDead();
                mc.world.removeEntityFromWorld(packet.entityId);
            }
        }
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onPacketReceive(@NotNull PacketEvent.Receive event) {
        SPacketSpawnObject packet;
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = event.getPacket()).getType() == 51) {
            for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
                if (this.isCrystalGood(new EntityEnderCrystal(mc.world, packet.getX(), packet.getY(), packet.getZ()), target) != 0) {
                    if (this.predictCrystal.getValue()) {
                        if (debug.getValue()) {
                            ClientMessage.sendMessage("predict break");
                        }
                        CPacketUseEntity predict = new CPacketUseEntity();
                        predict.entityId = packet.getEntityID();
                        predict.action = CPacketUseEntity.Action.ATTACK;
                        mc.player.connection.sendPacket(predict);
                        if (!this.swing.is("None")) {
                            BlockUtil.swingArm(swing);
                        }
                        if (packetSafe.getValue()) {
                            hasPacketBroke = true;
                            didAnything = true;
                        }
                    }
                    break;
                }
            }
        }
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet_ = event.getPacket();
            for (int id : packet_.getEntityIDs()) {
                try {
                    Entity entity = mc.world.getEntityByID(id);
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.attemptedCrystals.remove(entity);
                } catch (Exception ignored) {
                }
            }
        }

        if (event.getPacket() instanceof SPacketEntityTeleport) {
            Entity e = mc.world.getEntityByID(((SPacketEntityTeleport) event.getPacket()).getEntityId());
            if (e instanceof EntityPlayer && predictTeleport.is("Packet")) {
                SPacketEntityTeleport p = event.getPacket();
                e.setEntityBoundingBox(e.getEntityBoundingBox().offset(p.getX(), p.getY(), p.getZ()));
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect) {
            if (((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT && predictTeleport.is("Sound")) {
                SPacketSoundEffect p = event.getPacket();
                mc.world.loadedEntityList.spliterator().forEachRemaining(player -> {
                    if (player instanceof EntityPlayer) {
                        if (player.getDistance(p.getX(), p.getY(), p.getZ()) <= targetRange.getValue()) {
                            player.setEntityBoundingBox(player.getEntityBoundingBox().offset(p.getX(), p.getY(), p.getZ()));
                        }
                    }
                });
            }

            try {
                if (((SPacketSoundEffect) event.getPacket()).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity crystal : new ArrayList<>(mc.world.loadedEntityList)) {
                        if (crystal instanceof EntityEnderCrystal)
                            if (crystal.getDistance(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()) <= breakRange.getValue()) {
                                crystalLatency = System.currentTimeMillis() - start;
                                if (fastMode.getValue().equals("Sound")) {
                                    crystal.setDead();
                                }
                            }
                    }
                }
            } catch (NullPointerException e) {
                //empty catch block cus nullpointer gay
            }
        }
        if (event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion packet2 = event.getPacket();
            BlockPos pos = new BlockPos(Math.floor(packet2.getX()), Math.floor(packet2.getY()), Math.floor(packet2.getZ())).down();
            if (this.predictBlock.getValue()) {
                for (EntityPlayer player : new ArrayList<>(mc.world.playerEntities)) {
                    if (this.isBlockGood(pos, player) > 0) {
                        BlockUtil.placeCrystalOnBlock(pos, EnumHand.MAIN_HAND, true);
                    }
                }
            }
        }
    }

    public void doCrystalAura() {
        if (nullCheck()) {
            this.disable();
            return;
        }
        didAnything = false;

        if (placeDelayCounter > placeTimeout && (facePlaceDelayCounter >= facePlaceDelay.getValue() || !facePlacing)) {
            start = System.currentTimeMillis();
            this.placeCrystal();
        }
        if (breakDelayCounter > breakTimeout && (!hasPacketBroke || !packetSafe.getValue())) {
            this.breakCrystal();
        }

        if (!didAnything) {
            hasPacketBroke = false;
        }

        breakDelayCounter++;
        placeDelayCounter++;
        facePlaceDelayCounter++;
        obiFeetCounter++;
    }

    private void clearMap(BlockPos checkBlock) {
        List<RenderPos> toRemove = new ArrayList<>();
        if (checkBlock == null || renderMap.isEmpty()) return;
        for (RenderPos pos : renderMap) {
            if (pos.pos.getX() == checkBlock.getX() && pos.pos.getY() == checkBlock.getY() && pos.pos.getZ() == checkBlock.getZ())
                toRemove.add(pos);
        }
        renderMap.removeAll(toRemove);
    }

    private void placeCrystal() {
        ArrayList<BlockPos> placePositions = this.getBestBlocks();
        currentTargets.clear();
        currentTargets.addAll(placePositions);
        if (placePositions.size() > 0) {
            boolean offhandCheck = false;
            int slot = InventoryUtil.findHotbarBlock(ItemEndCrystal.class);
            int old = mc.player.inventory.currentItem;
            EnumHand hand = null;
            int stackSize = getCrystalCount(offhandCheck);
            alreadyAttacking = false;
            if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && (autoSwitch.getValue().equals("Allways") || autoSwitch.is("NoGap"))) {
                    if (autoSwitch.is("NoGap")) {
                        if (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE) {
                            return;
                        }
                    }
                    if (this.findCrystalsHotbar() == -1) return;
                    mc.player.inventory.currentItem = this.findCrystalsHotbar();
                    mc.playerController.syncCurrentPlayItem();
                }
            } else {
                offhandCheck = true;
            }
            if (autoSwitch.is("Silent")) {
                if (slot != -1) {
                    if (mc.player.isHandActive() && silentSwitchHand.getValue()) {
                        hand = mc.player.getActiveHand();
                    }
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }
            }
            placeDelayCounter = 0;
            facePlaceDelayCounter = 0;
            didAnything = true;
            for (BlockPos targetBlock : placePositions) {
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal || autoSwitch.is("Silent")) {
                    if (setYawPitch(targetBlock)) {
                        BlockUtil.placeCrystalOnBlock(targetBlock, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, placeSwing.getValue());
                        if (debug.getValue()) {
                            ClientMessage.sendMessage("placing");
                        }
                        crystalsPlaced++;
                    }
                } else if (debug.getValue()) {
                    ClientMessage.sendMessage("doing yawstep on place");
                }
            }
            int newSize = getCrystalCount(offhandCheck);
            if (autoSwitch.is("Silent")) {
                if (slot != -1) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(old));
                    if (silentSwitchHand.getValue() && hand != null) {
                        mc.player.setActiveHand(hand);
                    }
                }
            }

            if (newSize == stackSize) {
                didAnything = false;
            }
        }
    }

    private int getCrystalCount(boolean offhand) {
        if (offhand) {
            return mc.player.getHeldItemOffhand().stackSize;
        } else {
            return mc.player.getHeldItemMainhand().stackSize;
        }
    }

    private void breakCrystal() {
        EntityEnderCrystal crystal;
        crystal = this.getBestCrystal();
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
        if (setYawPitch(crystal)) {
            EntityUtil.attackEntity(crystal, this.attackPacket.getValue());
            if (!this.swing.is("None")) {
                BlockUtil.swingArm(swing);
            }
            if (debug.getValue()) {
                ClientMessage.sendMessage("breaking");
            }
            breakDelayCounter = 0;
        } else if (debug.getValue()) {
            ClientMessage.sendMessage("doing yawstep on break");
        }
    }

    public final EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        EntityEnderCrystal bestCrystal = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;
            for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
                if (mc.player.getDistanceSq(target) > MathsUtil.square(targetRange.getValue().floatValue())) continue;
                if (entityPredict.getValue() && rotateMode.is("Off")) {
                    float f = target.width / 2.0F, f1 = target.height;
                    target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
                    Entity y = CrystalUtil.getPredictedPosition(target, predictedTicks.getValue());
                    target.setEntityBoundingBox(y.getEntityBoundingBox());
                }
                double targetDamage = this.isCrystalGood(crystal, target);
                if (targetDamage <= 0) continue;
                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    this.ezTarget = target;
                    bestCrystal = crystal;
                }
            }
        }
        if (this.ezTarget != null) {
            WurstplusThree.KD_MANAGER.targets.put(this.ezTarget.getName(), 20);
            AutoClip.INSTANCE.targets.put(this.ezTarget.getName(), 20);
        }
        if (bestCrystal != null && (when.is("Both") || when.is("Break"))) {
            BlockPos renderPos = bestCrystal.getPosition().down();
            clearMap(renderPos);
            renderMap.add(new RenderPos(renderPos, bestDamage));
        }
        return bestCrystal;
    }


    public final ArrayList<BlockPos> getBestBlocks() {
        ArrayList<RenderPos> posArrayList = new ArrayList<>();
        if (getBestCrystal() != null && fastMode.is("Off")) {
            placeTimeoutFlag = true;
            return null;
        }

        if (placeTimeoutFlag) {
            placeTimeoutFlag = false;
            return null;
        }

        for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
            if (mc.player.getDistanceSq(target) > MathsUtil.square(targetRange.getValue().floatValue())) continue;
            if (entityPredict.getValue()) {
                float f = target.width / 2.0F, f1 = target.height;
                target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
                Entity y = CrystalUtil.getPredictedPosition(target, predictedTicks.getValue());
                target.setEntityBoundingBox(y.getEntityBoundingBox());
            }
            for (BlockPos blockPos : CrystalUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), true, this.thirteen.getValue())) {
                double targetDamage = isBlockGood(blockPos, target);
                if (targetDamage <= 0) continue;
                posArrayList.add(new RenderPos(blockPos, targetDamage));
            }
        }

        Collections.sort(posArrayList, new DamageComparator());
        //making sure all positions are placeble and wont block each other
        List<BlockPos> blockedPosList = new ArrayList<>();
        List<RenderPos> toRemove = new ArrayList<>();
        for (RenderPos test : posArrayList) {
            boolean blocked = false;
            for (BlockPos blockPos : blockedPosList) {
                if (blockPos.getX() == test.pos.getX() && blockPos.getY() == test.pos.getY() && blockPos.getZ() == test.pos.getZ()) {
                    blocked = true;
                }
            }
            if (!blocked) {
                blockedPosList.addAll(getBlockedPositions(test.pos));
            } else toRemove.add(test);
        }
        posArrayList.removeAll(toRemove);
        if (this.ezTarget != null) {
            WurstplusThree.KD_MANAGER.targets.put(this.ezTarget.getName(), 20);
            AutoClip.INSTANCE.targets.put(this.ezTarget.getName(), 20);
        }
        //taking the best out of the list
        ArrayList<BlockPos> finalArrayList = new ArrayList<>();
        IntStream.range(0, Math.min(maxCrystals.getValue(), posArrayList.size())).forEachOrdered(n -> {
            RenderPos pos = posArrayList.get(n);
            if (when.is("Both") || when.is("Place")) {
                clearMap(pos.pos);
                if (pos.pos != null) renderMap.add(pos);
            }
            finalArrayList.add(pos.pos);
        });
        return finalArrayList;
    }

    private ArrayList<BlockPos> getBlockedPositions(BlockPos pos) {
        ArrayList<BlockPos> list = new ArrayList<>();
        list.add(pos.add(1, -1, 1));
        list.add(pos.add(1, -1, -1));
        list.add(pos.add(-1, -1, 1));
        list.add(pos.add(-1, -1, -1));
        list.add(pos.add(-1, -1, 0));
        list.add(pos.add(1, -1, 0));
        list.add(pos.add(0, -1, -1));
        list.add(pos.add(0, -1, 1));
        list.add(pos.add(1, 0, 1));
        list.add(pos.add(1, 0, -1));
        list.add(pos.add(-1, 0, 1));
        list.add(pos.add(-1, 0, -1));
        list.add(pos.add(-1, 0, 0));
        list.add(pos.add(1, 0, 0));
        list.add(pos.add(0, 0, -1));
        list.add(pos.add(0, 0, 1));
        list.add(pos.add(1, 1, 1));
        list.add(pos.add(1, 1, -1));
        list.add(pos.add(-1, 1, 1));
        list.add(pos.add(-1, 1, -1));
        list.add(pos.add(-1, 1, 0));
        list.add(pos.add(1, 1, 0));
        list.add(pos.add(0, 1, -1));
        list.add(pos.add(0, 1, 1));
        return list;
    }

    private double isCrystalGood(@NotNull EntityEnderCrystal crystal, @NotNull EntityPlayer target) {
        if (this.isPlayerValid(target)) {
            if (mc.player.canEntityBeSeen(crystal)) {
                if (mc.player.getDistanceSq(crystal) > MathsUtil.square(this.breakRange.getValue().floatValue())) {
                    return 0;
                }
            } else {
                if (mc.player.getDistanceSq(crystal) > MathsUtil.square(this.breakRangeWall.getValue().floatValue())) {
                    return 0;
                }
            }
            if (crystal.isDead) return 0;
            if (attemptedCrystals.contains(crystal)) return 0;
            double targetDamage = CrystalUtil.calculateDamage(crystal, target, ignoreTerrain.getValue());
            // set min damage to 2 if we want to kill the dude fast
            facePlacing = false;
            double miniumDamage = this.minBreak.getValue();
            if (((EntityUtil.getHealth(target) <= facePlaceHP.getValue()) || CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) || fpbind.isDown()) && targetDamage < minBreak.getValue()) {
                miniumDamage = EntityUtil.isInHole(target) ? 1 : 2;
                facePlacing = true;
            }


            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = 0;
            if (!ignoreSelfDamage.getValue()) {
                selfDamage = CrystalUtil.calculateDamage(crystal, mc.player, ignoreTerrain.getValue());
            }
            if (selfDamage > maxSelfBreak.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;
            switch (crystalLogic.getValue()) {
                case "Safe":
                    return targetDamage - selfDamage;
                case "Damage":
                    return targetDamage;
                case "Nearby":
                    double distance = mc.player.getDistanceSq(crystal);
                    return targetDamage - distance;
            }
        }
        return 0;
    }

    private double isBlockGood(@NotNull BlockPos blockPos, @NotNull EntityPlayer target) {
        if (this.isPlayerValid(target)) {
            // if raytracing and cannot see block
            if (!CrystalUtil.canSeePos(blockPos) && raytrace.getValue()) return 0;
            // if cannot see pos use wall range, else use normal
            if (!CrystalUtil.canSeePos(blockPos)) {
                if (mc.player.getDistanceSq(blockPos) > MathsUtil.square(this.placeRangeWall.getValue().floatValue())) {
                    return 0;
                }
            } else {
                if (mc.player.getDistanceSq(blockPos) > MathsUtil.square(this.placeRange.getValue().floatValue())) {
                    return 0;
                }
            }
            double targetDamage = CrystalUtil.calculateDamage(blockPos, target, ignoreTerrain.getValue());

            facePlacing = false;
            double miniumDamage = this.minPlace.getValue();
            if (((EntityUtil.getHealth(target) <= facePlaceHP.getValue()) || CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) || fpbind.isDown()) && targetDamage < minPlace.getValue()) {
                miniumDamage = EntityUtil.isInHole(target) ? 1 : 2;
                facePlacing = true;
            }

            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = 0;
            if (!ignoreSelfDamage.getValue()) {
                selfDamage = CrystalUtil.calculateDamage(blockPos, mc.player, ignoreTerrain.getValue());
            }
            if (selfDamage > maxSelfPlace.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;
            switch (crystalLogic.getValue()) {
                case "Safe":
                    return targetDamage - selfDamage;
                case "Damage":
                    return targetDamage;
                case "Nearby":
                    double distance = mc.player.getDistanceSq(blockPos);
                    return targetDamage - distance;
            }
        }
        return 0;
    }

    private boolean isPlayerValid(@NotNull EntityPlayer player) {
        if (player.getHealth() + player.getAbsorptionAmount() <= 0 || player == mc.player) return false;
        if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) return false;
        if (player.getName().equals(mc.player.getName())) return false;
        if (player.getDistanceSq(mc.player) > 13 * 13) return false;
        if (this.palceObiFeet.getValue() && obiFeetCounter >= timeoutTicksObiFeet.getValue() && mc.player.getDistance(player) < 5) {
            try {
                this.blockObiNextToPlayer(player);
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void blockObiNextToPlayer(EntityPlayer player) {
        if (ObiYCheck.getValue() && Math.floor(player.posY) == Math.floor(mc.player.posY)) return;
        obiFeetCounter = 0;
        BlockPos pos = EntityUtil.getFlooredPos(player).down();
        if (EntityUtil.isInHole(player) || mc.world.getBlockState(pos).getBlock() == Blocks.AIR) return;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                BlockPos checkPos = pos.add(i, 0, j);
                if (mc.world.getBlockState(checkPos).getMaterial().isReplaceable()) {
                    BlockUtil.placeBlock(checkPos, PlayerUtil.findObiInHotbar(), rotateObiFeet.getValue(), rotateObiFeet.getValue(), swing);
                }
            }
        }
    }

    private int findCrystalsHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private boolean setYawPitch(@NotNull EntityEnderCrystal crystal) {
        if (rotateMode.is("Off") || rotateMode.is("Place")) return true;
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
        float yaw = angle[0];
        float pitch = angle[1];
        float spoofedYaw = WurstplusThree.ROTATION_MANAGER.getSpoofedYaw();
        if (Math.abs(spoofedYaw - yaw) > maxYaw.getValue() && Math.abs(spoofedYaw - 360 - yaw) > maxYaw.getValue() && Math.abs(spoofedYaw + 360 - yaw) > maxYaw.getValue()) {
            WurstplusThree.ROTATION_MANAGER.setPlayerRotations(Math.abs(spoofedYaw - yaw) < 180 ? spoofedYaw > yaw ? (spoofedYaw - maxYaw.getValue()) : (spoofedYaw + maxYaw.getValue()) : spoofedYaw > yaw ? (spoofedYaw + maxYaw.getValue()) : (spoofedYaw - maxYaw.getValue()), pitch);
            return false;
        } else {
            WurstplusThree.ROTATION_MANAGER.setPlayerRotations(yaw, pitch);
        }
        return true;
    }

    public boolean setYawPitch(@NotNull BlockPos pos) {
        if (rotateMode.is("Off") || rotateMode.is("Break")) return true;
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        float yaw = angle[0];
        float pitch = angle[1];
        float spoofedYaw = WurstplusThree.ROTATION_MANAGER.getSpoofedYaw();
        if (Math.abs(spoofedYaw - yaw) > maxYaw.getValue() && Math.abs(spoofedYaw - 360 - yaw) > maxYaw.getValue() && Math.abs(spoofedYaw + 360 - yaw) > maxYaw.getValue()) {
            WurstplusThree.ROTATION_MANAGER.setPlayerRotations(Math.abs(spoofedYaw - yaw) < 180 ? spoofedYaw > yaw ? (spoofedYaw - maxYaw.getValue()) : (spoofedYaw + maxYaw.getValue()) : spoofedYaw > yaw ? (spoofedYaw + maxYaw.getValue()) : (spoofedYaw - maxYaw.getValue()), pitch);
            return false;
        } else {
            WurstplusThree.ROTATION_MANAGER.setPlayerRotations(yaw, pitch);
        }
        return true;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (renderMap.isEmpty()) return;
        boolean outline = false;
        boolean solid = false;
        switch (mode.getValue()) {
            case "Pretty":
                outline = true;
                solid = true;
                break;
            case "Solid":
                outline = false;
                solid = true;
                break;
            case "Outline":
                outline = true;
                solid = false;
                break;
        }
        List<RenderPos> toRemove = new ArrayList<>();
        for (Iterator<RenderPos> it = renderMap.iterator(); it.hasNext(); ) {
            RenderPos renderPos = it.next();
            RenderUtil.drawBoxESP((flat.getValue()) ? new BlockPos(renderPos.pos.getX(), renderPos.pos.getY() + 1, renderPos.pos.getZ()) : renderPos.pos, new Colour(renderFillColour.getValue().getRed(), renderFillColour.getValue().getGreen(), renderFillColour.getValue().getBlue(), (int) Math.max(renderFillColour.getValue().getAlpha() - renderPos.alpha, 0)), new Colour(renderBoxColour.getValue().getRed(), renderBoxColour.getValue().getGreen(), renderBoxColour.getValue().getBlue(), (int) Math.max(renderBoxColour.getValue().getAlpha() - renderPos.alpha, 0)), width.getValue(), outline, solid, true, (flat.getValue()) ? hight.getValue() : 0f, false, false, false, false, 0);
            if (renderDamage.getValue())
                RenderUtil.drawText(renderPos.pos, String.valueOf(MathsUtil.roundAvoid(renderPos.damage, 1)), Gui.INSTANCE.customFont.getValue());
            if (renderPos.alpha > Math.max(renderFillColour.getValue().getAlpha(), renderBoxColour.getValue().getAlpha()))
                toRemove.add(renderPos);
            renderPos.alpha = renderPos.alpha + (fade.is("Fast") ? 1.5 : fade.is("Slow") ? 0.5 : 1);
            if (currentTargets.contains(renderPos.pos)) {
                renderPos.alpha = 0;
            } else if (fade.is("Off")) {
                toRemove.add(renderPos);
            }
        }
        renderMap.removeAll(toRemove);
    }

    @Override
    public void onEnable() {
        placeTimeout = this.placeDelay.getValue();
        breakTimeout = this.breakDelay.getValue();
        placeTimeoutFlag = false;
        ezTarget = null;
        facePlacing = false;
        attemptedCrystals.clear();
        hasPacketBroke = false;
        placeTimeoutFlag = false;
        alreadyAttacking = false;
        obiFeetCounter = 0;
        crystalLatency = 0;
        start = 0;
        staticEnderCrystal = null;
        staticPos = null;
        crystalsPlaced = 0;
        crystalsPlacedTimer.reset();
    }

    public float getCPS() {
        return crystalsPlaced / (crystalsPlacedTimer.getPassedTimeMs() / 1000f);
    }

    @Override
    public String getDisplayInfo() {
        switch (arrayListMode.getValue()) {
            case "Latency":
                return crystalLatency + "ms";
            case "CPS":
                return "" + MathsUtil.round(getCPS(), 2);
            case "Player":
                return this.ezTarget != null ? this.ezTarget.getName() : null;
            default:
                return "";
        }
    }

    class RenderPos {
        public RenderPos(BlockPos pos, Double damage) {
            this.pos = pos;
            this.damage = damage;
        }

        Double damage;
        double alpha;
        BlockPos pos;
    }

    class DamageComparator implements Comparator<RenderPos> {
        @Override
        public int compare(RenderPos a, RenderPos b) {
            return b.damage.compareTo(a.damage);
        }
    }
}


