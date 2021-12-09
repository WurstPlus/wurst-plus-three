package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.component.HackButton;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.ParentSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.awt.*;

public class ParentComponent extends Component {
    private boolean hovered;
    private ParentSetting option;
    private final HackButton parent;
    private int offset;
    private int x;
    private int y;

    public ParentComponent(ParentSetting option, HackButton button, int offset) {
        this.option = option;
        this.parent = button;
        this.offset = offset;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.setShown(true);
    }


    @Override
    public void renderComponent(int mouseX, int mouseY) {
        if(!this.isShown())return;
        RenderUtil2D.drawGradientRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET - 2, this.hovered ? Gui.INSTANCE.groupHoverColor.getValue().hashCode(): Gui.INSTANCE.groupColor.getValue().hashCode(), this.hovered ? Gui.INSTANCE.groupHoverColor.getValue().hashCode(): Gui.INSTANCE.groupColor.getValue().hashCode(), isMouseOnButton(mouseX, mouseY));
        WurstplusGuiNew.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET + WurstplusGuiNew.HEIGHT - 2, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.hovered ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_CHILDBUTTON());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.option.getName(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow((this.option.getValue() ? "-" : "+"), parent.parent.getX() + 90 + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(this.option.getName(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
            mc.fontRenderer.drawStringWithShadow((this.option.getValue() ? "-" : "+"), parent.parent.getX() + 90 + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
        boolean old = isShown();
        setShown(this.option.isShown());
        if(old != isShown()){
            this.parent.init(parent.mod, parent.parent, parent.offset, true);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(!isShown())return;
        if (isMouseOnButton(mouseX, mouseY) && (button == 0 || button == 1) && this.parent.isOpen) {
            this.option.toggle();
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
    }

    @Override
    public HackButton getParent() {
        return parent;
    }

    @Override
    public int getOffset() {
        return offset;
    }
}
