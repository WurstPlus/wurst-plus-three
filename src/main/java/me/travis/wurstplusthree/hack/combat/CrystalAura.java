package me.travis.wurstplusthree.hack.combat;

import com.mojang.authlib.GameProfile;
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
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Hack.Registration(name = "Crystal Aura", description = "the goods", category = Hack.Category.COMBAT, isListening = false)
public class CrystalAura extends Hack {

    // TODO : FIGURE OUT WHY IT SOMETIMES JUST STOPS
	//        Probably because of the shitty phobos event system
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

    BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", true, this);
    BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true, this);

    BooleanSetting packetSafe = new BooleanSetting("Packet Safe", false, this);
    BooleanSetting predictCrystal = new BooleanSetting("Predict Crystal", true, this);
    BooleanSetting predictBlock = new BooleanSetting("Predict Block", true, this);
    BooleanSetting predictPlace = new BooleanSetting("Predict Place", false, this);
    IntSetting predictTicks = new IntSetting("Predict Ticks", 2, 0, 10, this);

    EnumSetting fastMode = new EnumSetting("Fast", "Ghost", Arrays.asList("Off", "Ignore", "Ghost"), this);

    BooleanSetting thirteen = new BooleanSetting("1.13", false, this);

    BooleanSetting faceplace = new BooleanSetting("Tabbott", true, this);
    IntSetting facePlaceHP = new IntSetting("Tabbott HP", 8, 0, 36, this);

    BooleanSetting fuckArmour = new BooleanSetting("Armour Fucker", true, this);
    IntSetting fuckArmourHP = new IntSetting("Armour%", 20, 0, 100, this);

    BooleanSetting stopFPWhenSword = new BooleanSetting("Stop Faceplace Sword", false, this);
    BooleanSetting ignoreTerrain = new BooleanSetting("Ignore Webs", true, this);

    BooleanSetting placeSwing = new BooleanSetting("Place Swing", true, this);
    BooleanSetting attackPacket = new BooleanSetting("AttackPacket", true, this);

    BooleanSetting chainMode = new BooleanSetting("Chain Mode", false, this);
    IntSetting chainCounter = new IntSetting("Chain Counter", 3, 0, 10, this);
    IntSetting chainStep = new IntSetting("Chain Step", 2, 0, 5, this);
    

    EnumSetting mode = new EnumSetting("Render", "Pretty", Arrays.asList("Pretty", "Solid", "Outline", "Flat"), this);
    IntSetting width = new IntSetting("Width", 1, 1, 10, this);
    ColourSetting renderFillColour = new ColourSetting("Fill Colour", new Colour(0, 0, 0, 255), this);
    ColourSetting renderBoxColour = new ColourSetting("Box Colour", new Colour(255, 255, 255, 255), this);
    BooleanSetting renderDamage = new BooleanSetting("RenderDamage", true, this);

    private final List<EntityEnderCrystal> attemptedCrystals = new ArrayList<>();

    public EntityPlayer ezTarget = null;
    private BlockPos renderBlock = null;

    private double renderDamageVal = 0;

    private float yaw;
    private float pitch;

    private boolean alreadyAttacking;
    private boolean placeTimeoutFlag;
    private boolean hasPacketBroke;
    private boolean isRotating;
    private boolean didAnything;

    private int currentChainCounter;
    private int chainCount;
    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;

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
            if (this.predictBlock.getValue()) {
                if (crystal != null) {
                    for (EntityPlayer player : mc.world.playerEntities) {
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
            ClientMessage.sendErrorMessage("Rubberband detected, resetting rotations!");
            RotationUtil.resetRotations();
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
                mc.playerController.syncCurrentPlayItem();
            }
        } else {
            offhandCheck = true;
        }

        didAnything = true;
        setYawPitch(targetBlock);
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
            for (BlockPos blockPos : CrystalUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), true, this.thirteen.getValue())) {
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
            EntityPlayer player = target;
            if (this.predictPlace.getValue()) {
                player = this.newTarget(target);
            }

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
            if ((EntityUtil.getHealth(player) <= facePlaceHP.getValue() && faceplace.getValue()) ||
                    (CrystalUtil.getArmourFucker(player, fuckArmourHP.getValue()) && fuckArmour.getValue())) {
                miniumDamage = EntityUtil.isInHole(player) ? 0.5 : 2;
            } else {
                miniumDamage = this.minHpBreak.getValue();
            }

            double targetDamage = CrystalUtil.calculateDamage(crystal, player);
            if (targetDamage < miniumDamage && EntityUtil.getHealth(player) - targetDamage > 0) return 0;
            double selfDamage = CrystalUtil.calculateDamage(crystal, mc.player);
            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private double isBlockGood(BlockPos blockPos, EntityPlayer target) {
        if (this.isPlayerValid(target)) {
            EntityPlayer player = target;
            if (this.predictPlace.getValue()) {
                player = this.newTarget(target);
            }

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
            if ((EntityUtil.getHealth(player) <= facePlaceHP.getValue() && faceplace.getValue()) ||
                    (CrystalUtil.getArmourFucker(player, fuckArmourHP.getValue()) && fuckArmour.getValue())) {
                miniumDamage = EntityUtil.isInHole(player) ? 0.5 : 2;
            }
            else {
                miniumDamage = this.minHpPlace.getValue();
            }

            if (ignoreTerrain.getValue() && mc.world.getBlockState(EntityUtil.getRoundedBlockPos(player)).getBlock() == Blocks.WEB) {
                mc.world.setBlockToAir(EntityUtil.getRoundedBlockPos(player));
            }

            double targetDamage = CrystalUtil.calculateDamage(blockPos, player);
            if (targetDamage < miniumDamage && EntityUtil.getHealth(player) - targetDamage > 0) return 0;
            double selfDamage = CrystalUtil.calculateDamage(blockPos, mc.player);
            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private boolean isPlayerValid(EntityPlayer player) {
        if (player.getHealth() + player.getAbsorptionAmount() <= 0 || player == mc.player) return false;
        if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) return false;
        if (player.getDistanceSq(mc.player) > 13 * 13) return false;
        return !stopFPWhenSword.getValue() || mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD;
    }

    private EntityPlayer newTarget(EntityPlayer currentTarget) {
        if (!(currentTarget.motionX > 0.08 || currentTarget.motionX < -0.08))
            return currentTarget;
        if (!(currentTarget.motionZ > 0.08 || currentTarget.motionZ < -0.08))
            return currentTarget;
        currentTarget.getUniqueID();
        GameProfile profile = new GameProfile(currentTarget.getUniqueID(), currentTarget.getName());
        EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP(mc.world, profile);
        Vec3d extrapolatePosition = MathsUtil.extrapolatePlayerPosition(currentTarget, this.predictTicks.getValue());
        newTarget.copyLocationAndAnglesFrom(currentTarget);
        newTarget.posX = extrapolatePosition.x;
        newTarget.posY = extrapolatePosition.y;
        newTarget.posZ = extrapolatePosition.z;
        newTarget.setHealth(EntityUtil.getHealth(currentTarget));
        newTarget.inventory.copyInventory(currentTarget.inventory);
        return newTarget;
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
        RenderUtil.drawBoxESP(renderBlock, renderFillColour.getValue(), renderBoxColour.getValue(), width.getValue(), outline, solid, true);


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
        chainCount = chainStep.getValue();
        attemptedCrystals.clear();
        hasPacketBroke = false;
        placeTimeoutFlag = false;
        alreadyAttacking = false;
        currentChainCounter = 0;
    }

    @Override
    public String getDisplayInfo() {
        return this.ezTarget != null ? this.ezTarget.getName() : null;
    }

}
