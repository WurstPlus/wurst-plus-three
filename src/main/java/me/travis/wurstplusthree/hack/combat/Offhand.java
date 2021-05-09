package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

@Hack.Registration(name = "Offhand", description = "puts things in ur offhand", category = Hack.Category.COMBAT, isListening = false)
public class Offhand extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Totem", Arrays.asList("Totem", "Crystal", "Gapple"), this);
    IntSetting TotemHp = new IntSetting("Switch HP", 16, 0, 36, this);
    IntSetting HoleHP = new IntSetting("Hole HP", 16, 0, 36, this);
    BooleanSetting GapSwitch = new BooleanSetting("Gap Swap", false, this);
    BooleanSetting GapOnSword = new BooleanSetting("Sword Gap", false, this);
    BooleanSetting GapOnPick = new BooleanSetting("Pick Gap", false, this);
    BooleanSetting Always = new BooleanSetting("Always", false, this);
    BooleanSetting CrystalCheck = new BooleanSetting("CrystalCheck", false, this);


    private boolean switching;
    private int lastSlot;

    @Override
    public void onTick() {
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {

            if (switching) {
                swapItems(lastSlot, 2);
                return;
            }

            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();

            if ((hp > TotemHp.getValue() && (!crystalDamage() || !CrystalCheck.getValue()) || (EntityUtil.isInHole(mc.player) && hp > HoleHP.getValue()))) {

                if (mode.getValue().equalsIgnoreCase("crystal") && !(((GapOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) || Always.getValue() || (GapOnPick.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) && mc.gameSettings.keyBindUseItem.isKeyDown() && GapSwitch.getValue())) {
                    swapItems(getItemSlot(Items.END_CRYSTAL), 0);
                    return;
                } else if (((GapOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) || Always.getValue() || (GapOnPick.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) && mc.gameSettings.keyBindUseItem.isKeyDown() && GapSwitch.getValue()) {
                    swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                        mc.playerController.isHittingBlock = true;
                    }
                    return;
                }
                if (mode.getValue().equalsIgnoreCase("totem")) {
                    swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
                    return;
                }
                if (mode.getValue().equalsIgnoreCase("gapple")) {
                    swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
                    return;
                }
            } else {
                swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
                return;
            }


            if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
            }

        }

    }

    private boolean crystalDamage() {
        double ris2 = 0;
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) <= 12) {
                if ((ris2 = CrystalUtil.calculateDamage(new BlockPos(entity.posX, entity.posY, entity.posZ), mc.player)) >= mc.player.getHealth()) {
                    return true;
                }
            }
        }
        return false;
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
