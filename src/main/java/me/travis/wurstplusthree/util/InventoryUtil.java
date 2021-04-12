package me.travis.wurstplusthree.util;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil implements Globals {

    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (InventoryUtil.mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            InventoryUtil.mc.playerController.updateController();
        } else {
            InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            InventoryUtil.mc.player.inventory.currentItem = slot;
            InventoryUtil.mc.playerController.updateController();
        }
    }

    public static void switchToHotbarSlot(Class c, boolean silent) {
        int slot = InventoryUtil.findHotbarBlock(c);
        if (slot > -1) {
            InventoryUtil.switchToHotbarSlot(slot, silent);
        }
    }

    public static int findHotbarBlock(Class c) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (c.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !c.isInstance(((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }

}
