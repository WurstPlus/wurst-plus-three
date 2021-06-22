package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;

/**
 * @author Madmegsox1
 * @since 22/06/2021
 */

@HudElement.Element(name = "Clock", posX = 100, posY = 100)
public class HudClock extends HudElement {

    String text = "";

    @Override
    public int getWidth(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(text);
    }

    @Override
    public void onRender2D(Render2DEvent event){
        text = HudUtil.getAnaTimeLine() + " | " + HudUtil.getDate();
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(text, this.getX(), this.getY(), HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }
}
