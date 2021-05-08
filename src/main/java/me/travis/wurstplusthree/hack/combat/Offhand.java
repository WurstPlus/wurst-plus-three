package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.setting.type.KeySetting;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.MouseUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

@Hack.Registration(name = "Offhand", description = "puts things in ur offhand", category = Hack.Category.COMBAT, isListening = false)
public class Offhand extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Totem", Arrays.asList("Totem", "Crystal", "Gapple"), this);
    BooleanSetting gapHole = new BooleanSetting("Gap In Hole", true, this);
    IntSetting TotemHp = new IntSetting("Totem HP", 16, 0, 36, this);
    KeySetting gapKey = new KeySetting("Gap Key", Keyboard.KEY_NONE, this);

    private boolean switching;
    private boolean keyPressed;
    private int lastSlot;

    @Override
    public void onUpdate() {

        if (switching) {
            swapItems(lastSlot, 2);
            return;

        }
        if (mc.currentScreen instanceof GuiContainer) {
            return;

        }

        float hp = EntityUtil.getHealth(mc.player);

        if (hp < TotemHp.getValue()) {
            this.swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
            return;
        }
        if (gapKey.getKey() < -1) {
            if (Mouse.isButtonDown(MouseUtil.convertToMouse(gapKey.getKey()))) {
                if (!keyPressed && mc.currentScreen == null) {
                    this.swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
                }
                keyPressed = true;
                return;
            } else {
                keyPressed = false;
            }
        } else if (gapKey.getKey() > -1) {
            if (Keyboard.isKeyDown(gapKey.getKey())) {
                if (!keyPressed && mc.currentScreen == null) {
                    this.swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
                }
                keyPressed = true;
                return;
            } else {
                keyPressed = false;
            }
        }


        if (PlayerUtil.isInHole() && gapHole.getValue() && !WurstplusThree.HACKS.ishackEnabled("Crystal Aura")) {
            this.swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
            return;
        }



        switch (mode.getValue()) {
            case "Totem":
                this.swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
                return;
            case "Crystal":
                this.swapItems(getItemSlot(Items.END_CRYSTAL), 1);
                return;
            case "Gapple":
                this.swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
                return;
        }


    }

    public void swapItems(int slot, int step) {
        if (slot == -1) return;
        if (step == 0) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
        if (step == 1) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = true;
            lastSlot = slot;
        }
        if (step == 2) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            switching = false;

        }

        mc.playerController.updateController();
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
