package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.hack.hacks.chat.AutoEz;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hack.hacks.misc.AutoClip;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.CrystalPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public final EnumSetting rotateMode = new EnumSetting("Rotate", "Off", Arrays.asList("Off", "Packet", "Full"), general);
    private final BooleanSetting raytrace = new BooleanSetting("Raytrace", false, general);
    private final EnumSetting fastMode = new EnumSetting("Fast", "Ignore", Arrays.asList("Off", "Ignore", "Ghost", "Sound"), general);
    public final EnumSetting autoSwitch = new EnumSetting("Switch", "None", Arrays.asList("Allways", "NoGap", "None"), general);
    private final BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true, general);
    private final BooleanSetting ignoreTerrain = new BooleanSetting("Terrain Trace", true, general);
    private final EnumSetting crystalLogic = new EnumSetting("Placements", "Damage", Arrays.asList("Damage", "Smart", "Strict"), general);
    private final BooleanSetting thirteen = new BooleanSetting("1.13", false, general);
    private final BooleanSetting attackPacket = new BooleanSetting("AttackPacket", true, general);
    private final BooleanSetting packetSafe = new BooleanSetting("Packet Safe", true, general);
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
    private final BooleanSetting entityPredict = new BooleanSetting("Entity Predict", true, predict);
    private final IntSetting predictedTicks = new IntSetting("Predict Ticks", 2, 0, 5, predict, s -> entityPredict.getValue());

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
    private final BooleanSetting stopFPWhenSword = new BooleanSetting("NoFPSword", false, faceplace);
    private final IntSetting fuckArmourHP = new IntSetting("Armour%", 0, 0, 100, faceplace);

    //chaineese mode
    private final ParentSetting chainParent = new ParentSetting("ChainMode", this);
    private final BooleanSetting chainMode = new BooleanSetting("UseChainMode", false, chainParent);
    private final IntSetting chainCounter = new IntSetting("ChainCounter", 3, 0, 10, chainParent);
    private final IntSetting chainStep = new IntSetting("ChainStep", 2, 0, 5, chainParent);

    //render
    private final ParentSetting render = new ParentSetting("Render", this);
    private final EnumSetting mode = new EnumSetting("Mode", "Pretty", Arrays.asList("Pretty", "Solid", "Outline", "Circle", "Column"), render);
    private final BooleanSetting flat = new BooleanSetting("Flat", false, render);
    private final DoubleSetting hight = new DoubleSetting("FlatHeight", 0.2, -2.0, 2.0, render, s -> flat.getValue());
    private final IntSetting width = new IntSetting("Width", 1, 1, 10, render, s -> !mode.is("Circle") || !mode.is("Column"));
    private final DoubleSetting radius = new DoubleSetting("Radius", 0.7, 0.0, 5.0, render, s -> mode.is("Circle") || mode.is("Column"));
    private final DoubleSetting columnHight = new DoubleSetting("ColumnHeight", 1.5, 0.0, 10.0, render, s -> mode.is("Column"));
    private final ColourSetting renderFillColour = new ColourSetting("FillColour", new Colour(0, 0, 0, 255), render);
    private final ColourSetting renderBoxColour = new ColourSetting("BoxColour", new Colour(255, 255, 255, 255), render);
    private final BooleanSetting renderDamage = new BooleanSetting("RenderDamage", true, render);
    private final EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), render);
    private final BooleanSetting placeSwing = new BooleanSetting("Place Swing", true, render);



    private final List<EntityEnderCrystal> attemptedCrystals = new ArrayList<>();

    public EntityPlayer ezTarget = null;
    public BlockPos renderBlock = null;
    private double renderDamageVal = 0;

    private float yaw;
    private float pitch;

    private boolean alreadyAttacking;
    private boolean placeTimeoutFlag;
    private boolean hasPacketBroke;
    private boolean isRotating;
    private boolean didAnything;
    private boolean facePlacing;
    private boolean rotationPause;

    private long start = 0;
    private long crystalLatency;

    private int currentChainCounter;
    private int chainCount;
    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;
    private int facePlaceDelayCounter;
    private int obiFeetCounter;
    private int highestID;

    public BlockPos staticPos;
    public EntityEnderCrystal staticEnderCrystal;

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onUpdateWalkingPlayerEvent(@NotNull UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotateMode.is("Full")) {
            this.doCrystalAura();
        }
    }

    @Override
    public void onUpdate() {
        if (!this.rotateMode.is("Full")) {
            this.doCrystalAura();
        }
    }


    @CommitEvent(priority = EventPriority.HIGH)
    public final void onPacketSend(@NotNull PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && isRotating && rotateMode.is("Packet")) {
            final CPacketPlayer p = event.getPacket();
            p.yaw = yaw;
            p.pitch = pitch;
        }
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
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && threadAttack.getValue()) {
            CPacketPlayerTryUseItemOnBlock packet1 = event.getPacket();
            if (mc.player.getHeldItem(packet1.hand).getItem() instanceof ItemEndCrystal) {
                updateEntityID();
                for (int i = 1; i < 3; ++i) {
                    this.attackID(packet1.position, this.highestID + i);
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

        if (rotationPause) {
            if (WurstplusThree.ROTATION_MANAGER.stepRotation()) {
                rotationPause = false;
            } else {
                return;
            }
        }

        if (placeDelayCounter > placeTimeout && (facePlaceDelayCounter >= facePlaceDelay.getValue() || !facePlacing)) {
            start = System.currentTimeMillis();
            this.placeCrystal();
        }
        if (breakDelayCounter > breakTimeout && (!hasPacketBroke || !packetSafe.getValue())) {
            this.breakCrystal();
        }

        if (!didAnything) {
            hasPacketBroke = false;
            ezTarget = null;
            isRotating = false;
            chainCount = chainStep.getValue();
            currentChainCounter = 0;
        }

        currentChainCounter++;
        breakDelayCounter++;
        placeDelayCounter++;
        facePlaceDelayCounter++;
        obiFeetCounter++;
    }

    private void placeCrystal() {
        BlockPos targetBlock;
        if (threaded.getValue()) {
            Threads threads = new Threads(ThreadType.BLOCK);
            threads.start();
            targetBlock = staticPos;
        } else {
            targetBlock = this.getBestBlock();
        }
        if (targetBlock == null) return;

        placeDelayCounter = 0;
        facePlaceDelayCounter = 0;
        alreadyAttacking = false;
        boolean offhandCheck = false;

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

        int stackSize = getCrystalCount(offhandCheck);
        didAnything = true;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            setYawPitch(targetBlock);
            BlockUtil.placeCrystalOnBlock(targetBlock, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, placeSwing.getValue());
            if (debug.getValue()) {
                ClientMessage.sendMessage("placing");
            }
        }
        int newSize = getCrystalCount(offhandCheck);
        if (newSize == stackSize) {
            didAnything = false;
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
        if (threaded.getValue()) {
            Threads threads = new Threads(ThreadType.CRYSTAL);
            threads.start();
            crystal = staticEnderCrystal;
        } else {
            crystal = this.getBestCrystal();
        }
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
        EntityUtil.attackEntity(crystal, this.attackPacket.getValue());
        if (!this.swing.is("None")) {
            BlockUtil.swingArm(swing);
        }
        if (debug.getValue()) {
            ClientMessage.sendMessage("breaking");
        }
        breakDelayCounter = 0;
    }

    public final EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        EntityEnderCrystal bestCrystal = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;
            for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
                if (mc.player.getDistanceSq(target) > MathsUtil.square(targetRange.getValue().floatValue())) continue;
                if (entityPredict.getValue()) {
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
        return bestCrystal;
    }


    public final BlockPos getBestBlock() {
        if (getBestCrystal() != null && fastMode.is("Off")) {
            placeTimeoutFlag = true;
            return null;
        }

        if (placeTimeoutFlag) {
            placeTimeoutFlag = false;
            return null;
        }
        double bestDamage = 0;
        BlockPos bestPos = null;

        ArrayList<CrystalPos> validPos = new ArrayList<>();

        for (EntityPlayer target : new ArrayList<>(mc.world.playerEntities)) {
            if (mc.player.getDistanceSq(target) > MathsUtil.square(targetRange.getValue().floatValue())) continue;
            if (entityPredict.getValue()) {
                float f = target.width / 2.0F, f1 = target.height;
                target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
                Entity y = CrystalUtil.getPredictedPosition(target, predictedTicks.getValue());
                target.setEntityBoundingBox(y.getEntityBoundingBox());
            }
            for (BlockPos blockPos :  CrystalUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), true, this.thirteen.getValue())) {
                double targetDamage = isBlockGood(blockPos, target);
                if (targetDamage <= 0) continue;
                if (chainMode.getValue() && currentChainCounter >= chainCounter.getValue()) {
                    validPos.add(new CrystalPos(blockPos, targetDamage));
                } else {
                    if (targetDamage > bestDamage) {
                        bestDamage = targetDamage;
                        bestPos = blockPos;
                        ezTarget = target;
                    }
                }

            }
        }

        if (this.ezTarget != null) {
            WurstplusThree.KD_MANAGER.targets.put(this.ezTarget.getName(), 20);
            AutoClip.INSTANCE.targets.put(this.ezTarget.getName(), 20);
        }

        /*
        a while ago someone told me that the reason crystals don't do max damage is bc NCP blocks
        crystal damage after so much has been done in a short time frame, but it works in a sort
        of counter way so that if you take less damage than before the counter will reset and youll
        be taking full damage again.. this basically does that (no idea how well it works in practice
        bc i feel chinese every time i turn it on)
         */
        if (chainMode.getValue() && currentChainCounter >= chainCounter.getValue()) {
            currentChainCounter = 0;
            validPos.sort(Comparator.comparing(CrystalPos::getDamage));
            Collections.reverse(validPos);
            if (validPos.size() <= chainCount) {
                if (validPos.isEmpty()) {
                    renderDamageVal = 0;
                    renderBlock = null;
                    return null;
                }
                CrystalPos pos = validPos.get(0);
                renderDamageVal = pos.getDamage();
                renderBlock = pos.getPos();
                return pos.getPos();
            }
            CrystalPos pos = validPos.get(chainCount);
            renderDamageVal = pos.getDamage();
            renderBlock = pos.getPos();
            bestPos = renderBlock;
            if (chainCount == 0) {
                chainCount = chainStep.getValue();
            } else {
                chainCount--;
            }
        } else {
            try {
                renderDamageVal = CrystalUtil.calculateDamage(bestPos, ezTarget, false);
            } catch (NullPointerException nullPointerException) {
                renderDamageVal = bestDamage;
            }
            renderBlock = bestPos;
        }

        return bestPos;
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

            // set min damage to 2 if we want to kill the dude fast
            double miniumDamage;
            if (CrystalUtil.calculateDamage(crystal, target, ignoreTerrain.getValue()) >= minBreak.getValue()) {
                facePlacing = false;
                miniumDamage = this.minBreak.getValue();
            } else if (((EntityUtil.getHealth(target) <= facePlaceHP.getValue() && faceplace.getValue()) || CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) || fpbind.isDown()) && (!stopFPWhenSword.getValue() || !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword))) {
                miniumDamage = EntityUtil.isInHole(target) ? 1 : 2;
                facePlacing = true;
            } else {
                facePlacing = false;
                miniumDamage = this.minBreak.getValue();
            }

            double targetDamage = CrystalUtil.calculateDamage(crystal, target, ignoreTerrain.getValue());
            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = 0;
            if (!ignoreSelfDamage.getValue()) {
                selfDamage = CrystalUtil.calculateDamage(crystal, mc.player, ignoreTerrain.getValue());
            }
            if (selfDamage > maxSelfBreak.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;
            switch (crystalLogic.getValue()) {
                case "Smart":
                    return targetDamage - selfDamage;
                case "Damage":
                    return targetDamage;
                case "Strict":
                double distance = mc.player.getDistanceSq(crystal);
                return targetDamage - (selfDamage * 0.5 + (distance > 3 ? distance : 0) * (EntityUtil.canEntityFeetBeSeen(crystal) ? 0.2 : 0.5));
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

            double miniumDamage;
            if (CrystalUtil.calculateDamage(blockPos, target, ignoreTerrain.getValue()) >= minPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.minPlace.getValue();
            } else if (((EntityUtil.getHealth(target) <= facePlaceHP.getValue() && faceplace.getValue()) || CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) || fpbind.isDown()) && (!stopFPWhenSword.getValue() || !(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword))) {
                miniumDamage = EntityUtil.isInHole(target) ? 1 : 2;
                facePlacing = true;
            } else {
                facePlacing = false;
                miniumDamage = this.minPlace.getValue();
            }

            double targetDamage = CrystalUtil.calculateDamage(blockPos, target, ignoreTerrain.getValue());
            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = 0;
            if (!ignoreSelfDamage.getValue()) {
                selfDamage = CrystalUtil.calculateDamage(blockPos, mc.player, ignoreTerrain.getValue());
            }
            if (selfDamage > maxSelfPlace.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            switch (crystalLogic.getValue()) {
                case "Smart":
                    return targetDamage - selfDamage;
                case "Damage":
                    return targetDamage;
                case "Strict":
                    double distance = mc.player.getDistanceSq(blockPos);
                    return targetDamage - (selfDamage * 0.5 + (distance > 3 ? distance : 0) * (CrystalUtil.canSeePos(blockPos) ? 0.2 : 0.5));
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

    private void setYawPitch(@NotNull EntityEnderCrystal crystal) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
        if (rotateMode.is("Full")) {
            WurstplusThree.ROTATION_MANAGER.setPlayerRotationsStep(yaw, pitch, 3);
            rotationPause = true;
        }
    }

    public void setYawPitch(@NotNull BlockPos pos) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
        if (rotateMode.is("Full")) {
            WurstplusThree.ROTATION_MANAGER.setPlayerRotationsStep(yaw, pitch, 3);
            rotationPause = true;
        }
    }

    private void updateEntityID() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity.getEntityId() <= this.highestID) continue;
            this.highestID = entity.getEntityId();
        }
    }

    private void attackID(BlockPos pos, int id) {
        Entity entity = mc.world.getEntityByID(id);
        if (entity == null || entity instanceof EntityEnderCrystal) {
            AttackThread attackThread = new AttackThread(id, pos, 0);
            attackThread.start();
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderBlock == null) return;

        boolean outline = false;
        boolean solid = false;
        if (!mode.is("Circle") && !mode.is("Column")) {
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
            RenderUtil.drawBoxESP((flat.getValue()) ? new BlockPos(renderBlock.getX(), renderBlock.getY() + 1, renderBlock.getZ()) : renderBlock, renderFillColour.getValue(), renderBoxColour.getValue(), width.getValue(), outline, solid, true, (flat.getValue()) ? hight.getValue() : 0f, false, false, false, false, 0);
        } else if (mode.is("Circle")) {
            RenderUtil.drawCircle(renderBlock.getX(), (flat.getValue()) ? renderBlock.getY() + 1 : renderBlock.getY(), renderBlock.getZ(), radius.getValue().floatValue(), renderBoxColour.getValue());
        } else {
            RenderUtil.drawColumn(renderBlock.getX(), (flat.getValue()) ? renderBlock.getY() + 1 : renderBlock.getY(), renderBlock.getZ(), radius.getValue().floatValue(), renderBoxColour.getValue(), 5, columnHight.getValue());
        }
        if (renderDamage.getValue()) {
            RenderUtil.drawText(renderBlock, ((Math.floor(this.renderDamageVal) == this.renderDamageVal) ? Integer.valueOf((int) this.renderDamageVal) : String.format("%.1f", this.renderDamageVal)) + "", Gui.INSTANCE.customFont.getValue());
        }
    }

    @Override
    public void onEnable() {
        placeTimeout = this.placeDelay.getValue();
        breakTimeout = this.breakDelay.getValue();
        placeTimeoutFlag = false;
        isRotating = false;
        ezTarget = null;
        facePlacing = false;
        chainCount = chainStep.getValue();
        attemptedCrystals.clear();
        hasPacketBroke = false;
        placeTimeoutFlag = false;
        alreadyAttacking = false;
        currentChainCounter = 0;
        obiFeetCounter = 0;
        crystalLatency = 0;
        start = 0;
        highestID = -100000;
        staticEnderCrystal = null;
        staticPos = null;
        rotationPause = false;
    }

    @Override
    public String getDisplayInfo() {
        return crystalLatency + "ms";
    }

    // terrain ignoring raytrace stuff made by wallhacks_ and node3112
    // moved to CyrstalUtil ~travis
    //hello ~travis
}

final class AttackThread
        extends Thread implements Globals {
    private final BlockPos pos;
    private final int id;
    private final int delay;

    public AttackThread(int idIn, BlockPos posIn, int delayIn) {
        this.id = idIn;
        this.pos = posIn;
        this.delay = delayIn;
    }

    @Override
    public void run() {
        try {
            this.wait(this.delay);
            CPacketUseEntity attack = new CPacketUseEntity();
            attack.entityId = this.id;
            attack.action = CPacketUseEntity.Action.ATTACK;
            mc.player.connection.sendPacket(attack);
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

final class Threads extends Thread {
    ThreadType type;
    BlockPos bestBlock;
    EntityEnderCrystal bestCrystal;

    public Threads(@NotNull ThreadType type) {
        this.type = type;
    }

    @Override
    public void run() {
        if (this.type == ThreadType.BLOCK) {
            bestBlock = CrystalAura.INSTANCE.getBestBlock();
            CrystalAura.INSTANCE.staticPos = bestBlock;
        } else if (this.type == ThreadType.CRYSTAL) {
            bestCrystal = CrystalAura.INSTANCE.getBestCrystal();
            CrystalAura.INSTANCE.staticEnderCrystal = bestCrystal;
        }
    }
}

//final class CaThread extends Thread {
//    @Override
//    public void run() {
//        ClientMessage.sendMessage("run");
//        if (CrystalAura.INSTANCE.rotateMode.is("Fuck")) {
//            while (CrystalAura.INSTANCE.isEnabled()) {
//                CrystalAura.INSTANCE.threadOngoing.set(true);
//                CrystalAura.INSTANCE.doCrystalAura();
//                CrystalAura.INSTANCE.threadOngoing.set(false);
//                try {
//                    Thread.sleep(CrystalAura.INSTANCE.fuckDelay.getValue());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}

enum ThreadType {
    BLOCK,
    CRYSTAL
}
