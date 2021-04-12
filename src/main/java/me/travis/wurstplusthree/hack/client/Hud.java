package me.travis.wurstplusthree.hack.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.HudUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

public class Hud extends Hack {

    public Hud() {
        super("HUD", "Handles Hud Elements", Category.CLIENT, false, false);

        this.enable();
    }

    BooleanSetting welcomer = new BooleanSetting("Welcomer", false, this);

    BooleanSetting watermark = new BooleanSetting("Watermark", true, this);
    BooleanSetting fps = new BooleanSetting("Fps", false, this);
    BooleanSetting tps = new BooleanSetting("Tps", false, this);
    BooleanSetting ping = new BooleanSetting("Ping", false, this);
    BooleanSetting clock = new BooleanSetting("Clock", true, this);
    BooleanSetting arrayList = new BooleanSetting("ArrayList", true, this);

    BooleanSetting armour = new BooleanSetting("Armour", false, this);

    ColourSetting fontColour = new ColourSetting("Font", new Colour(255, 255, 255, 200), this);
    ColourSetting outlineColour = new ColourSetting("Outline", new Colour(0, 0, 0, 255), this);

    BooleanSetting customFont = new BooleanSetting("Custom Font", true, this);

    private ScaledResolution scaledResolution;

    @Override
    public void onRender2D(Render2DEvent event) {
        scaledResolution = new ScaledResolution(mc);

        if (welcomer.getValue()) {
            String line = HudUtil.getWelcomerLine();
            this.drawStringCenterX(line, 20);
        }

        this.doTopleft();
        this.doTopRight();
        this.doBottomRight();

        if (this.armour.getValue()) {
            this.renderArmorHUD(true);
        }

    }

    private void doBottomRight() {
        if (this.arrayList.getValue()) {
            List<Hack> hacks = WurstplusThree.HACKS.getSortedHacks(false);
            int y = scaledResolution.getScaledHeight() - (11 * hacks.size()) + 2;
            for (Hack hack : hacks) {
                String name = hack.getFullArrayString();
                drawString(name, this.getRightX(name, 2), y);
                y += 11;
            }
        }
    }

    private void doTopleft() {
        int y = 10;
        if (watermark.getValue()) {
            drawString(ChatFormatting.GOLD + WurstplusThree.MODNAME + ChatFormatting.RESET
                    + " v" + WurstplusThree.MODVER, 10, y);
            y += 16;
        }
        if (fps.getValue()) {
            drawString("Fps " + HudUtil.getFpsLine(), 10, y);
            y += 12;
        }
        if (tps.getValue()) {
            drawString("Tps " + HudUtil.getTpsLine(), 10, y);
            y += 12;
        }
        if (ping.getValue()) {
            drawString("Ping " + HudUtil.getPingLine(), 10, y);
            y += 12;
        }
    }

    private void doTopRight() {
        int y = 10;
        if (clock.getValue()) {
            String clock = HudUtil.getAnaTimeLine();
            drawString(clock, this.getRightX(clock, 10), y);
            y += 12;
        }
    }

    private int getRightX(String string, int x) {
        if (this.customFont.getValue()) {
            return scaledResolution.getScaledWidth() - x - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string);
        } else {
            return scaledResolution.getScaledWidth() - x - mc.fontRenderer.getStringWidth(string);
        }
    }

    private void drawString(String string, int x, int y) {
        if (this.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        } else {
            mc.fontRenderer.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        }
    }

    private void drawString(String string, int x, int y, int colour) {
        if (this.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(string, x, y, colour);
        } else {
            mc.fontRenderer.drawStringWithShadow(string, x, y, colour);
        }
    }

    private int getStringWidth(String string) {
        if (this.customFont.getValue()) {
            return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string);
        } else {
            return mc.fontRenderer.getStringWidth(string);
        }
    }

    private void drawStringCenterX(String string, int y) {
        if (this.customFont.getValue()) {
            int x = (scaledResolution.getScaledWidth() / 2) - (WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string) / 2);
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        } else {
            int x = (scaledResolution.getScaledWidth() / 2) - (mc.fontRenderer.getStringWidth(string) / 2);
            mc.fontRenderer.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        }
    }

    public void renderArmorHUD(final boolean percent) {
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.drawString(s, x + 19 - 2 - this.getStringWidth(s), y + 7, 1677215);
            if (!percent) {
                continue;
            }
            int dmg = 0;
            final int itemDurability = is.getMaxDamage() - is.getItemDamage();
            final float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            if (percent) {
                dmg = 100 - (int) (red * 100.0f);
            } else {
                dmg = itemDurability;
            }
            this.drawString(dmg+"", x + 8 - this.getStringWidth(dmg+"") / 2, y - 9, (new Color(red, green, 0)).getRGB());
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

}
