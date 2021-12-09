package me.travis.wurstplusthree.gui.hud.element.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Rainbow;

@HudElement.Element(name = "Clout Manager", posX = 100, posY = 100)
public class HudCloutManager extends HudElement {
    String text = "";

    @Override
    public int getHeight() {
        return HudUtil.getHudStringHeight();
    }

    @Override
    public int getWidth() {
        return HudUtil.getHudStringWidth(text);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int padding = 5;
        Colour fill = new Colour(77, 77, 77, 255);
        Colour outline = new Colour(127, 127, 127, 255);
        RenderUtil2D.drawBorderedRect(this.getX() - padding, this.getY() - padding,
                this.getX() + padding + this.getWidth(), this.getY() + padding + this.getHeight() - 1, 1, fill.hashCode(), outline.hashCode(), false);
        RenderUtil2D.drawHLineG(this.getX() - padding, this.getY() - padding,
                (this.getX() + padding + this.getWidth()) - (this.getX() - padding), Rainbow.getColour().hashCode(), Rainbow.getFurtherColour(HudEditor.INSTANCE.welcomerOffset.getValue()).hashCode());
        text = ChatFormatting.AQUA + "Clout" + ChatFormatting.RESET + "Manager";
        if (HudEditor.INSTANCE.kills.getValue()) {
            text += " | Kills: " + WurstplusThree.KD_MANAGER.getTotalKills();
        }
        if (HudEditor.INSTANCE.deaths.getValue()) {
            text += " | Deaths: " + WurstplusThree.KD_MANAGER.getTotalDeaths();
        }
        if (HudEditor.INSTANCE.kd.getValue()) {
            text += " | KD: " + WurstplusThree.KD_MANAGER.getKD();
        }
        if (HudEditor.INSTANCE.streak.getValue()) {
            text += " | Streak: " + WurstplusThree.KD_MANAGER.getCurrentKills();
        }

        HudUtil.drawHudString(text, getX(), getY(), HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }
}
