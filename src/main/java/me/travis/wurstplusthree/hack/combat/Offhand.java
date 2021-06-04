package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.MoveEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.CrystalUtil;
import me.travis.wurstplusthree.util.EntityUtil;
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

import java.util.Arrays;

@Hack.Registration(name = "Offhand", description = "puts things in ur offhand", category = Hack.Category.COMBAT, isListening = false)
public class Offhand extends Hack {

    EnumSetting mode = new EnumSetting("Mode", "Totem", Arrays.asList("Totem", "Crystal", "Gapple"), this);
    BooleanSetting cancelMovement = new BooleanSetting("CancelMovement", false, this); // 2b2t does not let you swap items when moving so you must stop movement first then swap.
    IntSetting TotemHp = new IntSetting("Totem HP", 16, 0, 36, this);
    IntSetting HoleHP = new IntSetting("Hole HP", 16, 0, 36, this);
    BooleanSetting GapSwitch = new BooleanSetting("Gap Swap", false, this);
    BooleanSetting GapOnSword = new BooleanSetting("Sword Gap", false, this);
    BooleanSetting GapOnPick = new BooleanSetting("Pick Gap", false, this);
    BooleanSetting Always = new BooleanSetting("Always", false, this);
    BooleanSetting CrystalCheck = new BooleanSetting("CrystalCheck", false, this);
    IntSetting cooldown = new IntSetting("Cooldown", 0, 0, 40, this);

    private int timer = 0;

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }

        float hp = EntityUtil.getHealth(mc.player);

        if (hp < TotemHp.getValue()) {
            // Stop the player movement so totem can be swapped on 2b2t.
            if (cancelMovement.getValue()) {
                StopPlayerMovement.toggle(true);
            }
            this.swapItems(getItemSlot(Items.TOTEM_OF_UNDYING));
            // Stop the cancelling of the MoveEvent after the totem has been swapped.
            if (cancelMovement.getValue()) {
                StopPlayerMovement.toggle(false);
            }
            return;
        }
    }

    @Override
    public void onTick() {
        timer = timer + 1;
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {
            float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            if (hp > TotemHp.getValue() || (EntityUtil.isInHole(mc.player) && hp > HoleHP.getValue())) {
                if (mode.getValue().equalsIgnoreCase("crystal") && (!CrystalAura.INSTANCE.autoSwitch.getValue().equals("Offhand") || CrystalAura.INSTANCE.renderBlock != null || !CrystalAura.INSTANCE.isEnabled()) && !(((GapOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) || Always.getValue() || (GapOnPick.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) && mc.gameSettings.keyBindUseItem.isKeyDown() && GapSwitch.getValue())) {
                    swapItems(getItemSlot(Items.END_CRYSTAL));
                    return;
                } else if (((GapOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) || Always.getValue() || (GapOnPick.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe)) && mc.gameSettings.keyBindUseItem.isKeyDown() && GapSwitch.getValue()) {
                    swapItems(getItemSlot(Items.GOLDEN_APPLE));
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                        mc.playerController.isHittingBlock = true;
                    }
                    return;
                } else if (CrystalAura.INSTANCE.autoSwitch.getValue().equals("Offhand") && CrystalAura.INSTANCE.renderBlock == null && CrystalAura.INSTANCE.isEnabled() && mode.getValue().equals("Crystal")){
                    swapItems(getItemSlot(Items.TOTEM_OF_UNDYING));
                    return;
                }
                if (mode.getValue().equalsIgnoreCase("totem")) {
                    swapItems(getItemSlot(Items.TOTEM_OF_UNDYING));
                    return;
                }
                if (mode.getValue().equalsIgnoreCase("gapple")) {
                    swapItems(getItemSlot(Items.GOLDEN_APPLE));
                    return;
                }
            } else {
                swapItems(getItemSlot(Items.TOTEM_OF_UNDYING));
                return;
            }
            if (mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                swapItems(getItemSlot(Items.TOTEM_OF_UNDYING));
            }
        }
    }

    private boolean lethalToLocalCheck() {
        if (!CrystalCheck.getValue()) {
            return false;
        }
        /*
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
         */
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) <= 12) {
                if (CrystalUtil.calculateDamage(new BlockPos(entity.posX, entity.posY, entity.posZ), mc.player, false) >= mc.player.getHealth()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void swapItems(int slot) {
        if (slot == -1 || (timer <= cooldown.getValue()) && mc.player.inventory.getStackInSlot(slot).getItem() != Items.TOTEM_OF_UNDYING) return;
        timer = 0;
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
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
    public static class StopPlayerMovement {
        private static StopPlayerMovement stopPlayerMovement = new StopPlayerMovement();

        public static void toggle(boolean on) {
            if (on) {
                WurstplusThree.EVENT_PROCESSOR.addEventListener(stopPlayerMovement);
            } else {
                WurstplusThree.EVENT_PROCESSOR.removeEventListener(stopPlayerMovement);
            }
        }

        // Cancel the MoveEvent.
        @CommitEvent
        public void onMove(MoveEvent event) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue();
    }

}
