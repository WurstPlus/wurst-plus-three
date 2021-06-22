package me.travis.wurstplusthree.gui.hud.element.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;

/**
 * @author Madmegsox1
 * @since 22/06/2021
 */
@HudElement.Element(name = "Watermark", posX = 100, posY = 100)
public class HudWatermark extends HudElement{
    String text = "";

    @Override
    public int getWidth(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(text);
    }

    @Override
    public void onRender2D(Render2DEvent event){
        text = ChatFormatting.GOLD + WurstplusThree.MODNAME + ChatFormatting.RESET
                + " v" + WurstplusThree.MODVER;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(text, getX(), getY(), HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }
}
