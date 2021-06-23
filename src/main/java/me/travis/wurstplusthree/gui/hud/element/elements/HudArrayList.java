package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;

@HudElement.Element(name = "Array List", posX = 50, posY = 50)
public class HudArrayList extends HudElement {
    int width = 0;
    int height = 0;

    @Override
    public int getWidth(){
        return width;
    }

    @Override
    public int getHeight(){
        return height;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        List<Hack> hacks = WurstplusThree.HACKS.getSortedHacks(false, true);
        int y = scaledResolution.getScaledHeight() - (11 * hacks.size()) + 2 + (11 * WurstplusThree.HACKS.getDrawnHacks().size());
        for (Hack hack : hacks) {
            if (WurstplusThree.HACKS.isDrawHack(hack)) continue;
            String name = hack.getFullArrayString();
            HudUtil.drawHudString(name, this.getRightX(name, scaledResolution), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
            y += 11;
        }
    }

    private int getRightX(String string, ScaledResolution scaledResolution) {
        return scaledResolution.getScaledWidth() - 2 - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string);
    }

}
