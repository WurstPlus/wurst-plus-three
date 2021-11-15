



package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;
import net.minecraft.util.math.*;
import me.earth.phobos.*;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.earth.phobos.event.events.*;
import java.awt.*;
import me.earth.phobos.features.modules.client.*;
import net.minecraft.entity.*;
import me.earth.phobos.util.*;
import me.earth.phobos.features.modules.player.*;
import net.minecraft.block.*;
import me.earth.phobos.features.command.*;
import net.minecraft.util.*;
import java.util.*;

public class Surround extends Module
{
    public static boolean isPlacing;
    private final Setting<Boolean> server;
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> raytrace;
    private final Setting<InventoryUtil.Switch> switchMode;
    private final Setting<Boolean> center;
    private final Setting<Boolean> helpingBlocks;
    private final Setting<Boolean> intelligent;
    private final Setting<Boolean> antiPedo;
    private final Setting<Integer> extender;
    private final Setting<Boolean> extendMove;
    private final Setting<MovementMode> movementMode;
    private final Setting<Double> speed;
    private final Setting<Integer> eventMode;
    private final Setting<Boolean> floor;
    private final Setting<Boolean> echests;
    private final Setting<Boolean> blockface;
    private final Setting<Boolean> noGhost;
    private final Setting<Boolean> info;
    private final Setting<Integer> retryer;
    private final Setting<Boolean> endPortals;
    private final Setting<Boolean> render;
    public final Setting<Boolean> colorSync;
    public final Setting<Boolean> box;
    private final Setting<Integer> boxAlpha;
    public final Setting<Boolean> outline;
    public final Setting<Boolean> customOutline;
    private final Setting<Integer> cRed;
    private final Setting<Integer> cGreen;
    private final Setting<Integer> cBlue;
    private final Setting<Integer> cAlpha;
    private final Setting<Float> lineWidth;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final TimerUtil timer;
    private final TimerUtil retryTimer;
    private final Set<Vec3d> extendingBlocks;
    private final Map<BlockPos,  Integer> retries;
    private int isSafe;
    private BlockPos startPos;
    private boolean didPlace;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements;
    private int extenders;
    private int obbySlot;
    private boolean offHand;
    private List<BlockPos> placeVectors;
    
    public Surround() {
        super("Surround",  "Surrounds you with Obsidian.",  Category.COMBAT,  true,  false,  false);
        this.server = (Setting<Boolean>)this.register(new Setting("Server", false));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", 50, 0, 250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Block/Place", 8, 1, 20));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", true));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", false));
        this.switchMode = (Setting<InventoryUtil.Switch>)this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
        this.center = (Setting<Boolean>)this.register(new Setting("Center", false));
        this.helpingBlocks = (Setting<Boolean>)this.register(new Setting("HelpingBlocks", true));
        this.intelligent = (Setting<Boolean>)this.register(new Setting("Intelligent", false,  v -> this.helpingBlocks.getValue()));
        this.antiPedo = (Setting<Boolean>)this.register(new Setting("NoPedo", false));
        this.extender = (Setting<Integer>)this.register(new Setting("Extend", 1, 1, 4));
        this.extendMove = (Setting<Boolean>)this.register(new Setting("MoveExtend", false,  v -> this.extender.getValue() > 1));
        this.movementMode = (Setting<MovementMode>)this.register(new Setting("Movement", MovementMode.STATIC));
        this.speed = (Setting<Double>)this.register(new Setting("Speed", 10.0, 0.0, 30.0,  v -> this.movementMode.getValue() == MovementMode.LIMIT || this.movementMode.getValue() == MovementMode.OFF,  "Maximum Movement Speed"));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", 3, 1, 3));
        this.floor = (Setting<Boolean>)this.register(new Setting("Floor", false));
        this.echests = (Setting<Boolean>)this.register(new Setting("Echests", false));
        this.blockface = (Setting<Boolean>)this.register(new Setting("Block Face", false));
        this.noGhost = (Setting<Boolean>)this.register(new Setting("Packet", false));
        this.info = (Setting<Boolean>)this.register(new Setting("Info", false));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", 4, 1, 15));
        this.endPortals = (Setting<Boolean>)this.register(new Setting("EndPortals", false));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", true));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false,  v -> this.render.getValue()));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", false,  v -> this.render.getValue()));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 125, 0, 255,  v -> this.box.getValue() && this.render.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true,  v -> this.render.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", false,  v -> this.outline.getValue() && this.render.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", 255, 0, 255,  v -> this.customOutline.getValue() && this.outline.getValue() && this.render.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", 255, 0, 255,  v -> this.customOutline.getValue() && this.outline.getValue() && this.render.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", 255, 0, 255,  v -> this.customOutline.getValue() && this.outline.getValue() && this.render.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", 255, 0, 255,  v -> this.customOutline.getValue() && this.outline.getValue() && this.render.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f,  v -> this.outline.getValue() && this.render.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 0, 0, 255,  v -> this.render.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255,  v -> this.render.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 0, 0, 255,  v -> this.render.getValue()));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255,  v -> this.render.getValue()));
        this.timer = new TimerUtil();
        this.retryTimer = new TimerUtil();
        this.extendingBlocks = new HashSet<Vec3d>();
        this.retries = new HashMap<BlockPos,  Integer>();
        this.extenders = 1;
        this.obbySlot = -1;
        this.placeVectors = new ArrayList<BlockPos>();
    }
    
    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }
        this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        this.startPos = new BlockPos(MathUtil.round(Surround.mc.player.posX,  0),  Math.ceil(Surround.mc.player.posY),  MathUtil.round(Surround.mc.player.posZ,  0));
        if (this.center.getValue() && !Phobos.moduleManager.isModuleEnabled("Freecam")) {
            if (Surround.mc.world.getBlockState(new BlockPos(Surround.mc.player.getPositionVector())).getBlock() == Blocks.WEB) {
                Phobos.positionManager.setPositionPacket(Surround.mc.player.posX,  this.startPos.getY(),  Surround.mc.player.posZ,  true,  true,  true);
            }
            else {
                Phobos.positionManager.setPositionPacket(this.startPos.getX() + 0.5,  this.startPos.getY(),  this.startPos.getZ() + 0.5,  true,  true,  true);
            }
        }
        if (this.shouldServer()) {
            Surround.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            Surround.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module Surround set Enabled true"));
            return;
        }
        this.retries.clear();
        this.retryTimer.reset();
    }
    
    @Override
    public void onTick() {
        if (this.eventMode.getValue() == 3) {
            this.doFeetPlace();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.eventMode.getValue() == 2) {
            this.doFeetPlace();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.eventMode.getValue() == 1) {
            this.doFeetPlace();
        }
        if (this.isSafe == 2) {
            this.placeVectors = new ArrayList<BlockPos>();
        }
    }
    
    @Override
    public void onDisable() {
        if (nullCheck()) {
            return;
        }
        if (this.shouldServer()) {
            Surround.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Serverprefix" + ClickGui.getInstance().prefix.getValue()));
            Surround.mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Server" + ClickGui.getInstance().prefix.getValue() + "module Surround set Enabled false"));
            return;
        }
        Surround.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.render.getValue() && (this.isSafe == 0 || this.isSafe == 1)) {
            this.placeVectors = this.fuckYou3arthqu4keYourCodeIsGarbage();
            for (final BlockPos pos : this.placeVectors) {
                if (!(Surround.mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) {
                    continue;
                }
                RenderUtil.drawBoxESP(pos,  ((boolean)this.colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor() : new Color(this.red.getValue(),  this.green.getValue(),  this.blue.getValue(),  this.alpha.getValue()),  this.customOutline.getValue(),  new Color(this.cRed.getValue(),  this.cGreen.getValue(),  this.cBlue.getValue(),  this.cAlpha.getValue()),  this.lineWidth.getValue(),  this.outline.getValue(),  this.box.getValue(),  this.boxAlpha.getValue(),  false);
            }
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (!this.info.getValue()) {
            return null;
        }
        switch (this.isSafe) {
            case 0: {
                return "§cUnsafe";
            }
            case 1: {
                return "§eSecure";
            }
            default: {
                return "§aSecure";
            }
        }
    }
    
    private boolean shouldServer() {
        return PingBypass.getInstance().isConnected() && this.server.getValue();
    }
    
    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        if (this.blockface.getValue()) {
            this.isSafe = 0;
            this.placeBlocks(Surround.mc.player.getPositionVector().add(0.0,  0.125,  0.0),  EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player,  1,  this.floor.getValue(),  true),  this.helpingBlocks.getValue(),  false,  false);
        }
        if (!EntityUtil.isSafe((Entity)Surround.mc.player,  0,  this.floor.getValue(),  true)) {
            this.isSafe = 0;
            this.placeBlocks(Surround.mc.player.getPositionVector().add(0.0,  0.125,  0.0),  EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player,  0,  this.floor.getValue(),  true),  this.helpingBlocks.getValue(),  false,  false);
        }
        else if (!EntityUtil.isSafe((Entity)Surround.mc.player,  -1,  false,  true)) {
            this.isSafe = 1;
            if (this.antiPedo.getValue()) {
                this.placeBlocks(Surround.mc.player.getPositionVector().add(0.0,  0.125,  0.0),  EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player,  -1,  false,  true),  false,  false,  true);
            }
        }
        else {
            this.isSafe = 2;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < this.extender.getValue()) {
            final Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (final Vec3d extendingBlock : this.extendingBlocks) {
                array[i] = extendingBlock;
                ++i;
            }
            final int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array),  EntityUtil.getUnsafeBlockArrayFromVec3d(Objects.requireNonNull(this.areClose(array)),  0,  this.floor.getValue(),  true),  this.helpingBlocks.getValue(),  false,  true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        }
        else if (this.extendingBlocks.size() > 2 || this.extenders >= this.extender.getValue()) {
            this.extendingBlocks.clear();
        }
    }
    
    private Vec3d areClose(final Vec3d[] vec3ds) {
        int matches = 0;
        for (final Vec3d vec3d : vec3ds) {
            for (final Vec3d pos : EntityUtil.getUnsafeBlockArray((Entity)Surround.mc.player,  0,  this.floor.getValue(),  true)) {
                if (vec3d.equals((Object)pos)) {
                    ++matches;
                }
            }
        }
        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1])).add(0.0,  0.13,  0.0);
        }
        return null;
    }
    
    private boolean placeBlocks(final Vec3d pos,  final Vec3d[] vec3ds,  final boolean hasHelpingBlocks,  final boolean isHelping,  final boolean isExtending) {
        int helpings = 0;
        for (final Vec3d vec3d : vec3ds) {
            boolean gotHelp = true;
            if (isHelping && !this.intelligent.getValue() && ++helpings > 1) {
                return false;
            }
            final BlockPos position = new BlockPos(pos).add(vec3d.x,  vec3d.y,  vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position,  this.raytrace.getValue())) {
                case 1: {
                    if ((this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && BlockTweaks.getINSTANCE().noBlock.getValue())) && (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue())) {
                        this.placeBlock(position);
                        this.retries.put(position,  (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                        this.retryTimer.reset();
                        break;
                    }
                    if ((!this.extendMove.getValue() && Phobos.speedManager.getSpeedKpH() != 0.0) || isExtending) {
                        break;
                    }
                    if (this.extenders >= this.extender.getValue()) {
                        break;
                    }
                    this.placeBlocks(Surround.mc.player.getPositionVector().add(0.0,  0.125,  0.0).add(vec3d),  EntityUtil.getUnsafeBlockArrayFromVec3d(Surround.mc.player.getPositionVector().add(0.0,  0.125,  0.0).add(vec3d),  0,  this.floor.getValue(),  true),  hasHelpingBlocks,  false,  true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    break;
                }
                case 2: {
                    if (!hasHelpingBlocks) {
                        break;
                    }
                    gotHelp = this.placeBlocks(pos,  BlockUtil.getHelpingBlocks(vec3d),  false,  true,  true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (!isHelping) {
                        break;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean check() {
        if (fullNullCheck() || this.shouldServer()) {
            return true;
        }
        if (this.endPortals.getValue()) {
            if (!(this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(),  BlockEndPortalFrame.class))) {
                this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(),  BlockObsidian.class);
            }
        }
        else {
            this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(),  BlockObsidian.class);
        }
        Surround.isPlacing = false;
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        if (this.endPortals.getValue()) {
            this.obbySlot = InventoryUtil.findHotbarBlock(BlockEndPortalFrame.class);
            if (this.obbySlot == -1) {
                this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            }
        }
        else {
            this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        }
        final int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.isOff()) {
            return true;
        }
        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        this.switchItem(true);
        if (this.obbySlot == -1 && !this.offHand && (!this.echests.getValue() || echestSlot == -1)) {
            if (this.info.getValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> §cYou are out of Obsidian.");
            }
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (Surround.mc.player.inventory.currentItem != this.lastHotbarSlot && Surround.mc.player.inventory.currentItem != this.obbySlot && Surround.mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        }
        switch (this.movementMode.getValue()) {
            case STATIC: {
                final BlockPos pos = new BlockPos(MathUtil.round(Surround.mc.player.posX,  0),  Math.ceil(Surround.mc.player.posY),  MathUtil.round(Surround.mc.player.posZ,  0));
                if (!this.startPos.equals((Object)pos)) {
                    this.disable();
                    return true;
                }
            }
            case LIMIT: {
                if (Phobos.speedManager.getSpeedKpH() <= this.speed.getValue()) {
                    break;
                }
                return true;
            }
            case OFF: {
                if (Phobos.speedManager.getSpeedKpH() <= this.speed.getValue()) {
                    break;
                }
                this.disable();
                return true;
            }
        }
        return Phobos.moduleManager.isModuleEnabled("Freecam") || !this.timer.passedMs(this.delay.getValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && Surround.mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockObsidian.class));
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue() && this.switchItem(false)) {
            Surround.isPlacing = true;
            this.isSneaking = BlockUtil.placeBlock(pos,  this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  this.rotate.getValue(),  this.noGhost.getValue(),  this.isSneaking);
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    private boolean switchItem(final boolean back) {
        if (this.offHand) {
            return true;
        }
        final boolean[] value = InventoryUtil.switchItem(back,  this.lastHotbarSlot,  this.switchedItem,  this.switchMode.getValue(),  (Class)((this.obbySlot == -1) ? BlockEnderChest.class : ((this.endPortals.getValue() && InventoryUtil.findHotbarBlock(BlockEndPortalFrame.class) != -1) ? BlockEndPortalFrame.class : BlockObsidian.class)));
        this.switchedItem = value[0];
        return value[1];
    }
    
    private List<BlockPos> fuckYou3arthqu4keYourCodeIsGarbage() {
        if (this.floor.getValue()) {
            return Arrays.asList(new BlockPos(Surround.mc.player.getPositionVector()).add(0.0,  -0.875,  0.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(1.0,  0.125,  0.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(-1.0,  0.125,  0.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(0.0,  0.125,  -1.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(0.0,  0.125,  1.0));
        }
        return Arrays.asList(new BlockPos(Surround.mc.player.getPositionVector()).add(1.0,  0.125,  0.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(-1.0,  0.125,  0.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(0.0,  0.125,  -1.0),  new BlockPos(Surround.mc.player.getPositionVector()).add(0.0,  0.125,  1.0));
    }
    
    public enum MovementMode
    {
        NONE,  
        STATIC,  
        LIMIT,  
        OFF;
    }
}
