package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;

import java.awt.*;

@HudElement.Element(name = "Array List", posX = 50, posY = 50)
public class HudArrayList extends HudElement {

    @Override
    public int getWidth(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth("Test");
    }

    @Override
    public int getHeight(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Test", this.getX(), this.getY(), new Color(255,255,255).hashCode());
    }

}
