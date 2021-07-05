package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.block.*;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Hack.Registration(name = "Auto 32K", description = "places some blocks and pulls out a sword", category = Hack.Category.COMBAT, priority = HackPriority.High)
public class Auto32k extends Hack {
    //original module by TrvsF
    //redone by wallhacks
    private BlockPos pos;
    private int hopperSlot;
    private int redstoneSlot;
    private int shulkerSlot;
    private int ticksPast;
    private boolean failed;
    private int offsetStep;
    CPacketCloseWindow packet;
    private int[] rot;
    private boolean setup;
    private BlockPos hopperPos;
    private boolean placeRedstone;
    private boolean dispenserDone;
    EnumSetting placeMode = new EnumSetting("Mode", "Dispenser", Arrays.asList("Dispenser", "Looking", "Hopper"), this);
    IntSetting delay = new IntSetting("Delay", 4, 0, 10, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    BooleanSetting encase = new BooleanSetting("Encase", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    IntSetting slot = new IntSetting("Slot", 0, 0, 9, this);
    DoubleSetting hopperRange = new DoubleSetting("HopperRange", 6.0, 0.0, 10.0, this);
    ParentSetting parentSetting = new ParentSetting("Gui", this);
    BooleanSetting secretClose = new BooleanSetting("PacketClose", false, parentSetting);
    BooleanSetting closeGui = new BooleanSetting("CloseGui", false, parentSetting, v -> secretClose.getValue());
    ColourSetting color = new ColourSetting("RenderColor", new Colour(0, 0, 200, 200), this);

    @Override
    public void onEnable() {
        ticksPast = 0;
        setup = false;
        failed = true;
        packet = null;
        hopperPos = null;
        dispenserDone = false;
        placeRedstone = false;
        offsetStep = 0;
        hopperSlot = -1;
        int dispenserSlot = -1;
        redstoneSlot = -1;
        shulkerSlot = -1;
        int blockSlot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER)) hopperSlot = i;
            else if (item == Item.getItemFromBlock(Blocks.DISPENSER)) dispenserSlot = i;
            else if (item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) redstoneSlot = i;
            else if (item instanceof ItemShulkerBox) shulkerSlot = i;
            else if (item instanceof ItemBlock) blockSlot = i;
        }

        if ((hopperSlot == -1 || dispenserSlot == -1 || redstoneSlot == -1 || shulkerSlot == -1 || blockSlot == -1) && !placeMode.getValue().equals("Hopper")) {
            ClientMessage.sendErrorMessage("missing item");
            disable();
            return;
        } else if (hopperSlot == -1 || shulkerSlot == -1) {
            ClientMessage.sendErrorMessage("missing item");
            disable();
            return;
        }
        if (placeMode.getValue().equals("Looking")) {
            RayTraceResult r = mc.player.rayTrace(5.0D, mc.getRenderPartialTicks());
            pos = Objects.requireNonNull(r).getBlockPos().up();
            double pos_x = (double) pos.getX() - mc.player.posX;
            double pos_z = (double) pos.getZ() - mc.player.posZ;
            rot = Math.abs(pos_x) > Math.abs(pos_z) ? (pos_x > 0.0D ? new int[] {-1, 0} : new int[] {1, 0}) : (pos_z > 0.0D ? new int[] {0, -1} : new int[] {0, 1});

            if (BlockUtil.canPlaceBlock(pos) && BlockUtil.isBlockEmpty(pos) && BlockUtil.isBlockEmpty(pos.add(rot[0], 0, rot[1])) && BlockUtil.isBlockEmpty(pos.add(0, 1, 0)) && BlockUtil.isBlockEmpty(pos.add(0, 2, 0)) && BlockUtil.isBlockEmpty(pos.add(rot[0], 1, rot[1]))) {
                BlockUtil.placeBlock(pos, blockSlot, rotate.getValue(), false, swing);
                BlockUtil.rotatePacket((double) pos.add(-rot[0], 1, -rot[1]).getX() + 0.5D, pos.getY() + 1, (double) pos.add(-rot[0], 1, -rot[1]).getZ() + 0.5D);
                BlockUtil.placeBlock(pos.up(), dispenserSlot, rotate.getValue(), false, swing);
                BlockUtil.openBlock(pos.up());
                setup = true;
            } else {
                ClientMessage.sendErrorMessage("unable to place");
                disable();
            }
        } else if (placeMode.getValue().equals("Dispenser")) {
            for (int x = -2; x <= 2; ++x) {
                for (int y = -1; y <= 0; ++y) {
                    for (int z = -2; z <= 2; ++z) {
                        rot = Math.abs(x) > Math.abs(z) ? (x > 0 ? new int[] {-1, 0} : new int[] {1, 0}) : (z > 0 ? new int[] {0, -1} : new int[] {0, 1});
                        pos = mc.player.getPosition().add(x, y, z);
                        if (mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add(x - rot[0] / 2f, (double) y + 0.5D, z + rot[1] / 2)) <= 4.5D && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add((double) x + 0.5D, (double) y + 2.5D, (double) z + 0.5D)) <= 4.5D && BlockUtil.isBlockEmpty(pos.add(rot[0], 0, rot[1])) && !EntityUtil.isBothHole(pos.add(rot[0], 0, rot[1])) && BlockUtil.isBlockEmpty(pos.add(0, 1, 0)) && (BlockUtil.isBlockEmpty(pos.add(0, 2, 0)) || (BlockUtil.isBlockEmpty(pos.add(1, 1, 0)) && pos.add(1, 1, 0) != pos.add(rot[0], 1, rot[1])) || (BlockUtil.isBlockEmpty(pos.add(-1, 1, 0)) && pos.add(-1, 1, 0) != pos.add(rot[0], 1, rot[1])) || (BlockUtil.isBlockEmpty(pos.add(0, 1, 1)) && pos.add(0, 1, 1) != pos.add(rot[0], 1, rot[1])) || (BlockUtil.isBlockEmpty(pos.add(0, 1, -1)) && pos.add(0, 1, -1) != pos.add(rot[0], 1, rot[1]))) && BlockUtil.isBlockEmpty(pos.add(rot[0], 1, rot[1]))) {
                            if (BlockUtil.isBlockEmpty(pos) && !BlockUtil.canPlaceBlock(pos.up())) {
                                BlockUtil.placeBlock(pos, blockSlot, rotate.getValue(), false, swing);
                            }
                            BlockUtil.rotatePacket((double) pos.add(-rot[0], 1, -rot[1]).getX() + 0.5D, pos.getY() + 1, (double) pos.add(-rot[0], 1, -rot[1]).getZ() + 0.5D);
                            BlockUtil.placeBlock(pos.up(), dispenserSlot, rotate.getValue(), false, swing);
                            BlockUtil.openBlock(pos.up());
                            setup = true;
                            return;
                        }
                    }
                }
            }
            ClientMessage.sendErrorMessage("unable to place");
            disable();
        } else {
            for (int z = -2; z <= 2; z++) {
                for (int y = -1; y <= 2; y++) {
                    for (int x = -2; x <= 2; x++) {
                        if ((z != 0 || y != 0 || x != 0) && (z != 0 || y != 1 || x != 0) && BlockUtil.isBlockEmpty(mc.player.getPosition().add(z, y, x)) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add((double) z + 0.5D, (double) y + 0.5D, (double) x + 0.5D)) < 4.5D && BlockUtil.isBlockEmpty(mc.player.getPosition().add(z, y + 1, x)) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add((double) z + 0.5D, (double) y + 1.5D, (double) x + 0.5D)) < 4.5D)  {
                            hopperPos = mc.player.getPosition().add(z, y, x);
                            BlockUtil.placeBlock(mc.player.getPosition().add(z, y, x), hopperSlot, rotate.getValue(), false, swing);
                            BlockUtil.placeBlock(mc.player.getPosition().add(z, y + 1, x), shulkerSlot, rotate.getValue(), false, swing);
                            BlockUtil.openBlock(mc.player.getPosition().add(z, y, x));
                            pos = mc.player.getPosition().add(z, y, x);
                            dispenserDone = true;
                            placeRedstone = true;
                            setup = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (packet != null)
            mc.player.connection.sendPacket(new CPacketCloseWindow());
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (hopperPos != null) {
            RenderUtil.drawBoxESP(hopperPos, color.getColor(), color.getColor(), 2.0f, true, true, false);
            RenderUtil.drawCircle(hopperPos.getX(), hopperPos.getY(), hopperPos.getZ(), hopperRange.getValue().floatValue(), color.getValue());
        }
    }

    @Override
    public void onUpdate() {
        if (ticksPast > 50 && failed && !(mc.currentScreen instanceof GuiHopper)) {
            ClientMessage.sendErrorMessage("Failed disabling now");
            disable();
            return;
        }
        if (hopperPos != null) {
            if (mc.player.getDistanceSqToCenter(hopperPos) >= MathsUtil.square(hopperRange.getValue().floatValue())) {
                ClientMessage.sendErrorMessage("Out of range disabling..");
                disable();
                return;
            }
        }

        if ((!failed || mc.currentScreen instanceof GuiHopper) && encase.getValue()) {
            final List<Vec3d> place_targets = new ArrayList<Vec3d>();
            Collections.addAll(place_targets, offsetsHopper);
            if (offsetStep >= place_targets.size()) {
                offsetStep = 0;
            }
            boolean foundblock = false;
            while (!foundblock && offsetStep <= place_targets.size() - 1) {
                final BlockPos offset_pos = new BlockPos(place_targets.get(offsetStep));
                final BlockPos target_pos = new BlockPos(hopperPos.add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ()));
                if (mc.world.getBlockState(target_pos).getMaterial().isReplaceable()) {
                    foundblock = true;
                }
                for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
                    if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                        foundblock = false;
                    }
                }
                if (foundblock) {
                    BlockUtil.placeBlock(target_pos, InventoryUtil.findHotbarBlock(BlockObsidian.class), rotate.getValue(), rotate.getValue(), swing);
                }
                offsetStep++;
            }
        }

        if (setup && ticksPast > delay.getValue()) {
            if (!dispenserDone) { // ching chong
                try {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, 36 + shulkerSlot, 0, ClickType.QUICK_MOVE, mc.player);
                    dispenserDone = true;
                } catch (Exception ignored) {
                    //EmptyCatchBlockXDDDDDDD
                }
            }

            if (!placeRedstone) {
                BlockPos shulkerPos = pos.add(rot[0], 1, rot[1]);
                BlockPos redstonePos;
                if (BlockUtil.canPlaceBlock(pos.add(0, 2, 0))) {
                    redstonePos = pos.add(0, 2, 0);
                } else if (BlockUtil.canPlaceBlock(pos.add(1, 1, 0)) && pos.add(1, 1, 0) != shulkerPos) {
                    redstonePos = pos.add(1, 1, 0);
                } else if (BlockUtil.canPlaceBlock(pos.add(-1, 1, 0)) && pos.add(-1, 1, 0) != shulkerPos) {
                    redstonePos = pos.add(-1, 1, 0);
                } else if (BlockUtil.canPlaceBlock(pos.add(0, 1, 1)) && pos.add(0, 1, 1) != shulkerPos) {
                    redstonePos = pos.add(0, 1, 1);
                } else if (BlockUtil.canPlaceBlock(pos.add(0, 1, -1)) && pos.add(0, 1, -1) != shulkerPos) {
                    redstonePos = pos.add(0, 1, -1);
                } else {
                    ClientMessage.sendMessage("This shouldnt happen report this to wallhacks");
                    this.disable();
                    return;
                }
                BlockUtil.placeBlock(redstonePos, redstoneSlot, rotate.getValue(), false, swing);
                placeRedstone = true;
                return;
            }

            if (!placeMode.getValue().equals("Hopper") && mc.world.getBlockState(pos.add(rot[0], 1, rot[1])).getBlock() instanceof BlockShulkerBox
                    && mc.world.getBlockState(pos.add(rot[0], 0, rot[1])).getBlock() != Blocks.HOPPER
                    && placeRedstone && dispenserDone && !(mc.currentScreen instanceof GuiInventory)) {
                hopperPos = pos.add(rot[0], 0, rot[1]);
                BlockUtil.placeBlock(pos.add(rot[0], 0, rot[1]), hopperSlot, rotate.getValue(), false, swing);
                BlockUtil.openBlock(pos.add(rot[0], 0, rot[1]));
            }

            if (hopperPos != null)
                if (!(mc.world.getBlockState(hopperPos).getBlock() instanceof BlockHopper) && !failed && !(mc.currentScreen instanceof GuiHopper)) {
                    ClientMessage.sendErrorMessage("Hopper Got blown up xDD");
                    disable();
                    return;
                }
            //the coming code is completly skidded and I do not feel bad about it
            //thx phobos <3
            if (mc.currentScreen instanceof GuiHopper) {
                if (!isEnabled()) {
                    return;
                }
                if (Auto32k.mc.player.openContainer instanceof ContainerHopper) {
                    if (!EntityUtil.holding32k(mc.player)) {
                        int swordIndex = -1;
                        for (int i = 0; i < 5; ++i) {
                            if (!EntityUtil.is32k(Auto32k.mc.player.openContainer.inventorySlots.get(0).inventory.getStackInSlot(i)))
                                continue;
                            swordIndex = i;
                            break;
                        }
                        if (swordIndex == -1) {
                            return;
                        }
                        if (slot.getValue() != 0) {
                            InventoryUtil.switchToHotbarSlot(slot.getValue() - 1, false);
                        } else if (!placeMode.getValue().equals("Hopper") && shulkerSlot > 35 && shulkerSlot != 45) {
                            InventoryUtil.switchToHotbarSlot(44 - shulkerSlot, false);
                        }
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, swordIndex, slot.getValue() == 0 ? Auto32k.mc.player.inventory.currentItem : slot.getValue() - 1, ClickType.SWAP, Auto32k.mc.player);
                    } else if (closeGui.getValue() && secretClose.getValue()) {
                        Auto32k.mc.player.closeScreen();
                    }
                    failed = false;
                }
            }
        }
        ++ticksPast;
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.currentScreen instanceof GuiHopper) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                if (secretClose.getValue()) {
                    event.setCancelled(true);
                    packet = event.getPacket();
                }
                if (!EntityUtil.holding32k(mc.player) && !EntityUtil.is32k(mc.player.getHeldItemMainhand())) {
                    mc.player.dropItem(true);
                }
            }
        }
    }

    private final Vec3d[] offsetsHopper = new Vec3d[]{
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(0.0, 1.0, 0.0),
            new Vec3d(0.0, -1.0, 0.0)
    };
}
