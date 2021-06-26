package me.travis.wurstplusthree.hack.hacks.combat;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.InventoryUtil;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Objects;

@Hack.Registration(name = "Auto32K", description = "Does 32k because you are lazy urself", category = Hack.Category.COMBAT, isListening = false)
public class Auto32k extends Hack {
    private BlockPos pos;
    private int hopper_slot;
    private int redstone_slot;
    private int shulker_slot;
    private int ticks_past;
    private int[] rot;
    private boolean setup;
    private boolean place_redstone;
    private boolean dispenser_done;
    EnumSetting placeMode = new EnumSetting("PlaceMode", "Dispenser", Arrays.asList("Dispenser", "Looking", "Hopper"), this);
    IntSetting delay = new IntSetting("Delay", 4, 0, 10, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    IntSetting slot = new IntSetting("Slot", 0, 0, 9, this);
    BooleanSetting secretClose = new BooleanSetting("PacketClose", false, this);
    BooleanSetting closeGui = new BooleanSetting("closeGui", false, this, v -> secretClose.getValue());

    @Override
    public void onEnable() {
        ticks_past = 0;
        setup = false;
        dispenser_done = false;
        place_redstone = false;
        hopper_slot = -1;
        int dispenser_slot = -1;
        redstone_slot = -1;
        shulker_slot = -1;
        int block_slot = -1;
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Item.getItemFromBlock(Blocks.HOPPER)) hopper_slot = i;
            else if (item == Item.getItemFromBlock(Blocks.DISPENSER)) dispenser_slot = i;
            else if (item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) redstone_slot = i;
            else if (item instanceof ItemShulkerBox) shulker_slot = i;
            else if (item instanceof ItemBlock) block_slot = i;
        }
        if ((hopper_slot == -1 || dispenser_slot == -1 || redstone_slot == -1 || shulker_slot == -1 || block_slot == -1) && !placeMode.getValue().equals("Hopper")) {
            ClientMessage.sendErrorMessage("missing item");
            this.disable();
            return;
        } else if (hopper_slot == -1 || shulker_slot == -1) {
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
                            BlockUtil.placeBlock(mc.player.getPosition().add(z, y, x), hopper_slot, rotate.getValue(), false, swing);
                            BlockUtil.placeBlock(mc.player.getPosition().add(z, y + 1, x), shulker_slot, rotate.getValue(), false, swing);
                            BlockUtil.openBlock(mc.player.getPosition().add(z, y, x));
                            pos = mc.player.getPosition().add(z, y, x);
                            dispenser_done = true;
                            place_redstone = true;
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
        if (ticks_past > 50 && !(mc.currentScreen instanceof GuiHopper)) {
            ClientMessage.sendErrorMessage("inactive too long, disabling");
            this.disable();
            return;
        }

        if (setup && ticks_past > this.delay.getValue()) {
            if (!dispenser_done) { // ching chong
                try {
                    mc.playerController.windowClick(mc.player.openContainer.windowId, 36 + shulker_slot, 0, ClickType.QUICK_MOVE, mc.player);
                    dispenser_done = true;
                } catch (Exception ignored) {
                    //EmptyCatchBlockXDDDDDDD
                }
            }

            if (!place_redstone) {
                BlockUtil.placeBlock(pos.add(0, 2, 0), redstone_slot, rotate.getValue(), false, swing);
                place_redstone = true;
                return;
            }

            if (!placeMode.getValue().equals("Hopper") && mc.world.getBlockState(this.pos.add(this.rot[0], 1, this.rot[1])).getBlock() instanceof BlockShulkerBox
                    && mc.world.getBlockState(this.pos.add(this.rot[0], 0, this.rot[1])).getBlock() != Blocks.HOPPER
                    && place_redstone && dispenser_done && !(mc.currentScreen instanceof GuiInventory)) {
                BlockUtil.placeBlock(this.pos.add(this.rot[0], 0, this.rot[1]), hopper_slot, rotate.getValue(), false, swing);
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
                        } else if (!this.placeMode.getValue().equals("Hopper") && this.shulker_slot > 35 && this.shulker_slot != 45) {
                            InventoryUtil.switchToHotbarSlot(44 - this.shulker_slot, false);
                        }
                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, swordIndex, this.slot.getValue() == 0 ? Auto32k.mc.player.inventory.currentItem : this.slot.getValue() - 1, ClickType.SWAP, Auto32k.mc.player);
                    } else if (this.closeGui.getValue().booleanValue() && this.secretClose.getValue().booleanValue()) {
                        Auto32k.mc.player.closeScreen();
                    }
                } else if (!EntityUtil.holding32k(mc.player)) {
                }
            }
        }
        ++this.ticks_past;
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public void onPacketSend(PacketEvent.Send event) {
        if (mc.currentScreen instanceof GuiHopper) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                if (this.secretClose.getValue().booleanValue()) {
                    event.setCancelled(true);
                }
                if (!EntityUtil.holding32k(mc.player) && !EntityUtil.is32k(mc.player.getHeldItemMainhand())) {
                    mc.player.dropItem(true);
                }
            }
        }
    }
}
