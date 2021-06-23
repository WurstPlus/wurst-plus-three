package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.util.HudUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Madmegsox1
 * @since 20/06/2021
 */

@HudElement.Element(name = "Armor", posX = 100, posY = 100)
public class HudArmor extends HudElement {

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public int getWidth() {
        if (nullCheck()) {
            return 0;
        }
        int length = 0;

        for (final ItemStack is : mc.player.inventory.armorInventory) {
            length += 20;
        }
        return length;
    }


    @Override
    public void onRender2D(Render2DEvent event) {
        GlStateManager.enableTexture2D();
        int iteration = -20;
        List<ItemStack> list = mc.player.inventory.armorInventory;
        ListIterator<ItemStack> listIterator = list.listIterator(list.size());
        while (listIterator.hasPrevious()) {
            ItemStack is = listIterator.previous();
            iteration += 20;
            if (is.isEmpty()) {
                continue;
            }
            final int x = getX() + iteration;
            final int y = getY() + 10;
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            HudUtil.drawHudString(s, x + 19 - 2 - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(s), y + 7, 1677215);
            int dmg;
            float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            float red = 1.0f - green;
            dmg = 100 - (int) (red * 100.0f);
            if (red > 1f) red = 1f;
            if (green > 1f) green = 1f;
            if (red < 0f) red = 0f;
            if (green < 0f) green = 0f;
            HudUtil.drawHudString(dmg + "", (int) (x + 8 - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(dmg + "") / 2f), y - 9, (new Color(red, green, 0)).getRGB());
        }

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
}
