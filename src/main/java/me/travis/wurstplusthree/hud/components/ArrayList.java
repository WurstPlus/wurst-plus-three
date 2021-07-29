package me.travis.wurstplusthree.hud.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.font.CustomFont;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import scala.Int;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

@HudComponent.Registration(name = "ArrayList")
public class ArrayList extends HudComponent {
    private EnumSetting mode = new EnumSetting("Mode", "Rainbow", Arrays.asList("AlphaStep", "Rainbow", "Normal", "ModuleColor"), this);
    private BooleanSetting customFont = new BooleanSetting("CustomFont", true, this);
    private ColourSetting color = new ColourSetting("Color", new Colour(30, 200, 100), this, v -> !mode.is("Rainbow"));
    private DoubleSetting speed = new DoubleSetting("RainbowSpeed", 20.0D, 1.0D, 100.0D, this, v -> mode.is("Rainbow"));
    private DoubleSetting saturation = new DoubleSetting("RainbowSaturation", 0.8D, 0.0D, 1.0D, this, v -> mode.is("Rainbow"));
    private DoubleSetting brightness = new DoubleSetting("RainbowBrightness", 0.8D, 0.0D, 1.0D, this, v -> mode.is("Rainbow"));
    private DoubleSetting difference = new DoubleSetting("RainbowDifference", 20.0D, 1.0D, 100.0D, this, v -> mode.is("Rainbow"));

    private int width;
    private int height;
    int count;

    @Override
    public int getHeight() {
        return height + 4;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void renderComponent() {
        count = 0;
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        int screenHeight = new ScaledResolution(mc).getScaledHeight();
        AtomicReference<Integer> largestwidth = new AtomicReference<>(75);
        WurstplusThree.HACKS.getHacks().stream().filter(Hack::isEnabled).filter(Hack::isNotification).sorted(Comparator.comparing(hack -> (customFont.getValue() ? WurstplusThree.GUI_FONT_MANAGER.getTextWidth(hack.getName() + hack.getFullArrayString()) : mc.fontRenderer.getStringWidth(hack.getName() + hack.getFullArrayString())) * (getY() + (height / 2) < screenHeight / 2 ? -1 : 1))).forEach(hack -> {
            int modWidth = mc.fontRenderer.getStringWidth(hack.getName() + TextFormatting.WHITE + hack.getFullArrayString());
            if (customFont.getValue())
                modWidth = WurstplusThree.GUI_FONT_MANAGER.getTextWidth(hack.getName() + TextFormatting.WHITE + hack.getFullArrayString());
            if (modWidth > largestwidth.get()) {
                largestwidth.set(modWidth);
            }
            String modText = hack.getName() + TextFormatting.WHITE + hack.getFullArrayString();

            if (getX() + (width / 2) < (screenWidth / 2)) {
                if (customFont.getValue())
                    WurstplusThree.GUI_FONT_MANAGER.drawString(modText, getX() + 3, getY() + ((WurstplusThree.GUI_FONT_MANAGER.getTextHeight() + 2)* count) + 2, getColor(hack), false);
                else
                    mc.fontRenderer.drawString(modText, getX() + 3, getY() + ((mc.fontRenderer.FONT_HEIGHT + 2)* count) + 2, getColor(hack));
            } else {
                if (customFont.getValue())
                    WurstplusThree.GUI_FONT_MANAGER.drawString(modText, getX() - 3 - modWidth + width, getY() + ((WurstplusThree.GUI_FONT_MANAGER.getTextHeight() + 2)* count) + 2, getColor(hack), false);
                else
                mc.fontRenderer.drawString(modText, getX() - 3 - modWidth + width, getY() + ((mc.fontRenderer.FONT_HEIGHT + 2)* count) + 2, getColor(hack));
            }

            count++;
        });
        width = largestwidth.get();
        if (customFont.getValue())
            height = ((WurstplusThree.GUI_FONT_MANAGER.getTextHeight() + 2) * count);
        else
            height= ((mc.fontRenderer.FONT_HEIGHT + 2) * count);
    }

    private int getColor(Hack hack) {
        switch (mode.getValue()) {
            case "AlphaStep":
                return alphaStep(new Color(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue()), 50, (count * 2) + 10).getRGB();
            case "Rainbow":
                return rainbow(count);
            case "ModuleColor":
                return hack.getModuleColor();
            case "Normal":
                return this.color.getValue().getRGB();
        }
        return -1;
    }

    private int rainbow(long offset) {
        float hue = (float) ((((System.currentTimeMillis() * (speed.getValue() / 10)) + (offset * 500)) % (30000L / (difference.getValue() / 100))) / (30000.0f / (difference.getValue() / 20)));
        int rgb = Color.HSBtoRGB(hue, saturation.getAsFloat(), brightness.getAsFloat());
        int red = rgb >> 16 & 255;
        int green = rgb >> 8 & 255;
        int blue = rgb & 255;
        return toRGBA(red, green, blue, 255);
    }

    private Color alphaStep(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index / (float) count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2.0F;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    private int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }
}
