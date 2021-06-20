package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;

/**
 * @author Madmegsox1
 * @since 20/06/2021
 */

@HudElement.Element(name = "Coords",posX = 50, posY = 50)
public class HudCoords extends HudElement {
    public int length;
    public String text;

    @Override
    public int getHeight(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
    }

    @Override
    public int getWidth(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(text);
    }

    @Override
    public void onRender2D(Render2DEvent event){

        String x = "[" + (int) (mc.player.posX) + "]";
        String y = "[" + (int) (mc.player.posY) + "]";
        String z = "[" + (int) (mc.player.posZ) + "]";

        String x_nether = "[" + Math.round(mc.player.dimension != -1 ? (mc.player.posX / 8) : (mc.player.posX * 8)) + "]";
        String z_nether = "[" + Math.round(mc.player.dimension != -1 ? (mc.player.posZ / 8) : (mc.player.posZ * 8)) + "]";
        text = "XYZ " + x + y + z + " XZ " + x_nether + z_nether;

        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(text, this.getX(), this.getY(), HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }

}
