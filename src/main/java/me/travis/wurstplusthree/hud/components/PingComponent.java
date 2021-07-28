package me.travis.wurstplusthree.hud.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Colour;

@HudComponent.Registration(name = "Ping")
public class PingComponent extends HudComponent {
    private ColourSetting color = new ColourSetting("Color", new Colour(30, 200, 100), this);
    private String renderString;

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public int getWidth() {
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(renderString) + 5;
    }

    @Override
    public void renderComponent() {
        renderString = "Ping: " + (!mc.isSingleplayer() ? mc.getConnection().getPlayerInfo(mc.player.getUniqueID()).getResponseTime() : -1);
        WurstplusThree.GUI_FONT_MANAGER.drawString(renderString , getX() + 2, getY() + 3, color.getValue().hashCode(), false);
    }
}
