package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;

@HudElement.Element(name = "FPS", posX = 10, posY = 62)
public class HudFps extends HudElement {

    public String text;

    @Override
    public int getHeight() {
        return HudUtil.getHudStringHeight();
    }

    @Override
    public int getWidth(){
        return HudUtil.getHudStringWidth(text);
    }

    @Override
    public void onRender2D(Render2DEvent event){
        text = "Fps " + HudUtil.getFpsLine();
        HudUtil.drawHudString(text, this.getX(), this.getY(), HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }

}
