package me.travis.wurstplusthree.gui.hud.element.elements;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;

/**
 * @author Madmegsox1
 * @since 20/06/2021
 */

@HudElement.Element(name = "Coords",posX = 50, posY = 50)
public class HudCoords extends HudElement {

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

        String x = "[" + (int) (mc.player.posX) + "]";
        String y = "[" + (int) (mc.player.posY) + "]";
        String z = "[" + (int) (mc.player.posZ) + "]";

        String x_nether = "[" + Math.round(mc.player.dimension != -1 ? (mc.player.posX / 8) : (mc.player.posX * 8)) + "]";
        String z_nether = "[" + Math.round(mc.player.dimension != -1 ? (mc.player.posZ / 8) : (mc.player.posZ * 8)) + "]";
        text = "XYZ " + x + y + z + " XZ " + x_nether + z_nether;

        HudUtil.drawHudString(text, this.getX(), this.getY(), HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }

}
