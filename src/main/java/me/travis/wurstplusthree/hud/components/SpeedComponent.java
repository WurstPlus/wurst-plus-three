package me.travis.wurstplusthree.hud.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.MathsUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

@HudComponent.Registration(name = "Speed")
public class SpeedComponent extends HudComponent {
    private ColourSetting color = new ColourSetting("Color", new Colour(30, 200, 100), this);
    private BooleanSetting customFont = new BooleanSetting("CustomFont", true, this);
    private String renderString;

    @Override
    public int getHeight() {
        if (customFont.getValue())
            return WurstplusThree.GUI_FONT_MANAGER.getTextHeight() + 4;
        return mc.fontRenderer.FONT_HEIGHT + 4;
    }

    @Override
    public int getWidth() {
        if (customFont.getValue())
            return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(renderString) + 5;
        return mc.fontRenderer.getStringWidth(renderString) + 5;
    }

    public static String getSpeed() {
        DecimalFormat formatter = new DecimalFormat("#.#");
        double deltaX = mc.player.posX - mc.player.prevPosX;
        double deltaZ = mc.player.posZ - mc.player.prevPosZ;

        double KMH = MathsUtil.roundAvoid((MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) / 1000.0f) / (0.05f / 3600.0f), 1);

        String formattedString = formatter.format(KMH);

        if (!formattedString.contains("."))
            formattedString += ".0";

        return formattedString + " km/h";
    }

    @Override
    public void renderComponent() {
        renderString = "Speed: " + getSpeed();
        if (customFont.getValue())
            WurstplusThree.GUI_FONT_MANAGER.drawString(renderString , getX() + 2, getY() + 2, color.getValue().hashCode(), false);
        else
            mc.fontRenderer.drawString(renderString , getX() + 2, getY() + 3, color.getValue().hashCode());
    }
}
