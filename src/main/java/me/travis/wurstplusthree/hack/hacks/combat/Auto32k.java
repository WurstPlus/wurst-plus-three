package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.*;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.*;

@Hack.Registration(name = "Auto 32K", description = "places some blocks and pulls out a sword", category = Hack.Category.COMBAT, priority = HackPriority.High)
public class Auto32k extends Hack {
    private BlockPos pos;
    private int hopperSlot;
    private int redstoneSlot;
    private int shulkerSlot;
    private int ticksPast;
    private int[] rot;
    private boolean setup;
    private boolean placeRedstone;
    private boolean dispenserDone;
    EnumSetting placeMode = new EnumSetting("Mode", "Dispenser", Arrays.asList("Dispenser", "Looking", "Hopper"), this);
    IntSetting delay = new IntSetting("Delay", 4, 0, 10, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    IntSetting slot = new IntSetting("Slot", 0, 0, 9, this);
    BooleanSetting secretClose = new BooleanSetting("PacketClose", false, this);
    BooleanSetting closeGui = new BooleanSetting("CloseGui", false, this, v -> secretClose.getValue());

    @Override
    public void onEnable() {
        ticksPast = 0;
        setup = false;
        dispenserDone = false;
        placeRedstone = false;
        hopperSlot = -1;
        int dispenser_slot = -1;
        redstoneSlot = -1;
        shulkerSlot = -1;
        int block_slot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER)) hopperSlot = i;
            else if (item == Item.getItemFromBlock(Blocks.DISPENSER)) dispenser_slot = i;
            else if (item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) redstoneSlot = i;
            else if (item instanceof ItemShulkerBox) shulkerSlot = i;
            else if (item instanceof ItemBlock) block_slot = i;
        }
        if ((hopperSlot == -1 || dispenser_slot == -1 || redstoneSlot == -1 || shulkerSlot == -1 || block_slot == -1) && !placeMode.getValue().equals("Hopper")) {
            ClientMessage.sendErrorMessage("missing item");
            this.disable();
            return;
        } else if (hopperSlot == -1 || shulkerSlot == -1) {
            ClientMessage.sendErrorMessage("missing item");
            this.disable();
            return;
        }
        if (placeMode.getValue().equals("Looking")) {
            RayTraceResult r = mc.player.rayTrace(5.0D, mc.getRenderPartialTicks());
            pos = Objects.requireNonNull(r).getBlockPos().up();
            double pos_x = (double) pos.getX() - mc.player.posX;
            double pos_z = (double) pos.getZ() - mc.player.posZ;
            rot = Math.abs(pos_x) > Math.abs(pos_z) ? (pos_x > 0.0D ? new int[] {-1, 0} : new int[] {1, 0}) : (pos_z > 0.0D ? new int[] {0, -1} : new int[] {0, 1});

            if (BlockUtil.canPlaceBlock(this.pos) && BlockUtil.isBlockEmpty(this.pos) && BlockUtil.isBlockEmpty(this.pos.add(this.rot[0], 0, this.rot[1])) && BlockUtil.isBlockEmpty(this.pos.add(0, 1, 0)) && BlockUtil.isBlockEmpty(this.pos.add(0, 2, 0)) && BlockUtil.isBlockEmpty(this.pos.add(this.rot[0], 1, this.rot[1]))) {
                BlockUtil.placeBlock(pos, block_slot, rotate.getValue(), false, swing);
                BlockUtil.rotatePacket((double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getX() + 0.5D, this.pos.getY() + 1, (double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getZ() + 0.5D);
                BlockUtil.placeBlock(this.pos.up(), dispenser_slot, rotate.getValue(), false, swing);
                BlockUtil.openBlock(this.pos.up());
                setup = true;
            } else {
                ClientMessage.sendErrorMessage("unable to place");
                this.disable();
            }
        } else if (placeMode.getValue().equals("Dispenser")) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -2; z <= 2; z++) {
                        this.rot = Math.abs(x) > Math.abs(z) ? (x > 0 ? new int[] {-1, 0} : new int[] {1, 0}) : (z > 0 ? new int[] {0, -1} : new int[] {0, 1});
                        this.pos = mc.player.getPosition().add(x, y, z);
                        if (mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add(x - rot[0] / 2f, (double) y + 0.5D, z + rot[1] / 2)) <= 4.5D && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add((double) x + 0.5D, (double) y + 2.5D, (double) z + 0.5D)) <= 4.5D && BlockUtil.canPlaceBlock(this.pos) && BlockUtil.isBlockEmpty(this.pos) && BlockUtil.isBlockEmpty(this.pos.add(this.rot[0], 0, this.rot[1])) && BlockUtil.isBlockEmpty(this.pos.add(0, 1, 0)) && BlockUtil.isBlockEmpty(this.pos.add(0, 2, 0)) && BlockUtil.isBlockEmpty(this.pos.add(this.rot[0], 1, this.rot[1]))) {
                            BlockUtil.placeBlock(this.pos, block_slot, rotate.getValue(), false, swing);
                            BlockUtil.rotatePacket((double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getX() + 0.5D, this.pos.getY() + 1, (double) this.pos.add(-this.rot[0], 1, -this.rot[1]).getZ() + 0.5D);
                            BlockUtil.placeBlock(this.pos.up(), dispenser_slot, rotate.getValue(), false, swing);
                            BlockUtil.openBlock(this.pos.up());
                            setup = true;
                            return;
                        }
                    }
                }
            }
            ClientMessage.sendErrorMessage("unable to place");
            this.disable();
        } else {
            for (int z = -2; z <= 2; z++) {
                for (int y = -1; y <= 2; y++) {
                    for (int x = -2; x <= 2; x++) {
                        if ((z != 0 || y != 0 || x != 0) && (z != 0 || y != 1 || x != 0) && BlockUtil.isBlockEmpty(mc.player.getPosition().add(z, y, x)) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add((double) z + 0.5D, (double) y + 0.5D, (double) x + 0.5D)) < 4.5D && BlockUtil.isBlockEmpty(mc.player.getPosition().add(z, y + 1, x)) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(mc.player.getPositionVector().add((double) z + 0.5D, (double) y + 1.5D, (double) x + 0.5D)) < 4.5D)  {
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
    public void onUpdate() {
        if (ticksPast > 50 && !(mc.currentScreen instanceof GuiHopper)) {
            ClientMessage.sendErrorMessage("inactive too long, disabling");
            this.disable();
            return;
        }

        if (setup && ticksPast > this.delay.getValue()) {
            if (!dispenserDone) { // ching chong
                try {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, 36 + shulkerSlot, 0, ClickType.QUICK_MOVE, mc.player);
                    dispenserDone = true;
                } catch (Exception ignored) {
                    //EmptyCatchBlockXDDDDDDD
                }
            }

            if (!placeRedstone) {
                BlockUtil.placeBlock(pos.add(0, 2, 0), redstoneSlot, rotate.getValue(), false, swing);
                placeRedstone = true;
                return;
            }

            if (!placeMode.getValue().equals("Hopper") && mc.world.getBlockState(this.pos.add(this.rot[0], 1, this.rot[1])).getBlock() instanceof BlockShulkerBox
                    && mc.world.getBlockState(this.pos.add(this.rot[0], 0, this.rot[1])).getBlock() != Blocks.HOPPER
                    && placeRedstone && dispenserDone && !(mc.currentScreen instanceof GuiInventory)) {
                BlockUtil.placeBlock(this.pos.add(this.rot[0], 0, this.rot[1]), hopperSlot, rotate.getValue(), false, swing);
                BlockUtil.openBlock(this.pos.add(this.rot[0], 0, this.rot[1]));
            }
            //the coming code is completly skidded and I do not feel bad about it
            //thx phobos <3
            if (mc.currentScreen instanceof GuiHopper) {
                if (!this.isEnabled()) {
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
                        if (this.slot.getValue() != 0) {
                            InventoryUtil.switchToHotbarSlot(this.slot.getValue() - 1, false);
                        } else if (!this.placeMode.getValue().equals("Hopper") && this.shulkerSlot > 35 && this.shulkerSlot != 45) {
                            InventoryUtil.switchToHotbarSlot(44 - this.shulkerSlot, false);
                        }
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, swordIndex, this.slot.getValue() == 0 ? Auto32k.mc.player.inventory.currentItem : this.slot.getValue() - 1, ClickType.SWAP, Auto32k.mc.player);
                    } else if (this.closeGui.getValue() && this.secretClose.getValue()) {
                        Auto32k.mc.player.closeScreen();
                    }
                } else if (!EntityUtil.holding32k(mc.player)) {
                }
            }
        }
        ++this.ticksPast;
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.currentScreen instanceof GuiHopper) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                if (this.secretClose.getValue()) {
                    event.setCancelled(true);
                }
                if (!EntityUtil.holding32k(mc.player) && !EntityUtil.is32k(mc.player.getHeldItemMainhand())) {
                    mc.player.dropItem(true);
                }
            }
        }
    }
}
