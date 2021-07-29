package me.travis.wurstplusthree.hud.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;


@HudComponent.Registration(name = "PvPResources")
public class PVPResources extends HudComponent {
    private EnumSetting mode = new EnumSetting("Mode", "Vertical", Arrays.asList("Vertical", "Horizontal"), this);
    private BooleanSetting xp = new BooleanSetting("XP", true, this);
    private BooleanSetting crystals = new BooleanSetting("Crystals", true, this);
    private BooleanSetting totems = new BooleanSetting("Totems", true, this);
    private BooleanSetting gaps = new BooleanSetting("Gaps", true, this);
    private ColourSetting color = new ColourSetting("Color", new Colour(30, 200, 100), this);
    private BooleanSetting customFont = new BooleanSetting("CustomFont", true, this);


    @Override
    public int getWidth() {
        int width = 0;
        if (mode.is("Horizontal")) {
            if (xp.getValue()) {
                width += 20;
            }
            if (crystals.getValue()) {
                width += 20;
            }
            if (totems.getValue()) {
                width += 20;
            }
            if (gaps.getValue()) {
                width += 20;
            }
        }
        return Math.max(width, 15);
    }

    @Override
    public int getHeight() {
        int height = 0;
        if (mode.is("Vertical")) {
            if (xp.getValue()) {
                height += 20;
            }
            if (crystals.getValue()) {
                height += 20;
            }
            if (totems.getValue()) {
                height += 20;
            }
            if (gaps.getValue()) {
                height += 20;
            }
        }
        return Math.max(height, 15);
    }

    public static int getItemCount(Item item) {
        return mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == item).mapToInt(ItemStack::getCount).sum();
    }

    @Override
    public void renderComponent() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int offset = 0;
        if (crystals.getValue()) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), this.getX()  + (mode.is("Horizontal") ? offset : 0), this.getY() + (mode.is("Vertical") ? offset : 0));
            renderItemOverlayIntoGUI(getX()  + (mode.is("Horizontal") ? offset : 0), getY() + (mode.is("Vertical") ? offset : 0), "" + getItemCount(Item.getItemById(426)));
            offset += 20;
        }
        if (xp.getValue()) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), this.getX()  + (mode.is("Horizontal") ? offset : 0), this.getY() + (mode.is("Vertical") ? offset : 0));
            renderItemOverlayIntoGUI(getX()  + (mode.is("Horizontal") ? offset : 0), getY() + (mode.is("Vertical") ? offset : 0), "" + getItemCount(Item.getItemById(384)));
            offset += 20;
        }
        if (gaps.getValue()) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1), this.getX()  + (mode.is("Horizontal") ? offset : 0), this.getY() + (mode.is("Vertical") ? offset : 0));
            renderItemOverlayIntoGUI(getX()  + (mode.is("Horizontal") ? offset : 0), getY() + (mode.is("Vertical") ? offset : 0), "" + getItemCount(Item.getItemById(322)));
            offset += 20;
        }
        if (totems.getValue()) {
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1), this.getX()  + (mode.is("Horizontal") ? offset : 0), this.getY() + (mode.is("Vertical") ? offset : 0));
            renderItemOverlayIntoGUI(getX()  + (mode.is("Horizontal") ? offset : 0), getY() + (mode.is("Vertical") ? offset : 0), "" + getItemCount(Item.getItemById(449)));
        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public void renderItemOverlayIntoGUI(int xPosition, int yPosition, String s) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        if (customFont.getValue())
            WurstplusThree.GUI_FONT_MANAGER.drawString(s, (float) (xPosition + 19 - 2 - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(s)), (float) (yPosition + 6 + 3), color.getValue().hashCode(), false);
        else
            mc.fontRenderer.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (float) (yPosition + 6 + 3), color.getValue().hashCode());
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }
}
