package me.travis.wurstplusthree.hack.hacks.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.event.processor.EventPriority;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.InventoryUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

@Hack.Registration(name = "Quiver", description = "shoots arrows like brr", category = Hack.Category.COMBAT, isListening = false)
public class Quiver extends Hack {
    private int timer = 0;
    private int stage = 1;
    private int returnSlot = -1;
    private int oldHotbar;
    private Item expectedItem;

    @Override
    public void onEnable() {
        oldHotbar = mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        timer = 0;
        this.stage = 0;
        mc.gameSettings.keyBindUseItem.pressed = false;
        mc.player.inventory.currentItem = oldHotbar;
        if (returnSlot != -1) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, returnSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();
        }
        returnSlot = -1;
    }

    @CommitEvent(priority = EventPriority.HIGH)
    public final void onUpdateWalkingPlayerEvent(@NotNull UpdateWalkingPlayerEvent event) {
        WurstplusThree.ROTATION_MANAGER.setPlayerPitch(-90);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;
        if (mc.currentScreen != null) return;
        if (InventoryUtil.findHotbarBlock(ItemBow.class) == -1) {
            this.disable();
            ClientMessage.sendMessage(ChatFormatting.RED + "No bow found!");
            return;
        }
        if (stage != 0)
            InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
        if (stage == 0) {
            if (mc.player.inventory.getStackInSlot(9).getItem() == expectedItem) {
                this.stage++;
            } else {
                if (!mapArrows()) {
                    this.disable();
                    return;
                }
            }
        } else if (stage == 1) {
            this.stage++;
            timer++;
            return;
        } else if (stage == 2) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            timer = 0;
            this.stage++;
        } else if (stage == 3) {
            if (timer > 5) {
                this.stage++;
            }
        } else if (stage == 4) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.resetActiveHand();
            mc.gameSettings.keyBindUseItem.pressed = false;
            timer = 0;
            this.stage++;
        } else if (stage == 5) {
            if (timer < 12) {
                timer++;
                return;
            }
            this.stage = 0;
            timer = 0;
            expectedItem = null;
        }
        timer++;
    }

    private boolean mapArrows() {
        for (int a = 9; a < 45; a++) {
            if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemTippedArrow) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
                final ItemStack currentArrow = mc.player.inventoryContainer.getInventory().get(9);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    if (!mc.player.isPotionActive(MobEffects.STRENGTH) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.STRENGTH) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.STRONG_STRENGTH) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.LONG_STRENGTH)) {
                        switchTo(a);
                        expectedItem = mc.player.inventory.getStackInSlot(a).getItem();
                        return true;
                    }
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    if (!mc.player.isPotionActive(MobEffects.SPEED) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.SWIFTNESS) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.STRONG_SWIFTNESS) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.LONG_SWIFTNESS)) {
                        switchTo(a);
                        expectedItem = mc.player.inventory.getStackInSlot(a).getItem();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void switchTo(int from) {
        if (from == 9) return;
        if (returnSlot == -1)
            returnSlot = from;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }
}
