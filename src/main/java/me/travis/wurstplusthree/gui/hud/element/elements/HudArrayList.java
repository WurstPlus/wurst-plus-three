package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;

@HudElement.Element(name = "Array List", posX = 50, posY = 50)
public class HudArrayList extends HudElement {
    @Override
    public int getWidth(){
        return 0;
    }

    @Override
    public int getHeight(){
        return 0;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        List<Hack> hacks = WurstplusThree.HACKS.getSortedHacks(false, true);
        int y = scaledResolution.getScaledHeight() - (11 * hacks.size()) + 2 + (11 * WurstplusThree.HACKS.getDrawnHacks().size());
        for (Hack hack : hacks) {
            if (WurstplusThree.HACKS.isDrawHack(hack)) continue;
            String name = hack.getFullArrayString();
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(name, this.getRightX(name, 2, scaledResolution), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
            y += 11;
        }
    }

    private int getRightX(String string, int x, ScaledResolution scaledResolution) {
        return scaledResolution.getScaledWidth()- x - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string);
    }

}
