package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.awt.*;

public class BooleanComponent extends Component {
    private BooleanSetting option;
    private int x;
    private int y;
    private float x2;

    public BooleanComponent(BooleanSetting option) {
        super(option);
        this.option = option;
    }


    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        if (option.getValue() && x2 != 7) {
            x2 = (float) Math.min((x2 + Gui.INSTANCE.animation.getValue() / 3), 7);
        } else if (!option.getValue() && x2 != 0) {
            x2 = (float) Math.max((x2 - Gui.INSTANCE.animation.getValue() / 3), 0);
        }
        WurstplusGuiNew.drawRect(x + WurstplusGuiNew.SETTING_OFFSET, y  , x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT , isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : this.option.isChild() ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR());
        RenderUtil2D.drawBorderedRect(x + WurstplusGuiNew.SETTING_OFFSET + 85, y + 3 , x + 115 - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT  - 3, 1, !this.option.getValue() ? WurstplusGuiNew.GUI_COLOR() : Gui.INSTANCE.buttonColor.getValue().hashCode(), new Color(0, 0, 0, 200).hashCode(), isMouseOnButton(mouseX, mouseY));
        RenderUtil2D.drawRectMC(x + WurstplusGuiNew.SETTING_OFFSET + 88 + (int) x2, y + 5 , x + 105 + (int) x2 - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT  - 5, new Color(50, 50, 50, 255).hashCode());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.option.getName(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(this.option.getName(),x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.option.setValue(!option.getValue());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y  && y < this.y + WurstplusGuiNew.HEIGHT ;
    }
}

