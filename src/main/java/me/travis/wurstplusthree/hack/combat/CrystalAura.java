package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.chat.AutoEz;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.CrystalPos;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Hack.Registration(name = "Crystal Aura", description = "the goods", category = Hack.Category.COMBAT, isListening = false)
public class CrystalAura extends Hack {

    // TODO : FIGURE OUT WHY IT SOMETIMES JUST STOPS
    public static CrystalAura INSTANCE;

    public CrystalAura() {
        INSTANCE = this;
    }

    BooleanSetting place = new BooleanSetting("Place", true, this);
    BooleanSetting breaK = new BooleanSetting("Break", true, this);
    BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true, this);

    DoubleSetting breakRange = new DoubleSetting("Break Range", 5.0, 0.0, 6.0, this);
    DoubleSetting placeRange = new DoubleSetting("Place Range", 5.0, 0.0, 6.0, this);
    DoubleSetting breakRangeWall = new DoubleSetting("Break Range Wall", 3.0, 0.0, 6.0, this);
    DoubleSetting placeRangeWall = new DoubleSetting("Place Range Wall", 3.0, 0.0, 6.0, this);

    IntSetting placeDelay = new IntSetting("Place Delay", 0, 0, 10, this);
    IntSetting breakDelay = new IntSetting("Break Delay", 1, 0, 10, this);

    IntSetting minHpPlace = new IntSetting("HP Enemy Place", 9, 0, 36, this);
    IntSetting minHpBreak = new IntSetting("HP Enemy Break", 8, 0, 36, this);
    IntSetting maxSelfDamage = new IntSetting("Max Self Damage", 5, 0, 36, this);

    EnumSetting rotateMode = new EnumSetting("Rotate", "Off", Arrays.asList("Off", "Packet", "Full"), this);

    BooleanSetting detectRubberBand = new BooleanSetting("Detect Rubberband", false, this);
    BooleanSetting raytrace = new BooleanSetting("Raytrace", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    BooleanSetting placeSwing = new BooleanSetting("Place Swing", true, this);

    EnumSetting autoSwitch = new EnumSetting("Switch", "None", Arrays.asList("Mainhand", "None"), this);
    BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true, this);

    BooleanSetting packetSafe = new BooleanSetting("Packet Safe", false, this);
    BooleanSetting predictCrystal = new BooleanSetting("Predict Crystal", true, this);
    BooleanSetting predictBlock = new BooleanSetting("Predict Block", true, this);

    BooleanSetting palceObiFeet = new BooleanSetting("Place Feet Obi", false, this);
    BooleanSetting ObiYCheck = new BooleanSetting("Place Obi Y Check", false, this);
    BooleanSetting rotateObiFeet = new BooleanSetting("Place Feet Rotate", false, this);
    IntSetting timeoutTicksObiFeet = new IntSetting("Place Feet Timeout", 3, 0, 5, this);

    EnumSetting fastMode = new EnumSetting("Fast", "Ghost", Arrays.asList("Off", "Ignore", "Ghost", "Sound"), this);

    BooleanSetting thirteen = new BooleanSetting("1.13", false, this);

    BooleanSetting faceplace = new BooleanSetting("Tabbott", true, this);
    IntSetting facePlaceHP = new IntSetting("Tabbott HP", 8, 0, 36, this);
    IntSetting facePlaceDelay = new IntSetting("Tabbott Delay", 5, 0, 10, this);
    KeySetting fpbind = new KeySetting("Tabbott Bind", -1, this);

    BooleanSetting fuckArmour = new BooleanSetting("Armour Fucker", true, this);
    IntSetting fuckArmourHP = new IntSetting("Armour%", 20, 0, 100, this);

    BooleanSetting stopFPWhenSword = new BooleanSetting("Stop Faceplace Sword", false, this);
    BooleanSetting ignoreTerrain = new BooleanSetting("TerrainTrace", true, this);
    BooleanSetting crystalLogic = new BooleanSetting("CrystalCheck", false, this);

    BooleanSetting attackPacket = new BooleanSetting("AttackPacket", true, this);

    BooleanSetting chainMode = new BooleanSetting("Chain Mode", false, this);
    IntSetting chainCounter = new IntSetting("Chain Counter", 3, 0, 10, this);
    IntSetting chainStep = new IntSetting("Chain Step", 2, 0, 5, this);
    EnumSetting mode = new EnumSetting("Render", "Pretty", Arrays.asList("Pretty", "Solid", "Outline", "Circle"), this);
    BooleanSetting flat = new BooleanSetting("Flat", false, this);
    DoubleSetting hight = new DoubleSetting("FlatHeight", 0.2, -2.0, 0.0, this);
    IntSetting width = new IntSetting("Width", 1, 1, 10, this);
    DoubleSetting radius = new DoubleSetting("Radius", 0.7, 0.0, 5.0, this);
    ColourSetting renderFillColour = new ColourSetting("Fill Colour", new Colour(0, 0, 0, 255), this);
    ColourSetting renderBoxColour = new ColourSetting("Box Colour", new Colour(255, 255, 255, 255), this);
    BooleanSetting renderDamage = new BooleanSetting("RenderDamage", true, this);

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

    private int currentChainCounter;
    private int chainCount;
    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;
    private int facePlaceDelayCounter;
    private int obiFeetCounter;

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotateMode.is("Full")) {
            if (this.isRotating) {
                WurstplusThree.ROTATION_MANAGER.setPlayerRotations(yaw, pitch);
            }
            this.doCrystalAura();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPacketSend(PacketEvent.Send event) {
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
            EntityEnderCrystal crystal = (EntityEnderCrystal) packet.getEntityFromWorld(mc.world);
            if (this.predictBlock.getValue() && place.getValue()) {
                if (crystal != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal) {
                    for (EntityPlayer player : mc.world.playerEntities) {
                        if (player == null || crystal == null) return;
                        if (this.isBlockGood(crystal.getPosition().down(), player) != 0) {
                            BlockUtil.placeCrystalOnBlock(crystal.getPosition().down(), EnumHand.MAIN_HAND, true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSpawnObject packet;
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = event.getPacket()).getType() == 51) {
            this.hasPacketBroke = false;
            try { // minecraft may update player list during us looping through it
                for (EntityPlayer target : mc.world.playerEntities) {
                    if (this.isCrystalGood(new EntityEnderCrystal(mc.world, packet.getX(), packet.getY(), packet.getZ()), target) != 0) {
                        if (this.predictCrystal.getValue()) {
                            CPacketUseEntity predict = new CPacketUseEntity();
                            predict.entityId = packet.getEntityID();
                            predict.action = CPacketUseEntity.Action.ATTACK;
                            mc.player.connection.sendPacket(predict);
                            if (!this.swing.is("None")) {
                                BlockUtil.swingArm(swing);
                            }
                            if (packetSafe.getValue()) {
                                this.hasPacketBroke = true;
                            }
                        }
                        break;
                    }
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet_ = event.getPacket();
            for (int id : packet_.getEntityIDs()) {
                try {
                    Entity entity = mc.world.getEntityByID(id);
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.attemptedCrystals.remove(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && detectRubberBand.getValue()) {
            //ClientMessage.sendErrorMessage("Rubberband detected, resetting rotations!");
            RotationUtil.resetRotations();
        }
        if (event.getPacket() instanceof SPacketSoundEffect && fastMode.getValue().equals("Sound")) {
            if (((SPacketSoundEffect) event.getPacket()).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal)
                        if (crystal.getDistance(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()) <= breakRange.getValue()) {
                            crystal.setDead();
                        }
                }
            }
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

        if (this.place.getValue() && placeDelayCounter > placeTimeout && (facePlaceDelayCounter >= facePlaceDelay.getValue() || !facePlacing)) {
            this.placeCrystal();
        }
        if (this.breaK.getValue() && breakDelayCounter > breakTimeout && !hasPacketBroke) {
            this.breakCrystal();
        }

        if (!didAnything) {
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
        BlockPos targetBlock = this.getBestBlock();
        if (targetBlock == null) return;

        placeDelayCounter = 0;
        facePlaceDelayCounter = 0;
        alreadyAttacking = false;
        boolean offhandCheck = false;

        if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && autoSwitch.getValue().equals("Mainhand")) {
                if (this.findCrystalsHotbar() == -1) return;
                mc.player.inventory.currentItem = this.findCrystalsHotbar();
                mc.playerController.syncCurrentPlayItem();
            }
        } else {
            offhandCheck = true;
        }

        didAnything = true;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            setYawPitch(targetBlock);
            BlockUtil.placeCrystalOnBlock(targetBlock, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, placeSwing.getValue());
        }
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
        EntityUtil.attackEntity(crystal, this.attackPacket.getValue());
        if (!this.swing.is("None")) {
            BlockUtil.swingArm(swing);
        }
        breakDelayCounter = 0;
    }

    private EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        EntityEnderCrystal bestCrystal = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;
            for (EntityPlayer target : mc.world.playerEntities) {
                double targetDamage = this.isCrystalGood(crystal, target);
                if (targetDamage == 0) continue;
                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    this.ezTarget = target;
                    bestCrystal = crystal;
                }
            }
        }
        if (this.ezTarget != null) {
            AutoEz.INSTANCE.targets.put(this.ezTarget.getName(), 20);
        }
        return bestCrystal;
    }

    private BlockPos getBestBlock() {
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

        for (EntityPlayer target : mc.world.playerEntities) {
            for (BlockPos blockPos : CrystalUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), !crystalLogic.getValue(), this.thirteen.getValue())) {
                double targetDamage = isBlockGood(blockPos, target);
                if (targetDamage == 0) continue;
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
            AutoEz.INSTANCE.targets.put(this.ezTarget.getName(), 20);
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
            renderDamageVal = bestDamage;
            renderBlock = bestPos;
        }

        return bestPos;
    }

    private double isCrystalGood(EntityEnderCrystal crystal, EntityPlayer target) {
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
            if (calculateDamage(crystal, target) >= minHpPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            } else if ((EntityUtil.getHealth(target) <= facePlaceHP.getValue() && faceplace.getValue()) || (CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) && fuckArmour.getValue()) || fpbind.isDown()) {
                miniumDamage = EntityUtil.isInHole(target) ? 0.5 : 2;
                facePlacing = true;
            } else {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            }

            double targetDamage = calculateDamage(crystal, target);
            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = calculateDamage(crystal, mc.player);
            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private double isBlockGood(BlockPos blockPos, EntityPlayer target) {
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

            // set min damage to 2/.5 if we want to kill the dude fast
            double miniumDamage;
            if (calculateDamage(blockPos, target) >= minHpPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            } else if ((EntityUtil.getHealth(target) <= facePlaceHP.getValue() && faceplace.getValue()) ||
                    (CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) && fuckArmour.getValue()) || fpbind.isDown()) {
                miniumDamage = EntityUtil.isInHole(target) ? 0.5 : 2;
                facePlacing = true;
            } else {
                miniumDamage = this.minHpPlace.getValue();
                facePlacing = false;
            }

            double targetDamage = calculateDamage(blockPos, target);
            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = calculateDamage(blockPos, mc.player);
            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private boolean isPlayerValid(EntityPlayer player) {
        if (player.getHealth() + player.getAbsorptionAmount() <= 0 || player == mc.player) return false;
        if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) return false;
        if (player.getName().equals(mc.player.getName())) return false;
        if (player.getDistanceSq(mc.player) > 13 * 13) return false;
        if (this.palceObiFeet.getValue() && obiFeetCounter >= timeoutTicksObiFeet.getValue() && mc.player.getDistance(player) < 5) {
            this.blockObiNextToPlayer(player);
        }
        return !stopFPWhenSword.getValue() || mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD;
    }

    private void blockObiNextToPlayer(EntityPlayer player) {
        if(ObiYCheck.getValue() && Math.floor(player.posY) == Math.floor(mc.player.posY))return;
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

    private void setYawPitch(EntityEnderCrystal crystal) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
    }

    private void setYawPitch(BlockPos pos) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderBlock == null) return;

        boolean outline = false;
        boolean solid = false;
        if (!mode.is("Circle")) {
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
            RenderUtil.drawBoxESP((flat.getValue()) ? new BlockPos(renderBlock.getX(), renderBlock.getY()+1, renderBlock.getZ()) : renderBlock, renderFillColour.getValue(), renderBoxColour.getValue(), width.getValue(), outline, solid, true, (flat.getValue()) ? hight.getValue() : 0f, false, false, false, false, 0);
        } else {
            RenderUtil.drawCircle(renderBlock.getX(), (flat.getValue()) ? renderBlock.getY() + 1: renderBlock.getY(), renderBlock.getZ(), radius.getValue().floatValue(), renderBoxColour.getValue());
        }
        if (renderDamage.getValue()) {
            RenderUtil.drawText(renderBlock, ((Math.floor(this.renderDamageVal) == this.renderDamageVal) ? Integer.valueOf((int) this.renderDamageVal) : String.format("%.1f", this.renderDamageVal)) + "");
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
    }

    @Override
    public String getDisplayInfo() {
        return (facePlacing ? "FacePlacing " : "Chasing ") + (this.ezTarget != null ? this.ezTarget.getName() : "");
    }

    // terrain ignoring raytrace stuff made by wallhacks_ and node3112

    public float calculateDamage(BlockPos pos, Entity target) {
        return getExplosionDamage(target, new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), 6.0f);
    }

    public float calculateDamage(Entity crystal, Entity target) {
        return getExplosionDamage(target, new Vec3d(crystal.posX, crystal.posY, crystal.posZ), 6.0f);
    }

    public float getExplosionDamage(Entity targetEntity, Vec3d explosionPosition, float explosionPower) {
        Vec3d entityPosition = new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
        if (targetEntity.isImmuneToExplosions()) return 0.0f;
        explosionPower *= 2.0f;
        double distanceToSize = entityPosition.distanceTo(explosionPosition) / explosionPower;
        double blockDensity = 0.0;
        // Offset to "fake position"
        AxisAlignedBB bbox = targetEntity.getEntityBoundingBox().offset(targetEntity.getPositionVector().subtract(entityPosition));
        Vec3d bboxDelta = new Vec3d(
                1.0 / ((bbox.maxX - bbox.minX) * 2.0 + 1),
                1.0 / ((bbox.maxY - bbox.minY) * 2.0 + 1),
                1.0 / ((bbox.maxZ - bbox.minZ) * 2.0 + 1)
        );

        double xOff = (1.0 - Math.floor(1.0 / bboxDelta.x) * bboxDelta.x) / 2.0;
        double zOff = (1.0 - Math.floor(1.0 / bboxDelta.z) * bboxDelta.z) / 2.0;

        if (bboxDelta.x >= 0.0 && bboxDelta.y >= 0.0 && bboxDelta.z >= 0.0) {
            int nonSolid = 0;
            int total = 0;

            for (double x = 0.0; x <= 1.0; x += bboxDelta.x) {
                for (double y = 0.0; y <= 1.0; y += bboxDelta.y) {
                    for (double z = 0.0; z <= 1.0; z += bboxDelta.z) {
                        Vec3d startPos = new Vec3d(
                                xOff + bbox.minX + (bbox.maxX - bbox.minX) * x,
                                bbox.minY + (bbox.maxY - bbox.minY) * y,
                                zOff + bbox.minZ + (bbox.maxZ - bbox.minZ) * z
                        );

                        if (!rayTraceSolidCheck(startPos, explosionPosition)) ++nonSolid;
                        ++total;
                    }
                }
            }

            blockDensity = (double) nonSolid / (double) total;
        }

        double densityAdjust = (1.0 - distanceToSize) * blockDensity;
        float damage = (float) (int) ((densityAdjust * densityAdjust + densityAdjust) / 2.0 * 7.0 * explosionPower + 1.0);

        if (targetEntity instanceof EntityLivingBase)
            damage = getBlastReduction((EntityLivingBase) targetEntity, getDamageFromDifficulty(damage, mc.world.getDifficulty()),
                    new Explosion(mc.world, null, explosionPosition.x, explosionPosition.y, explosionPosition.z,
                            explosionPower / 2.0f, false, true));

        return damage;
    }

    public boolean rayTraceSolidCheck(Vec3d start, Vec3d end) {
        Minecraft mc = Minecraft.getMinecraft();

        if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
            if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
                int currX = MathHelper.floor(start.x);
                int currY = MathHelper.floor(start.y);
                int currZ = MathHelper.floor(start.z);

                int endX = MathHelper.floor(end.x);
                int endY = MathHelper.floor(end.y);
                int endZ = MathHelper.floor(end.z);

                BlockPos blockPos = new BlockPos(currX, currY, currZ);
                IBlockState blockState = mc.world.getBlockState(blockPos);
                net.minecraft.block.Block block = blockState.getBlock();

                if ((blockState.getCollisionBoundingBox(mc.world, blockPos) != Block.NULL_AABB) &&
                        block.canCollideCheck(blockState, false) && (getBlocks().contains(block) || !ignoreTerrain.getValue())) {
                    return true;
                }

                double seDeltaX = end.x - start.x;
                double seDeltaY = end.y - start.y;
                double seDeltaZ = end.z - start.z;

                int steps = 200;

                while (steps-- >= 0) {
                    if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) return false;
                    if (currX == endX && currY == endY && currZ == endZ) return false;

                    boolean unboundedX = true;
                    boolean unboundedY = true;
                    boolean unboundedZ = true;

                    double stepX = 999.0;
                    double stepY = 999.0;
                    double stepZ = 999.0;
                    double deltaX = 999.0;
                    double deltaY = 999.0;
                    double deltaZ = 999.0;

                    if (endX > currX) {
                        stepX = currX + 1.0;
                    } else if (endX < currX) {
                        stepX = currX;
                    } else {
                        unboundedX = false;
                    }

                    if (endY > currY) {
                        stepY = currY + 1.0;
                    } else if (endY < currY) {
                        stepY = currY;
                    } else {
                        unboundedY = false;
                    }

                    if (endZ > currZ) {
                        stepZ = currZ + 1.0;
                    } else if (endZ < currZ) {
                        stepZ = currZ;
                    } else {
                        unboundedZ = false;
                    }

                    if (unboundedX) deltaX = (stepX - start.x) / seDeltaX;
                    if (unboundedY) deltaY = (stepY - start.y) / seDeltaY;
                    if (unboundedZ) deltaZ = (stepZ - start.z) / seDeltaZ;

                    if (deltaX == 0.0) deltaX = -1.0e-4;
                    if (deltaY == 0.0) deltaY = -1.0e-4;
                    if (deltaZ == 0.0) deltaZ = -1.0e-4;

                    EnumFacing facing;

                    if (deltaX < deltaY && deltaX < deltaZ) {
                        facing = endX > currX ? EnumFacing.WEST : EnumFacing.EAST;
                        start = new Vec3d(stepX, start.y + seDeltaY * deltaX, start.z + seDeltaZ * deltaX);
                    } else if (deltaY < deltaZ) {
                        facing = endY > currY ? EnumFacing.DOWN : EnumFacing.UP;
                        start = new Vec3d(start.x + seDeltaX * deltaY, stepY, start.z + seDeltaZ * deltaY);
                    } else {
                        facing = endZ > currZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        start = new Vec3d(start.x + seDeltaX * deltaZ, start.y + seDeltaY * deltaZ, stepZ);
                    }

                    currX = MathHelper.floor(start.x) - (facing == EnumFacing.EAST ? 1 : 0);
                    currY = MathHelper.floor(start.y) - (facing == EnumFacing.UP ? 1 : 0);
                    currZ = MathHelper.floor(start.z) - (facing == EnumFacing.SOUTH ? 1 : 0);

                    blockPos = new BlockPos(currX, currY, currZ);
                    blockState = mc.world.getBlockState(blockPos);
                    block = blockState.getBlock();

                    if (block.canCollideCheck(blockState, false) && (getBlocks().contains(block) || !ignoreTerrain.getValue())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        float enchantmentModifierDamage = 0.0f;
        try {
            enchantmentModifierDamage = (float) EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(),
                    DamageSource.causeExplosionDamage(explosion));
        } catch (Exception ignored) {
        }
        enchantmentModifierDamage = MathHelper.clamp(enchantmentModifierDamage, 0.0f, 20.0f);

        damage *= 1.0f - enchantmentModifierDamage / 25.0f;
        PotionEffect resistanceEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);

        if (entity.isPotionActive(MobEffects.RESISTANCE) && resistanceEffect != null)
            damage = damage * (25.0f - (resistanceEffect.getAmplifier() + 1) * 5.0f) / 25.0f;

        damage = Math.max(damage, 0.0f);
        return damage;
    }

    public List<Block> getBlocks() {
        return Arrays.asList(
                Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.COMMAND_BLOCK, Blocks.BARRIER, Blocks.ENCHANTING_TABLE, Blocks.ENDER_CHEST, Blocks.END_PORTAL_FRAME, Blocks.BEACON, Blocks.ANVIL
        );
    }

    public float getDamageFromDifficulty(float damage, EnumDifficulty difficulty) {
        switch (difficulty) {
            case PEACEFUL: {
                return 0.0f;
            }
            case EASY: {
                return damage * 0.5f;
            }
            case NORMAL: {
                return damage;
            }
            case HARD:
            default: {
                return damage * 1.5f;
            }
        }
    }
}
