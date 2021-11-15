



package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.gameevent.*;
import org.lwjgl.input.*;
import me.earth.phobos.features.gui.*;
import me.earth.phobos.event.events.*;
import me.earth.phobos.features.command.*;
import me.earth.phobos.features.modules.player.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.util.*;
import me.earth.phobos.util.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.item.*;

public class Auto32k extends Module
{
    private static Auto32k instance;
    private final Setting<Integer> delay;
    private final Setting<Float> range;
    private final Setting<Boolean> raytrace;
    private final Setting<Boolean> rotate;
    private final Setting<Double> targetRange;
    private final Setting<Boolean> extra;
    private final Setting<PlaceType> placeType;
    private final Setting<Boolean> freecam;
    private final Setting<Boolean> onOtherHoppers;
    private final Setting<Boolean> checkForShulker;
    private final Setting<Integer> checkDelay;
    private final Setting<Boolean> drop;
    private final Setting<Boolean> mine;
    private final Setting<Boolean> checkStatus;
    private final Setting<Boolean> packet;
    private final Setting<Boolean> superPacket;
    private final Setting<Boolean> secretClose;
    private final Setting<Boolean> closeGui;
    private final Setting<Boolean> repeatSwitch;
    private final Setting<Float> hopperDistance;
    private final Setting<Integer> trashSlot;
    private final Setting<Boolean> messages;
    private final Setting<Boolean> antiHopper;
    private final TimerUtil placeTimer;
    private final TimerUtil disableTimer;
    public Setting<Mode> mode;
    private final Setting<Integer> delayDispenser;
    private final Setting<Integer> blocksPerPlace;
    private final Setting<Boolean> preferObby;
    private final Setting<Boolean> simulate;
    public Setting<Boolean> autoSwitch;
    public Setting<Boolean> withBind;
    public Setting<Bind> switchBind;
    public boolean switching;
    public Step currentStep;
    private float yaw;
    private float pitch;
    private boolean spoof;
    private int shulkerSlot;
    private int hopperSlot;
    private BlockPos hopperPos;
    private EntityPlayer target;
    private int obbySlot;
    private int dispenserSlot;
    private int redstoneSlot;
    private DispenserData finalDispenserData;
    private int actionsThisTick;
    private boolean checkedThisTick;
    private boolean shouldDisable;
    
    public Auto32k() {
        super("Auto32k",  "Auto32ks.",  Category.COMBAT,  true,  false,  false);
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", 25, 0, 250));
        this.range = (Setting<Float>)this.register(new Setting("PlaceRange", 4.5f, 0.0f, 6.0f));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", false));
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", false));
        this.targetRange = (Setting<Double>)this.register(new Setting("TargetRange", 6.0, 0.0, 20.0));
        this.extra = (Setting<Boolean>)this.register(new Setting("ExtraRotation", false));
        this.placeType = (Setting<PlaceType>)this.register(new Setting("Place", PlaceType.CLOSE));
        this.freecam = (Setting<Boolean>)this.register(new Setting("Freecam", false));
        this.onOtherHoppers = (Setting<Boolean>)this.register(new Setting("UseHoppers", false));
        this.checkForShulker = (Setting<Boolean>)this.register(new Setting("CheckShulker", true));
        this.checkDelay = (Setting<Integer>)this.register(new Setting("CheckDelay", 500, 0, 500,  v -> this.checkForShulker.getValue()));
        this.drop = (Setting<Boolean>)this.register(new Setting("Drop", false));
        this.mine = (Setting<Boolean>)this.register(new Setting("Mine", false,  v -> this.drop.getValue()));
        this.checkStatus = (Setting<Boolean>)this.register(new Setting("CheckState", true));
        this.packet = (Setting<Boolean>)this.register(new Setting("Packet", false));
        this.superPacket = (Setting<Boolean>)this.register(new Setting("DispExtra", false));
        this.secretClose = (Setting<Boolean>)this.register(new Setting("SecretClose", false));
        this.closeGui = (Setting<Boolean>)this.register(new Setting("CloseGui", false,  v -> this.secretClose.getValue()));
        this.repeatSwitch = (Setting<Boolean>)this.register(new Setting("SwitchOnFail", true));
        this.hopperDistance = (Setting<Float>)this.register(new Setting("HopperRange", 8.0f, 0.0f, 20.0f));
        this.trashSlot = (Setting<Integer>)this.register(new Setting("32kSlot", 0, 0, 9));
        this.messages = (Setting<Boolean>)this.register(new Setting("Messages", false));
        this.antiHopper = (Setting<Boolean>)this.register(new Setting("AntiHopper", false));
        this.placeTimer = new TimerUtil();
        this.disableTimer = new TimerUtil();
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.NORMAL));
        this.delayDispenser = (Setting<Integer>)this.register(new Setting("Blocks/Place", 1, 1, 8,  v -> this.mode.getValue() != Mode.NORMAL));
        this.blocksPerPlace = (Setting<Integer>)this.register(new Setting("Actions/Place", 1, 1, 3,  v -> this.mode.getValue() == Mode.NORMAL));
        this.preferObby = (Setting<Boolean>)this.register(new Setting("UseObby", false,  v -> this.mode.getValue() != Mode.NORMAL));
        this.simulate = (Setting<Boolean>)this.register(new Setting("Simulate", true,  v -> this.mode.getValue() != Mode.NORMAL));
        this.autoSwitch = (Setting<Boolean>)this.register(new Setting("AutoSwitch", false,  v -> this.mode.getValue() == Mode.NORMAL));
        this.withBind = (Setting<Boolean>)this.register(new Setting("WithBind", false,  v -> this.mode.getValue() == Mode.NORMAL && this.autoSwitch.getValue()));
        this.switchBind = (Setting<Bind>)this.register(new Setting("SwitchBind", new Bind(-1),  v -> this.autoSwitch.getValue() && this.mode.getValue() == Mode.NORMAL && this.withBind.getValue()));
        this.currentStep = Step.PRE;
        this.shulkerSlot = -1;
        this.hopperSlot = -1;
        this.obbySlot = -1;
        this.dispenserSlot = -1;
        this.redstoneSlot = -1;
        Auto32k.instance = this;
    }
    
    public static Auto32k getInstance() {
        if (Auto32k.instance == null) {
            Auto32k.instance = new Auto32k();
        }
        return Auto32k.instance;
    }
    
    @Override
    public void onEnable() {
        this.checkedThisTick = false;
        this.resetFields();
        if (Auto32k.mc.currentScreen instanceof GuiHopper) {
            this.currentStep = Step.HOPPERGUI;
        }
        if (this.mode.getValue() == Mode.NORMAL && this.autoSwitch.getValue() && !this.withBind.getValue()) {
            this.switching = true;
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.shouldDisable && this.disableTimer.passedMs(1000L)) {
            this.shouldDisable = false;
            this.disable();
            return;
        }
        this.checkedThisTick = false;
        this.actionsThisTick = 0;
        if (this.isOff() || (this.mode.getValue() == Mode.NORMAL && this.autoSwitch.getValue() && !this.switching)) {
            return;
        }
        if (this.mode.getValue() == Mode.NORMAL) {
            this.normal32k();
        }
        else {
            this.processDispenser32k();
        }
    }
    
    @SubscribeEvent
    public void onGui(final GuiOpenEvent event) {
        if (fullNullCheck() || this.isOff()) {
            return;
        }
        if (!this.secretClose.getValue() && Auto32k.mc.currentScreen instanceof GuiHopper) {
            if (this.drop.getValue() && Auto32k.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && this.hopperPos != null) {
                Auto32k.mc.player.dropItem(true);
                final int pickaxeSlot;
                if (this.mine.getValue() && this.hopperPos != null && (pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class)) != -1) {
                    InventoryUtil.switchToHotbarSlot(pickaxeSlot,  false);
                    if (this.rotate.getValue()) {
                        this.rotateToPos(this.hopperPos.up(),  null);
                    }
                    Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(),  Auto32k.mc.player.getHorizontalFacing());
                    Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(),  Auto32k.mc.player.getHorizontalFacing());
                    Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
            this.resetFields();
            if (this.mode.getValue() != Mode.NORMAL) {
                this.disable();
                return;
            }
            if (!this.autoSwitch.getValue() || this.mode.getValue() == Mode.DISPENSER) {
                this.disable();
            }
            else if (!this.withBind.getValue()) {
                this.disable();
            }
        }
        else if (event.getGui() instanceof GuiHopper) {
            this.currentStep = Step.HOPPERGUI;
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "§aSwitch";
        }
        return null;
    }
    
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (this.isOff()) {
            return;
        }
        if (Keyboard.getEventKeyState() && !(Auto32k.mc.currentScreen instanceof PhobosGui) && this.switchBind.getValue().getKey() == Keyboard.getEventKey() && this.withBind.getValue()) {
            if (this.switching) {
                this.resetFields();
                this.switching = true;
            }
            this.switching = !this.switching;
        }
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        final Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this) && setting.equals(this.mode)) {
            this.resetFields();
        }
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (fullNullCheck() || this.isOff()) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            if (this.spoof) {
                final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                packet.yaw = this.yaw;
                packet.pitch = this.pitch;
                this.spoof = false;
            }
        }
        else if (event.getPacket() instanceof CPacketCloseWindow) {
            if (!this.secretClose.getValue() && Auto32k.mc.currentScreen instanceof GuiHopper && this.hopperPos != null) {
                if (this.drop.getValue() && Auto32k.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    Auto32k.mc.player.dropItem(true);
                    final int pickaxeSlot;
                    if (this.mine.getValue() && (pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class)) != -1) {
                        InventoryUtil.switchToHotbarSlot(pickaxeSlot,  false);
                        if (this.rotate.getValue()) {
                            this.rotateToPos(this.hopperPos.up(),  null);
                        }
                        Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(),  Auto32k.mc.player.getHorizontalFacing());
                        Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(),  Auto32k.mc.player.getHorizontalFacing());
                        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }
                this.resetFields();
                if (!this.autoSwitch.getValue() || this.mode.getValue() == Mode.DISPENSER) {
                    this.disable();
                }
                else if (!this.withBind.getValue()) {
                    this.disable();
                }
            }
            else if (this.secretClose.getValue() && (!this.autoSwitch.getValue() || this.switching || this.mode.getValue() == Mode.DISPENSER) && this.currentStep == Step.HOPPERGUI) {
                event.setCanceled(true);
            }
        }
    }
    
    private void normal32k() {
        if (this.autoSwitch.getValue()) {
            if (this.switching) {
                this.processNormal32k();
            }
            else {
                this.resetFields();
            }
        }
        else {
            this.processNormal32k();
        }
    }
    
    private void processNormal32k() {
        if (this.isOff()) {
            return;
        }
        if (this.placeTimer.passedMs(this.delay.getValue())) {
            this.check();
            Label_0184: {
                switch (this.currentStep) {
                    case PRE: {
                        this.runPreStep();
                        if (this.currentStep == Step.PRE) {
                            break;
                        }
                    }
                    case HOPPER: {
                        if (this.currentStep != Step.HOPPER) {
                            break Label_0184;
                        }
                        this.checkState();
                        if (this.currentStep == Step.PRE) {
                            if (this.checkedThisTick) {
                                this.processNormal32k();
                            }
                            return;
                        }
                        this.runHopperStep();
                        if (this.actionsThisTick >= this.blocksPerPlace.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) {
                            break;
                        }
                        break Label_0184;
                    }
                    case SHULKER: {
                        this.checkState();
                        if (this.currentStep == Step.PRE) {
                            if (this.checkedThisTick) {
                                this.processNormal32k();
                            }
                            return;
                        }
                        this.runShulkerStep();
                        if (this.actionsThisTick >= this.blocksPerPlace.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) {
                            break;
                        }
                    }
                    case CLICKHOPPER: {
                        this.checkState();
                        if (this.currentStep == Step.PRE) {
                            if (this.checkedThisTick) {
                                this.processNormal32k();
                            }
                            return;
                        }
                        this.runClickHopper();
                    }
                    case HOPPERGUI: {
                        this.runHopperGuiStep();
                        break;
                    }
                    default: {
                        Command.sendMessage("§cThis shouldn't happen,  report to 3arthqu4ke!!!");
                        Command.sendMessage("§cThis shouldn't happen,  report to 3arthqu4ke!!!");
                        Command.sendMessage("§cThis shouldn't happen,  report to 3arthqu4ke!!!");
                        Command.sendMessage("§cThis shouldn't happen,  report to 3arthqu4ke!!!");
                        Command.sendMessage("§cThis shouldn't happen,  report to 3arthqu4ke!!!");
                        this.currentStep = Step.PRE;
                        break;
                    }
                }
            }
        }
    }
    
    private void runPreStep() {
        if (this.isOff()) {
            return;
        }
        PlaceType type = this.placeType.getValue();
        if (Freecam.getInstance().isOn() && !this.freecam.getValue()) {
            if (this.messages.getValue()) {
                Command.sendMessage("§c<Auto32k> Disable Freecam.");
            }
            if (this.autoSwitch.getValue()) {
                this.resetFields();
                if (!this.withBind.getValue()) {
                    this.disable();
                }
            }
            else {
                this.disable();
            }
            return;
        }
        this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
        this.shulkerSlot = InventoryUtil.findHotbarBlock(BlockShulkerBox.class);
        if (Auto32k.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
            final Block block = ((ItemBlock)Auto32k.mc.player.getHeldItemOffhand().getItem()).getBlock();
            if (block instanceof BlockShulkerBox) {
                this.shulkerSlot = -2;
            }
            else if (block instanceof BlockHopper) {
                this.hopperSlot = -2;
            }
        }
        if (this.shulkerSlot == -1 || this.hopperSlot == -1) {
            if (this.messages.getValue()) {
                Command.sendMessage("§c<Auto32k> Materials not found.");
            }
            if (this.autoSwitch.getValue()) {
                this.resetFields();
                if (!this.withBind.getValue()) {
                    this.disable();
                }
            }
            else {
                this.disable();
            }
            return;
        }
        this.target = EntityUtil.getClosestEnemy(this.targetRange.getValue());
        if (this.target == null) {
            if (this.autoSwitch.getValue()) {
                if (this.switching) {
                    this.resetFields();
                    this.switching = true;
                }
                else {
                    this.resetFields();
                }
                return;
            }
            type = ((this.placeType.getValue() == PlaceType.MOUSE) ? PlaceType.MOUSE : PlaceType.CLOSE);
        }
        this.hopperPos = this.findBestPos(type,  this.target);
        if (this.hopperPos != null) {
            this.currentStep = ((Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper) ? Step.SHULKER : Step.HOPPER);
        }
        else {
            if (this.messages.getValue()) {
                Command.sendMessage("§c<Auto32k> Block not found.");
            }
            if (this.autoSwitch.getValue()) {
                this.resetFields();
                if (!this.withBind.getValue()) {
                    this.disable();
                }
            }
            else {
                this.disable();
            }
        }
    }
    
    private void runHopperStep() {
        if (this.isOff()) {
            return;
        }
        if (this.currentStep == Step.HOPPER) {
            this.runPlaceStep(this.hopperPos,  this.hopperSlot);
            this.currentStep = Step.SHULKER;
        }
    }
    
    private void runShulkerStep() {
        if (this.isOff()) {
            return;
        }
        if (this.currentStep == Step.SHULKER) {
            this.runPlaceStep(this.hopperPos.up(),  this.shulkerSlot);
            this.currentStep = Step.CLICKHOPPER;
        }
    }
    
    private void runClickHopper() {
        if (this.isOff()) {
            return;
        }
        if (this.currentStep != Step.CLICKHOPPER) {
            return;
        }
        if (this.mode.getValue() == Mode.NORMAL && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) && this.checkForShulker.getValue()) {
            if (this.placeTimer.passedMs(this.checkDelay.getValue())) {
                this.currentStep = Step.SHULKER;
            }
            return;
        }
        this.clickBlock(this.hopperPos);
        this.currentStep = Step.HOPPERGUI;
    }
    
    private void runHopperGuiStep() {
        if (this.isOff()) {
            return;
        }
        if (this.currentStep != Step.HOPPERGUI) {
            return;
        }
        if (Auto32k.mc.player.openContainer instanceof ContainerHopper) {
            if (!EntityUtil.holding32k((EntityPlayer)Auto32k.mc.player)) {
                int swordIndex = -1;
                for (int i = 0; i < 5; ++i) {
                    if (EntityUtil.is32k(Auto32k.mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i))) {
                        swordIndex = i;
                        break;
                    }
                }
                if (swordIndex == -1) {
                    return;
                }
                if (this.trashSlot.getValue() != 0) {
                    InventoryUtil.switchToHotbarSlot(this.trashSlot.getValue() - 1,  false);
                }
                else if (this.mode.getValue() != Mode.NORMAL && this.shulkerSlot > 35 && this.shulkerSlot != 45) {
                    InventoryUtil.switchToHotbarSlot(44 - this.shulkerSlot,  false);
                }
                Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId,  swordIndex,  (this.trashSlot.getValue() == 0) ? Auto32k.mc.player.inventory.currentItem : (this.trashSlot.getValue() - 1),  ClickType.SWAP,  (EntityPlayer)Auto32k.mc.player);
            }
            else if (this.closeGui.getValue() && this.secretClose.getValue()) {
                Auto32k.mc.player.closeScreen();
            }
        }
        else if (EntityUtil.holding32k((EntityPlayer)Auto32k.mc.player)) {
            if (this.autoSwitch.getValue() && this.mode.getValue() == Mode.NORMAL) {
                this.switching = false;
            }
            else if (!this.autoSwitch.getValue() || this.mode.getValue() == Mode.DISPENSER) {
                this.shouldDisable = true;
                this.disableTimer.reset();
            }
        }
    }
    
    private void runPlaceStep(final BlockPos pos,  final int slot) {
        if (this.isOff()) {
            return;
        }
        EnumFacing side = EnumFacing.UP;
        if (this.antiHopper.getValue() && this.currentStep == Step.HOPPER) {
            boolean foundfacing = false;
            for (final EnumFacing facing : EnumFacing.values()) {
                if (Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() != Blocks.HOPPER && !Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                    foundfacing = true;
                    side = facing;
                    break;
                }
            }
            if (!foundfacing) {
                this.resetFields();
                return;
            }
        }
        else {
            side = BlockUtil.getFirstFacing(pos);
            if (side == null) {
                this.resetFields();
                return;
            }
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5,  0.5,  0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Auto32k.mc.world.getBlockState(neighbour).getBlock();
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.START_SNEAKING));
        if (this.rotate.getValue()) {
            if (this.blocksPerPlace.getValue() > 1) {
                final float[] angle = RotationUtil.getLegitRotations(hitVec);
                if (this.extra.getValue()) {
                    RotationUtil.faceYawAndPitch(angle[0],  angle[1]);
                }
            }
            else {
                this.rotateToPos(null,  hitVec);
            }
        }
        InventoryUtil.switchToHotbarSlot(slot,  false);
        BlockUtil.rightClickBlock(neighbour,  hitVec,  (slot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  opposite,  this.packet.getValue());
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.STOP_SNEAKING));
        this.placeTimer.reset();
        ++this.actionsThisTick;
    }
    
    private BlockPos findBestPos(final PlaceType type,  final EntityPlayer target) {
        BlockPos pos3 = null;
        final NonNullList<BlockPos> positions = (NonNullList<BlockPos>)NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)Auto32k.mc.player),  this.range.getValue(),  this.range.getValue().intValue(),  false,  true,  0).stream().filter((Predicate<? super Object>)this::canPlace).collect((Collector<? super Object,  ?,  List<? super Object>>)Collectors.toList()));
        if (positions.isEmpty()) {
            return null;
        }
        switch (type) {
            case MOUSE: {
                if (Auto32k.mc.objectMouseOver != null && Auto32k.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    final BlockPos mousePos = Auto32k.mc.objectMouseOver.getBlockPos();
                    if (!this.canPlace(mousePos)) {
                        final BlockPos mousePosUp = mousePos.up();
                        if (this.canPlace(mousePosUp)) {
                            pos3 = mousePosUp;
                        }
                    }
                    else {
                        pos3 = mousePos;
                    }
                }
                if (pos3 != null) {
                    break;
                }
            }
            case CLOSE: {
                positions.sort((Comparator)Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                pos3 = (BlockPos)positions.get(0);
                break;
            }
            case ENEMY: {
                positions.sort((Comparator)Comparator.comparingDouble((ToDoubleFunction<? super Object>)target::getDistanceSq));
                pos3 = (BlockPos)positions.get(0);
                break;
            }
            case MIDDLE: {
                final ArrayList<BlockPos> toRemove = new ArrayList<BlockPos>();
                final NonNullList<BlockPos> copy = (NonNullList<BlockPos>)NonNullList.create();
                copy.addAll((Collection)positions);
                for (final BlockPos position : copy) {
                    final double difference = Auto32k.mc.player.getDistanceSq(position) - target.getDistanceSq(position);
                    if (difference <= 1.0 && difference >= -1.0) {
                        continue;
                    }
                    toRemove.add(position);
                }
                copy.removeAll((Collection)toRemove);
                if (copy.isEmpty()) {
                    copy.addAll((Collection)positions);
                }
                copy.sort((Comparator)Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                pos3 = (BlockPos)copy.get(0);
                break;
            }
            case FAR: {
                positions.sort((Comparator)Comparator.comparingDouble(pos2 -> -target.getDistanceSq(pos2)));
                pos3 = (BlockPos)positions.get(0);
                break;
            }
            case SAFE: {
                positions.sort((Comparator)Comparator.comparingInt(pos2 -> -this.safetyFactor(pos2)));
                pos3 = (BlockPos)positions.get(0);
                break;
            }
        }
        return pos3;
    }
    
    private boolean canPlace(final BlockPos pos) {
        if (pos == null) {
            return false;
        }
        final BlockPos boost = pos.up();
        return !this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos).getBlock(),  this.onOtherHoppers.getValue()) && !this.isGoodMaterial(Auto32k.mc.world.getBlockState(boost).getBlock(),  false) && (!this.raytrace.getValue() || (BlockUtil.rayTracePlaceCheck(pos,  this.raytrace.getValue()) && BlockUtil.rayTracePlaceCheck(pos,  this.raytrace.getValue()))) && !this.badEntities(pos) && !this.badEntities(boost) && ((this.onOtherHoppers.getValue() && Auto32k.mc.world.getBlockState(pos).getBlock() instanceof BlockHopper) || this.findFacing(pos));
    }
    
    private void check() {
        if (this.currentStep != Step.PRE && this.currentStep != Step.HOPPER && this.hopperPos != null && !(Auto32k.mc.currentScreen instanceof GuiHopper) && !EntityUtil.holding32k((EntityPlayer)Auto32k.mc.player) && (Auto32k.mc.player.getDistanceSq(this.hopperPos) > MathUtil.square(this.hopperDistance.getValue()) || Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() != Blocks.HOPPER)) {
            this.resetFields();
            if (!this.autoSwitch.getValue() || !this.withBind.getValue() || this.mode.getValue() != Mode.NORMAL) {
                this.disable();
            }
        }
    }
    
    private void checkState() {
        if (!this.checkStatus.getValue() || this.checkedThisTick || (this.currentStep != Step.HOPPER && this.currentStep != Step.SHULKER && this.currentStep != Step.CLICKHOPPER)) {
            this.checkedThisTick = false;
            return;
        }
        if (this.hopperPos == null || this.isGoodMaterial(Auto32k.mc.world.getBlockState(this.hopperPos).getBlock(),  true) || (this.isGoodMaterial(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock(),  false) && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox)) || this.badEntities(this.hopperPos) || this.badEntities(this.hopperPos.up())) {
            if (this.autoSwitch.getValue() && this.mode.getValue() == Mode.NORMAL) {
                if (this.switching) {
                    this.resetFields();
                    if (this.repeatSwitch.getValue()) {
                        this.switching = true;
                    }
                }
                else {
                    this.resetFields();
                }
                if (!this.withBind.getValue()) {
                    this.disable();
                }
            }
            else {
                this.disable();
            }
            this.checkedThisTick = true;
        }
    }
    
    private void processDispenser32k() {
        if (this.isOff()) {
            return;
        }
        if (this.placeTimer.passedMs(this.delay.getValue())) {
            this.check();
            switch (this.currentStep) {
                case PRE: {
                    this.runDispenserPreStep();
                    if (this.currentStep == Step.PRE) {
                        break;
                    }
                }
                case HOPPER: {
                    this.runHopperStep();
                    this.currentStep = Step.DISPENSER;
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) {
                        break;
                    }
                }
                case DISPENSER: {
                    this.runDispenserStep();
                    final boolean quickCheck = !Auto32k.mc.world.getBlockState(this.finalDispenserData.getHelpingPos()).getMaterial().isReplaceable();
                    if ((this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) || (this.currentStep != Step.DISPENSER_HELPING && this.currentStep != Step.CLICK_DISPENSER)) {
                        break;
                    }
                    if (this.rotate.getValue() && quickCheck) {
                        break;
                    }
                }
                case DISPENSER_HELPING: {
                    this.runDispenserStep();
                    if ((this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) || (this.currentStep != Step.CLICK_DISPENSER && this.currentStep != Step.DISPENSER_HELPING)) {
                        break;
                    }
                    if (this.rotate.getValue()) {
                        break;
                    }
                }
                case CLICK_DISPENSER: {
                    this.clickDispenser();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) {
                        break;
                    }
                }
                case DISPENSER_GUI: {
                    this.dispenserGui();
                    if (this.currentStep == Step.DISPENSER_GUI) {
                        break;
                    }
                }
                case REDSTONE: {
                    this.placeRedstone();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) {
                        break;
                    }
                }
                case CLICKHOPPER: {
                    this.runClickHopper();
                    if (this.actionsThisTick >= this.delayDispenser.getValue() && !this.placeTimer.passedMs(this.delay.getValue())) {
                        break;
                    }
                }
                case HOPPERGUI: {
                    this.runHopperGuiStep();
                    if (this.actionsThisTick < this.delayDispenser.getValue() || this.placeTimer.passedMs(this.delay.getValue())) {}
                    break;
                }
            }
        }
    }
    
    private void placeRedstone() {
        if (this.isOff()) {
            return;
        }
        if (this.badEntities(this.hopperPos.up()) && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox)) {
            return;
        }
        this.runPlaceStep(this.finalDispenserData.getRedStonePos(),  this.redstoneSlot);
        this.currentStep = Step.CLICKHOPPER;
    }
    
    private void clickDispenser() {
        if (this.isOff()) {
            return;
        }
        this.clickBlock(this.finalDispenserData.getDispenserPos());
        this.currentStep = Step.DISPENSER_GUI;
    }
    
    private void dispenserGui() {
        if (this.isOff()) {
            return;
        }
        if (!(Auto32k.mc.currentScreen instanceof GuiDispenser)) {
            return;
        }
        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId,  this.shulkerSlot,  0,  ClickType.QUICK_MOVE,  (EntityPlayer)Auto32k.mc.player);
        Auto32k.mc.player.closeScreen();
        this.currentStep = Step.REDSTONE;
    }
    
    private void clickBlock(final BlockPos pos) {
        if (this.isOff() || pos == null) {
            return;
        }
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.STOP_SNEAKING));
        final Vec3d hitVec = new Vec3d((Vec3i)pos).add(0.5,  -0.5,  0.5);
        if (this.rotate.getValue()) {
            this.rotateToPos(null,  hitVec);
        }
        EnumFacing facing = EnumFacing.UP;
        if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getDispenserPos().equals((Object)pos) && pos.getY() > new BlockPos(Auto32k.mc.player.getPositionVector()).up().getY()) {
            facing = EnumFacing.DOWN;
        }
        BlockUtil.rightClickBlock(pos,  hitVec,  (this.shulkerSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  facing,  this.packet.getValue());
        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
        Auto32k.mc.rightClickDelayTimer = 4;
        ++this.actionsThisTick;
    }
    
    private void runDispenserStep() {
        if (this.isOff()) {
            return;
        }
        if (this.finalDispenserData == null || this.finalDispenserData.getDispenserPos() == null || this.finalDispenserData.getHelpingPos() == null) {
            this.resetFields();
            return;
        }
        if (this.currentStep != Step.DISPENSER && this.currentStep != Step.DISPENSER_HELPING) {
            return;
        }
        final BlockPos dispenserPos = this.finalDispenserData.getDispenserPos();
        final BlockPos helpingPos = this.finalDispenserData.getHelpingPos();
        if (!Auto32k.mc.world.getBlockState(helpingPos).getMaterial().isReplaceable()) {
            this.placeDispenserAgainstBlock(dispenserPos,  helpingPos);
            ++this.actionsThisTick;
            this.currentStep = Step.CLICK_DISPENSER;
            return;
        }
        this.currentStep = Step.DISPENSER_HELPING;
        EnumFacing facing = EnumFacing.DOWN;
        boolean foundHelpingPos = false;
        for (final EnumFacing enumFacing : EnumFacing.values()) {
            final BlockPos position = helpingPos.offset(enumFacing);
            if (!position.equals((Object)this.hopperPos) && !position.equals((Object)this.hopperPos.up()) && !position.equals((Object)dispenserPos) && !position.equals((Object)this.finalDispenserData.getRedStonePos()) && Auto32k.mc.player.getDistanceSq(position) <= MathUtil.square(this.range.getValue()) && (!this.raytrace.getValue() || BlockUtil.rayTracePlaceCheck(position,  this.raytrace.getValue())) && !Auto32k.mc.world.getBlockState(position).getMaterial().isReplaceable()) {
                foundHelpingPos = true;
                facing = enumFacing;
                break;
            }
        }
        if (!foundHelpingPos) {
            this.disable();
            return;
        }
        final BlockPos neighbour = helpingPos.offset(facing);
        final EnumFacing opposite = facing.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5,  0.5,  0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Auto32k.mc.world.getBlockState(neighbour).getBlock();
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.START_SNEAKING));
        if (this.rotate.getValue()) {
            if (this.blocksPerPlace.getValue() > 1) {
                final float[] angle = RotationUtil.getLegitRotations(hitVec);
                if (this.extra.getValue()) {
                    RotationUtil.faceYawAndPitch(angle[0],  angle[1]);
                }
            }
            else {
                this.rotateToPos(null,  hitVec);
            }
        }
        final int slot = (this.preferObby.getValue() && this.obbySlot != -1) ? this.obbySlot : this.dispenserSlot;
        InventoryUtil.switchToHotbarSlot(slot,  false);
        BlockUtil.rightClickBlock(neighbour,  hitVec,  (slot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  opposite,  this.packet.getValue());
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.STOP_SNEAKING));
        this.placeTimer.reset();
        ++this.actionsThisTick;
    }
    
    private void placeDispenserAgainstBlock(final BlockPos dispenserPos,  final BlockPos helpingPos) {
        if (this.isOff()) {
            return;
        }
        EnumFacing facing = EnumFacing.DOWN;
        for (final EnumFacing enumFacing : EnumFacing.values()) {
            final BlockPos position = dispenserPos.offset(enumFacing);
            if (position.equals((Object)helpingPos)) {
                facing = enumFacing;
                break;
            }
        }
        final EnumFacing opposite = facing.getOpposite();
        final Vec3d hitVec = new Vec3d((Vec3i)helpingPos).add(0.5,  0.5,  0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Auto32k.mc.world.getBlockState(helpingPos).getBlock();
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.START_SNEAKING));
        EnumFacing facings = EnumFacing.UP;
        if (this.rotate.getValue()) {
            if (this.blocksPerPlace.getValue() > 1) {
                final float[] arrf = RotationUtil.getLegitRotations(hitVec);
                if (this.extra.getValue()) {
                    RotationUtil.faceYawAndPitch(arrf[0],  arrf[1]);
                }
            }
            else {
                this.rotateToPos(null,  hitVec);
            }
            new Vec3d((Vec3i)helpingPos).add(0.5,  0.5,  0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        }
        else if (dispenserPos.getY() <= new BlockPos(Auto32k.mc.player.getPositionVector()).up().getY()) {
            for (final EnumFacing enumFacing2 : EnumFacing.values()) {
                final BlockPos position2 = this.hopperPos.up().offset(enumFacing2);
                if (position2.equals((Object)dispenserPos)) {
                    facings = enumFacing2;
                    break;
                }
            }
            final float[] arrf = RotationUtil.simpleFacing(facings);
            this.yaw = arrf[0];
            this.pitch = arrf[1];
            this.spoof = true;
        }
        else {
            final float[] arrf = RotationUtil.simpleFacing(facings);
            this.yaw = arrf[0];
            this.pitch = arrf[1];
            this.spoof = true;
        }
        final Vec3d rotationVec = new Vec3d((Vec3i)helpingPos).add(0.5,  0.5,  0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final float[] arrf = RotationUtil.simpleFacing(facings);
        final float[] angle = RotationUtil.getLegitRotations(hitVec);
        if (this.superPacket.getValue()) {
            RotationUtil.faceYawAndPitch(((boolean)this.rotate.getValue()) ? angle[0] : arrf[0],  ((boolean)this.rotate.getValue()) ? angle[1] : arrf[1]);
        }
        InventoryUtil.switchToHotbarSlot(this.dispenserSlot,  false);
        BlockUtil.rightClickBlock(helpingPos,  rotationVec,  (this.dispenserSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  opposite,  this.packet.getValue());
        Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Auto32k.mc.player,  CPacketEntityAction.Action.STOP_SNEAKING));
        this.placeTimer.reset();
        ++this.actionsThisTick;
        this.currentStep = Step.CLICK_DISPENSER;
    }
    
    private void runDispenserPreStep() {
        if (this.isOff()) {
            return;
        }
        if (Freecam.getInstance().isOn() && !this.freecam.getValue()) {
            if (this.messages.getValue()) {
                Command.sendMessage("§c<Auto32k> Disable Freecam.");
            }
            this.disable();
            return;
        }
        this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
        this.shulkerSlot = InventoryUtil.findBlockSlotInventory(BlockShulkerBox.class,  false,  false);
        this.dispenserSlot = InventoryUtil.findHotbarBlock(BlockDispenser.class);
        this.redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (Auto32k.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
            final Block block = ((ItemBlock)Auto32k.mc.player.getHeldItemOffhand().getItem()).getBlock();
            if (block instanceof BlockHopper) {
                this.hopperSlot = -2;
            }
            else if (block instanceof BlockDispenser) {
                this.dispenserSlot = -2;
            }
            else if (block == Blocks.REDSTONE_BLOCK) {
                this.redstoneSlot = -2;
            }
            else if (block instanceof BlockObsidian) {
                this.obbySlot = -2;
            }
        }
        if (this.shulkerSlot == -1 || this.hopperSlot == -1 || this.dispenserSlot == -1 || this.redstoneSlot == -1) {
            if (this.messages.getValue()) {
                Command.sendMessage("§c<Auto32k> Materials not found.");
            }
            this.disable();
            return;
        }
        this.finalDispenserData = this.findBestPos();
        if (this.finalDispenserData.isPlaceable()) {
            this.hopperPos = this.finalDispenserData.getHopperPos();
            this.currentStep = ((Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper) ? Step.DISPENSER : Step.HOPPER);
        }
        else {
            if (this.messages.getValue()) {
                Command.sendMessage("§c<Auto32k> Block not found.");
            }
            this.disable();
        }
    }
    
    private DispenserData findBestPos() {
        PlaceType type = this.placeType.getValue();
        this.target = EntityUtil.getClosestEnemy(this.targetRange.getValue());
        if (this.target == null) {
            type = ((this.placeType.getValue() == PlaceType.MOUSE) ? PlaceType.MOUSE : PlaceType.CLOSE);
        }
        final NonNullList<BlockPos> positions = (NonNullList<BlockPos>)NonNullList.create();
        positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)Auto32k.mc.player),  this.range.getValue(),  this.range.getValue().intValue(),  false,  true,  0));
        DispenserData data = new DispenserData();
        switch (type) {
            case MOUSE: {
                final BlockPos mousePos;
                if (Auto32k.mc.objectMouseOver != null && Auto32k.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && (mousePos = Auto32k.mc.objectMouseOver.getBlockPos()) != null && !(data = this.analyzePos(mousePos)).isPlaceable()) {
                    data = this.analyzePos(mousePos.up());
                }
                if (data.isPlaceable()) {
                    return data;
                }
            }
            case CLOSE: {
                positions.sort((Comparator)Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                break;
            }
            case ENEMY: {
                positions.sort((Comparator)Comparator.comparingDouble((ToDoubleFunction<? super Object>)this.target::getDistanceSq));
                break;
            }
            case MIDDLE: {
                final ArrayList<BlockPos> toRemove = new ArrayList<BlockPos>();
                final NonNullList<BlockPos> copy = (NonNullList<BlockPos>)NonNullList.create();
                copy.addAll((Collection)positions);
                for (final BlockPos position : copy) {
                    final double difference = Auto32k.mc.player.getDistanceSq(position) - this.target.getDistanceSq(position);
                    if (difference <= 1.0 && difference >= -1.0) {
                        continue;
                    }
                    toRemove.add(position);
                }
                copy.removeAll((Collection)toRemove);
                if (copy.isEmpty()) {
                    copy.addAll((Collection)positions);
                }
                copy.sort((Comparator)Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
                break;
            }
            case FAR: {
                positions.sort((Comparator)Comparator.comparingDouble(pos2 -> -this.target.getDistanceSq(pos2)));
                break;
            }
            case SAFE: {
                positions.sort((Comparator)Comparator.comparingInt(pos2 -> -this.safetyFactor(pos2)));
                break;
            }
        }
        data = this.findData(positions);
        return data;
    }
    
    private DispenserData findData(final NonNullList<BlockPos> positions) {
        for (final BlockPos position : positions) {
            final DispenserData data = this.analyzePos(position);
            if (!data.isPlaceable()) {
                continue;
            }
            return data;
        }
        return new DispenserData();
    }
    
    private DispenserData analyzePos(final BlockPos pos) {
        final DispenserData data = new DispenserData(pos);
        if (pos == null) {
            return data;
        }
        if (this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos).getBlock(),  this.onOtherHoppers.getValue()) || this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos.up()).getBlock(),  false)) {
            return data;
        }
        if (this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(pos,  this.raytrace.getValue())) {
            return data;
        }
        if (this.badEntities(pos) || this.badEntities(pos.up())) {
            return data;
        }
        if (this.hasAdjancedRedstone(pos)) {
            return data;
        }
        if (!this.findFacing(pos)) {
            return data;
        }
        final BlockPos[] otherPositions = this.checkForDispenserPos(pos);
        if (otherPositions[0] == null || otherPositions[1] == null || otherPositions[2] == null) {
            return data;
        }
        data.setDispenserPos(otherPositions[0]);
        data.setRedStonePos(otherPositions[1]);
        data.setHelpingPos(otherPositions[2]);
        data.setPlaceable();
        return data;
    }
    
    private boolean findFacing(final BlockPos pos) {
        boolean foundFacing = false;
        for (final EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.UP) {
                if (facing == EnumFacing.DOWN && this.antiHopper.getValue() && Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) {
                    foundFacing = false;
                    break;
                }
                if (!Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                    if (!this.antiHopper.getValue() || Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() != Blocks.HOPPER) {
                        foundFacing = true;
                    }
                }
            }
        }
        return foundFacing;
    }
    
    private BlockPos[] checkForDispenserPos(final BlockPos posIn) {
        final BlockPos[] pos3 = new BlockPos[3];
        final BlockPos playerPos = new BlockPos(Auto32k.mc.player.getPositionVector());
        if (posIn.getY() < playerPos.down().getY()) {
            return pos3;
        }
        final List<BlockPos> possiblePositions = this.getDispenserPositions(posIn);
        if (posIn.getY() < playerPos.getY()) {
            possiblePositions.remove(posIn.up().up());
        }
        else if (posIn.getY() > playerPos.getY()) {
            possiblePositions.remove(posIn.west().up());
            possiblePositions.remove(posIn.north().up());
            possiblePositions.remove(posIn.south().up());
            possiblePositions.remove(posIn.east().up());
        }
        if (!this.rotate.getValue() && !this.simulate.getValue()) {
            possiblePositions.removeIf(position -> Auto32k.mc.player.getDistanceSq(position) > MathUtil.square(this.range.getValue()));
            possiblePositions.removeIf(position -> this.isGoodMaterial(Auto32k.mc.world.getBlockState(position).getBlock(),  false));
            possiblePositions.removeIf(position -> this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(position,  this.raytrace.getValue()));
            possiblePositions.removeIf(this::badEntities);
            possiblePositions.removeIf(this::hasAdjancedRedstone);
            for (final BlockPos position2 : possiblePositions) {
                final List<BlockPos> possibleRedStonePositions = this.checkRedStone(position2,  posIn);
                final BlockPos[] helpingStuff;
                if (!possiblePositions.isEmpty() && (helpingStuff = this.getHelpingPos(position2,  posIn,  possibleRedStonePositions)) != null && helpingStuff[0] != null) {
                    if (helpingStuff[1] == null) {
                        continue;
                    }
                    pos3[0] = position2;
                    pos3[1] = helpingStuff[1];
                    pos3[2] = helpingStuff[0];
                    break;
                }
            }
        }
        else {
            possiblePositions.sort(Comparator.comparingDouble(pos2 -> -Auto32k.mc.player.getDistanceSq(pos2)));
            final BlockPos posToCheck = possiblePositions.get(0);
            if (this.isGoodMaterial(Auto32k.mc.world.getBlockState(posToCheck).getBlock(),  false)) {
                return pos3;
            }
            if (Auto32k.mc.player.getDistanceSq(posToCheck) > MathUtil.square(this.range.getValue())) {
                return pos3;
            }
            if (this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(posToCheck,  this.raytrace.getValue())) {
                return pos3;
            }
            if (this.badEntities(posToCheck)) {
                return pos3;
            }
            if (this.hasAdjancedRedstone(posToCheck)) {
                return pos3;
            }
            final List<BlockPos> possibleRedStonePositions2 = this.checkRedStone(posToCheck,  posIn);
            if (possiblePositions.isEmpty()) {
                return pos3;
            }
            final BlockPos[] helpingStuff2 = this.getHelpingPos(posToCheck,  posIn,  possibleRedStonePositions2);
            if (helpingStuff2 != null && helpingStuff2[0] != null) {
                if (helpingStuff2[1] != null) {
                    pos3[0] = posToCheck;
                    pos3[1] = helpingStuff2[1];
                    pos3[2] = helpingStuff2[0];
                }
            }
        }
        return pos3;
    }
    
    private List<BlockPos> checkRedStone(final BlockPos pos,  final BlockPos hopperPos) {
        final ArrayList<BlockPos> toCheck = new ArrayList<BlockPos>();
        for (final EnumFacing facing : EnumFacing.values()) {
            toCheck.add(pos.offset(facing));
        }
        toCheck.removeIf(position -> position.equals((Object)hopperPos.up()));
        toCheck.removeIf(position -> Auto32k.mc.player.getDistanceSq(position) > MathUtil.square(this.range.getValue()));
        toCheck.removeIf(position -> this.isGoodMaterial(Auto32k.mc.world.getBlockState(position).getBlock(),  false));
        toCheck.removeIf(position -> this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(position,  this.raytrace.getValue()));
        toCheck.removeIf(this::badEntities);
        toCheck.sort(Comparator.comparingDouble(pos2 -> Auto32k.mc.player.getDistanceSq(pos2)));
        return toCheck;
    }
    
    private boolean hasAdjancedRedstone(final BlockPos pos) {
        for (final EnumFacing facing : EnumFacing.values()) {
            final BlockPos position = pos.offset(facing);
            if (Auto32k.mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_BLOCK || Auto32k.mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_TORCH) {
                return true;
            }
        }
        return false;
    }
    
    private List<BlockPos> getDispenserPositions(final BlockPos pos) {
        final ArrayList<BlockPos> list = new ArrayList<BlockPos>();
        for (final EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.DOWN) {
                list.add(pos.offset(facing).up());
            }
        }
        return list;
    }
    
    private BlockPos[] getHelpingPos(final BlockPos pos,  final BlockPos hopperPos,  final List<BlockPos> redStonePositions) {
        final BlockPos[] result = new BlockPos[2];
        final ArrayList<BlockPos> possiblePositions = new ArrayList<BlockPos>();
        if (redStonePositions.isEmpty()) {
            return null;
        }
        for (final EnumFacing facing : EnumFacing.values()) {
            final BlockPos facingPos = pos.offset(facing);
            if (!facingPos.equals((Object)hopperPos)) {
                if (!facingPos.equals((Object)hopperPos.up())) {
                    if (!Auto32k.mc.world.getBlockState(facingPos).getMaterial().isReplaceable()) {
                        if (!redStonePositions.contains(facingPos)) {
                            result[0] = facingPos;
                            result[1] = redStonePositions.get(0);
                            return result;
                        }
                        redStonePositions.remove(facingPos);
                        if (!redStonePositions.isEmpty()) {
                            result[0] = facingPos;
                            result[1] = redStonePositions.get(0);
                            return result;
                        }
                        redStonePositions.add(facingPos);
                    }
                    else {
                        for (final EnumFacing facing2 : EnumFacing.values()) {
                            final BlockPos facingPos2 = facingPos.offset(facing2);
                            if (!facingPos2.equals((Object)hopperPos) && !facingPos2.equals((Object)hopperPos.up()) && !facingPos2.equals((Object)pos)) {
                                if (!Auto32k.mc.world.getBlockState(facingPos2).getMaterial().isReplaceable()) {
                                    if (redStonePositions.contains(facingPos)) {
                                        redStonePositions.remove(facingPos);
                                        if (redStonePositions.isEmpty()) {
                                            redStonePositions.add(facingPos);
                                        }
                                        else {
                                            possiblePositions.add(facingPos);
                                        }
                                    }
                                    else {
                                        possiblePositions.add(facingPos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        possiblePositions.removeIf(position -> Auto32k.mc.player.getDistanceSq(position) > MathUtil.square(this.range.getValue()));
        possiblePositions.sort(Comparator.comparingDouble(position -> Auto32k.mc.player.getDistanceSq(position)));
        if (!possiblePositions.isEmpty()) {
            redStonePositions.remove(possiblePositions.get(0));
            if (!redStonePositions.isEmpty()) {
                result[0] = possiblePositions.get(0);
                result[1] = redStonePositions.get(0);
            }
            return result;
        }
        return null;
    }
    
    private void rotateToPos(final BlockPos pos,  final Vec3d vec3d) {
        final float[] angle = (vec3d == null) ? MathUtil.calcAngle(Auto32k.mc.player.getPositionEyes(Auto32k.mc.getRenderPartialTicks()),  new Vec3d((double)(pos.getX() + 0.5f),  (double)(pos.getY() - 0.5f),  (double)(pos.getZ() + 0.5f))) : RotationUtil.getLegitRotations(vec3d);
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.spoof = true;
    }
    
    private boolean isGoodMaterial(final Block block,  final boolean allowHopper) {
        return !(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow) && (!allowHopper || !(block instanceof BlockHopper));
    }
    
    private void resetFields() {
        this.shouldDisable = false;
        this.spoof = false;
        this.switching = false;
        this.shulkerSlot = -1;
        this.hopperSlot = -1;
        this.hopperPos = null;
        this.target = null;
        this.currentStep = Step.PRE;
        this.obbySlot = -1;
        this.dispenserSlot = -1;
        this.redstoneSlot = -1;
        this.finalDispenserData = null;
        this.actionsThisTick = 0;
    }
    
    private boolean badEntities(final BlockPos pos) {
        for (final Entity entity : Auto32k.mc.world.getEntitiesWithinAABB((Class)Entity.class,  new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityExpBottle) && !(entity instanceof EntityItem)) {
                if (entity instanceof EntityXPOrb) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    private int safetyFactor(final BlockPos pos) {
        return this.safety(pos) + this.safety(pos.up());
    }
    
    private int safety(final BlockPos pos) {
        int safety = 0;
        for (final EnumFacing facing : EnumFacing.values()) {
            if (!Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                ++safety;
            }
        }
        return safety;
    }
    
    public enum Step
    {
        PRE,  
        HOPPER,  
        SHULKER,  
        CLICKHOPPER,  
        HOPPERGUI,  
        DISPENSER_HELPING,  
        DISPENSER_GUI,  
        DISPENSER,  
        CLICK_DISPENSER,  
        REDSTONE;
    }
    
    public enum Mode
    {
        NORMAL,  
        DISPENSER;
    }
    
    public enum PlaceType
    {
        MOUSE,  
        CLOSE,  
        ENEMY,  
        MIDDLE,  
        FAR,  
        SAFE;
    }
    
    public static class DispenserData
    {
        private BlockPos dispenserPos;
        private BlockPos redStonePos;
        private BlockPos hopperPos;
        private BlockPos helpingPos;
        
        public DispenserData() {
        }
        
        public DispenserData(final BlockPos pos) {
            this.hopperPos = pos;
        }
        
        public boolean isPlaceable() {
            return this.dispenserPos != null && this.hopperPos != null && this.redStonePos != null && this.helpingPos != null;
        }
        
        public void setPlaceable() {
        }
        
        public BlockPos getDispenserPos() {
            return this.dispenserPos;
        }
        
        public void setDispenserPos(final BlockPos dispenserPos) {
            this.dispenserPos = dispenserPos;
        }
        
        public BlockPos getRedStonePos() {
            return this.redStonePos;
        }
        
        public void setRedStonePos(final BlockPos redStonePos) {
            this.redStonePos = redStonePos;
        }
        
        public BlockPos getHopperPos() {
            return this.hopperPos;
        }
        
        public BlockPos getHelpingPos() {
            return this.helpingPos;
        }
        
        public void setHelpingPos(final BlockPos helpingPos) {
            this.helpingPos = helpingPos;
        }
    }
}
