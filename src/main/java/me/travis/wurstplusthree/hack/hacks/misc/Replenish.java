package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Hack.Registration(name = "Replenish", description = "replenishes items in ur hotbar", category = Hack.Category.MISC)
public class Replenish extends Hack {

    IntSetting threshold = new IntSetting("Threshold", 32, 0, 63, this);
    IntSetting tickDelay = new IntSetting("Tick Delay", 2, 0, 10, this);

    private int delayStep = 0;

    @Override
    public void onUpdate() {
        if(nullCheck())return;
        if (mc.currentScreen instanceof GuiContainer) return;

        if (delayStep < tickDelay.getValue()) {
            delayStep++;
            return;
        }

        delayStep = 0;

        final Pair<Integer, Integer> slots = findReplenishableHotbarSlot();
        if (slots == null) return;

        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        Pair<Integer, Integer> returnPair = null;
        for (final Map.Entry<Integer, ItemStack> hotbarSlot : getHotbar().entrySet()) {
            final ItemStack stack = hotbarSlot.getValue();
            if (!stack.isEmpty) {
                if (stack.getItem() == Items.AIR) {
                    continue;
                }
                if (!stack.isStackable()) {
                    continue;
                }
                if (stack.stackSize >= stack.getMaxStackSize()) {
                    continue;
                }
                if (stack.stackSize > threshold.getValue()) {
                    continue;
                }
                final int inventorySlot = this.findCompatibleInventorySlot(stack);
                if (inventorySlot == -1) {
                    continue;
                }
                returnPair = new Pair<>(inventorySlot, hotbarSlot.getKey());
            }
        }
        return returnPair;
    }

    private int findCompatibleInventorySlot(final ItemStack hotbarStack) {
        int inventorySlot = -1;
        int smallestStackSize = 999;
        for (final Map.Entry<Integer, ItemStack> entry : getInventory().entrySet()) {
            final ItemStack inventoryStack = entry.getValue();
            if (!inventoryStack.isEmpty) {
                if (inventoryStack.getItem() == Items.AIR) {
                    continue;
                }
                if (!this.isCompatibleStacks(hotbarStack, inventoryStack)) {
                    continue;
                }
                final int currentStackSize = mc.player.inventoryContainer.getInventory().get(entry.getKey()).stackSize;
                if (smallestStackSize <= currentStackSize) {
                    continue;
                }
                smallestStackSize = currentStackSize;
                inventorySlot = entry.getKey();
            }
        }
        return inventorySlot;
    }

    private boolean isCompatibleStacks(final ItemStack stack1, final ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            final Block block1 = ((ItemBlock)stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock)stack2.getItem()).getBlock();
            if (!block1.material.equals(block2.material)) {
                return false;
            }
        }
        return stack1.getDisplayName().equals(stack2.getDisplayName()) && stack1.getItemDamage() == stack2.getItemDamage();
    }

    private Map<Integer, ItemStack> getInventory() {
        return getInvSlots(9, 35);
    }

    private Map<Integer, ItemStack> getHotbar() {
        return getInvSlots(36, 44);
    }

    private Map<Integer, ItemStack> getInvSlots(int current, final int last) {
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }

}
