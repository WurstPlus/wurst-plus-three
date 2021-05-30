package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Hack.Registration(name = "Auto Armour", description = "automates ur armour", category = Hack.Category.PLAYER, isListening = false)
public class ArmourMend extends Hack {

    IntSetting delay = new IntSetting("Delay", 50, 0, 500, this);
    BooleanSetting mendingTakeOff = new BooleanSetting("Auto Mend", true, this);
    IntSetting enemyRange = new IntSetting("Enemy Range", 8, 0, 25, this, s -> mendingTakeOff.getValue());
    IntSetting helmetThreshold = new IntSetting("Helmet", 80, 0, 100, this, s -> mendingTakeOff.getValue());
    IntSetting chestThreshold = new IntSetting("Chest", 80, 0, 100, this, s -> mendingTakeOff.getValue());
    IntSetting legThreshold = new IntSetting("Leggings", 80, 0, 100, this, s -> mendingTakeOff.getValue());
    IntSetting bootsThreshold = new IntSetting("Boots", 80, 0, 100, this, s -> mendingTakeOff.getValue());
    IntSetting actions = new IntSetting("Actions", 3, 1, 10, this, s -> mendingTakeOff.getValue());
    private final Timer timer = new Timer();
    private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
    private final List<Integer> doneSlots = new ArrayList<>();
    boolean flag;

    @Override
    public void onLogin() {
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        this.taskList.clear();
        this.doneSlots.clear();
        this.flag = false;
    }

    @Override
    public void onLogout() {
        this.taskList.clear();
        this.doneSlots.clear();
    }

    @Override
    public void onTick() {
        if (nullCheck() || mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.taskList.isEmpty()) {
            int slot;
            int slot2;
            int slot3;
            int slot4;
            if (this.mendingTakeOff.getValue() && InventoryUtil.holdingItem(ItemExpBottle.class) &&
                    mc.gameSettings.keyBindUseItem.isKeyDown() &&
                    mc.world.playerEntities.stream().noneMatch(e ->
                            e != mc.player && !WurstplusThree.FRIEND_MANAGER.isFriend(e.getName())
                            && mc.player.getDistance(e) <= (float) this.enemyRange.getValue()
                    ) && !this.flag) {
                int dam;
                int takeOff = 0;
                for (Map.Entry<Integer, ItemStack> armorSlot : this.getArmor().entrySet()) {
                    ItemStack stack = armorSlot.getValue();
                    float percent = (float) this.helmetThreshold.getValue() / 100.0f;
                    dam = Math.round((float) stack.getMaxDamage() * percent);
                    if (dam >= stack.getMaxDamage() - stack.getItemDamage()) continue;
                    ++takeOff;
                }
                if (takeOff == 4) {
                    this.flag = true;
                }
                if (!this.flag) {
                    ItemStack itemStack1 = mc.player.inventoryContainer.getSlot(5).getStack();
                    if (!itemStack1.isEmpty) {
                        float percent = (float) this.helmetThreshold.getValue() / 100.0f;
                        int dam2 = Math.round((float) itemStack1.getMaxDamage() * percent);
                        if (dam2 < itemStack1.getMaxDamage() - itemStack1.getItemDamage()) {
                            this.takeOffSlot(5);
                        }
                    }
                    ItemStack itemStack2 = mc.player.inventoryContainer.getSlot(6).getStack();
                    if (!itemStack2.isEmpty) {
                        float percent = (float) this.chestThreshold.getValue() / 100.0f;
                        int dam3 = Math.round((float) itemStack2.getMaxDamage() * percent);
                        if (dam3 < itemStack2.getMaxDamage() - itemStack2.getItemDamage()) {
                            this.takeOffSlot(6);
                        }
                    }
                    ItemStack itemStack3 = mc.player.inventoryContainer.getSlot(7).getStack();
                    if (!itemStack3.isEmpty) {
                        float percent = (float) this.legThreshold.getValue() / 100.0f;
                        dam = Math.round((float) itemStack3.getMaxDamage() * percent);
                        if (dam < itemStack3.getMaxDamage() - itemStack3.getItemDamage()) {
                            this.takeOffSlot(7);
                        }
                    }
                    ItemStack itemStack4 = mc.player.inventoryContainer.getSlot(8).getStack();
                    if (!itemStack4.isEmpty) {
                        float percent = (float) this.bootsThreshold.getValue() / 100.0f;
                        int dam4 = Math.round((float) itemStack4.getMaxDamage() * percent);
                        if (dam4 < itemStack4.getMaxDamage() - itemStack4.getItemDamage()) {
                            this.takeOffSlot(8);
                        }
                    }
                }
                return;
            }
            this.flag = false;
            ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, true, true)) != -1) {
                this.getSlotOn(5, slot4);
            }
            if (mc.player.inventoryContainer.getSlot(6).getStack().getItem() == Items.AIR && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, true, true)) != -1) {
                this.getSlotOn(6, slot3);
            }
            if (mc.player.inventoryContainer.getSlot(7).getStack().getItem() == Items.AIR && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, true, true)) != -1) {
                this.getSlotOn(7, slot2);
            }
            if (mc.player.inventoryContainer.getSlot(8).getStack().getItem() == Items.AIR && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, true, true)) != -1) {
                this.getSlotOn(8, slot);
            }
        }
        if (this.timer.passedMs((int) ((float) this.delay.getValue() * WurstplusThree.SERVER_MANAGER.getTpsFactor()))) {
            if (!this.taskList.isEmpty()) {
                for (int i = 0; i < this.actions.getValue(); ++i) {
                    InventoryUtil.Task task = this.taskList.poll();
                    if (task == null) continue;
                    task.run();
                }
            }
            this.timer.reset();
        }
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            for (int i : InventoryUtil.findEmptySlots(true)) {
                if (this.doneSlots.contains(target)) continue;
                target = i;
                this.doneSlots.add(i);
            }
            if (target != -1) {
                this.taskList.add(new InventoryUtil.Task(slot));
                this.taskList.add(new InventoryUtil.Task(target));
                this.taskList.add(new InventoryUtil.Task());
            }
        }
    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove((Object) target);
            this.taskList.add(new InventoryUtil.Task(target));
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    private Map<Integer, ItemStack> getArmor() {
        return this.getInventorySlots(5, 8);
    }

    private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }

}
