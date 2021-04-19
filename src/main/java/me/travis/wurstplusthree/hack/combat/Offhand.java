package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.EntityUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

import java.util.Arrays;

public class Offhand extends Hack {

    public Offhand() {
        super("Offhand", "puts things in ur offhand", Category.COMBAT, false, false);
    }

    EnumSetting mode = new EnumSetting("Mode", "Totem", Arrays.asList("Totem", "Crystal", "Gapple"), this);
    BooleanSetting gapHole = new BooleanSetting("Gap In Hole", true, this);
    IntSetting TotemHp = new IntSetting("Totem HP", 16, 0, 36, this);

    private boolean switching;
    private int lastSlot;

    @Override
    public void onUpdate() {

        if (switching) {
            swapItems(lastSlot, 2);
            return;
        }

        float hp = EntityUtil.getHealth(mc.player);

        if (hp < TotemHp.getValue()) {
            this.swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
            return;
        }

        if (PlayerUtil.isInHole() && gapHole.getValue() && !WurstplusThree.HACKS.ishackEnabled("Crystal Aura")) {
            this.swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
            return;
        }

        if (mode.is("Totem")) {
            this.swapItems(getItemSlot(Items.TOTEM_OF_UNDYING), 1);
            return;
        }

        if (mode.is("Crystal")) {
            this.swapItems(getItemSlot(Items.END_CRYSTAL), 1);
            return;
        }

        if (mode.is("Gapple")) {
            this.swapItems(getItemSlot(Items.GOLDEN_APPLE), 1);
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
        for(int i = 36; i >= 0; i--) {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if(item == input) {
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
