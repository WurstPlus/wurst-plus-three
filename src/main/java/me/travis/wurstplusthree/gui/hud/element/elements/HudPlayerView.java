package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.util.RenderUtil2D;

@HudElement.Element(name = "Player View", posY = 100, posX = 100)
public class HudPlayerView extends HudElement{

    @Override
    public int getHeight(){
        return 80;
    }

    @Override
    public int getWidth(){
        return 40;
    }

    @Override
    public void onRender2D(Render2DEvent event){
        RenderUtil2D.drawEntityOnScreen(getX() + (getWidth() / 2f), getY() + getHeight(), 40, mc.player);
    }
}
