package me.travis.wurstplusthree.gui.hud.element.elements;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.HudEditor;
import me.travis.wurstplusthree.util.HudUtil;

@HudElement.Element(name = "Helper", posX = 100, posY = 100)
public class HudHelper extends HudElement {

    @Override
    public int getHeight(){
        return 6 * 12;
    }

    @Override
    public int getWidth(){
        return WurstplusThree.GUI_FONT_MANAGER.getTextWidth("Surround 0");
    }

    @Override
    public void onRender2D(Render2DEvent event){
        int y = getY();

        String ca = (WurstplusThree.HACKS.ishackEnabled("Crystal Aura") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String holefill = (WurstplusThree.HACKS.ishackEnabled("Hole Fill") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String trap = (WurstplusThree.HACKS.ishackEnabled("Trap") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String surround = (WurstplusThree.HACKS.ishackEnabled("Surround") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String ka = (WurstplusThree.HACKS.ishackEnabled("Kill Aura") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Totems " + HudUtil.getTotems(), getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
        y += 12;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("CAura " + ca, getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
        y += 12;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("HoleFill " + holefill, getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
        y += 12;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Trap " + trap, getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
        y += 12;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Surround " + surround, getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
        y += 12;
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("KAura " + ka, getX(), y, HudEditor.INSTANCE.fontColor.getValue().hashCode());
    }
}
