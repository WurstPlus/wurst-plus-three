package me.travis.wurstplusthree.hack.combat;

/**
 * @Author wallhacks0
 * @Author TrvsF
 */
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;

import java.util.Arrays;

@Hack.Registration(name = "Offhand", description = "puts things in ur offhand", category = Hack.Category.COMBAT, isListening = false)
public class Offhand extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Totem", Arrays.asList("Totem", "Crystal", "Gapple"), this);
    IntSetting totemHp = new IntSetting("Totem HP", 16, 0, 36, this);
    IntSetting holeHp = new IntSetting("Hole HP", 16, 0, 36, this);
    BooleanSetting crystalCheck = new BooleanSetting("CrystalCheck", false, this);
    IntSetting crystalDistance = new IntSetting("Crystal Distance", 10, 0, 14, this);
    EnumSetting gapSwap = new EnumSetting("GapSwap", "Never", Arrays.asList("Never", "Sword", "Pickaxe", "Both",  "Always"), this);
    IntSetting steps = new IntSetting("Steps", 1, 1, 3, this);


    private int step;
    private int lastSlot;

    public void onEnable() {
        step = -1;
    }

    @Override
    public void onTick() {
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {
            if (step != -1) {
                swapItems(lastSlot, step);
                return;
            }
            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            if ((hp <= (EntityUtil.isInHole(mc.player) ? holeHp.getValue() : totemHp.getValue()) || lethalToLocalCheck() || (mode.getValue().equals("Totem") && !shouldGapSwap()))) {
                swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), steps.getValue());
                return;
            }
            if (mode.getValue().equals("Gapple") || shouldGapSwap()) {
                swapItems(getItemSlot(Items.GOLDEN_APPLE), steps.getValue());
                return;
            }
            if (mode.getValue().equals("Crystal")) {
                swapItems(getItemSlot(Items.END_CRYSTAL), steps.getValue());
            }
        }
    }

    public void swapItems(int slot, int step) {
        if (slot == -1) return;
        if (step == 1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            step = -1;
        }
        if (step == 2) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            this.step = 4;
            lastSlot = slot;
        }
        if (step == 3) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            this.step = 5;
            lastSlot = slot;
        }
        if (step == 4) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            this.step = -1;
        }
        if (step == 5) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            this.step = 6;
        }
        if (step == 6) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            this.step = -1;
        }
        mc.playerController.updateController();
    }

    private boolean shouldGapSwap() {
        if (mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if ((gapSwap.getValue().equals("Sword") || gapSwap.getValue().equals("Both")) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                return true;
            }
            if ((gapSwap.getValue().equals("Pickaxe") || gapSwap.getValue().equals("Both")) && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                return true;
            }
            if (gapSwap.getValue().equals("Always")) {
                return true;
            }
        }
        return false;
    }

    private boolean lethalToLocalCheck() {
        if (!crystalCheck.getValue()) {
            return false;
        }
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) <= crystalDistance.getValue()) {
                return true;
            }
        }
        return false;
    }

    private int getItemSlot(Item input) {
        if (input == mc.player.getHeldItemOffhand().getItem()) return -1;
        for (int i = 36; i >= 0; i--) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == input) {
                if (i < 9) {
                    if (input == Items.GOLDEN_APPLE) {
                        return -1;
                    }
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }
}