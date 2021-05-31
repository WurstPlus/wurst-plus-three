package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class ModeComponent extends Component {

    private boolean hovered;
    private HackButton parent;
    private EnumSetting set;
    private int offset;
    private int x;
    private int y;
    private Hack mod;

    private int modeIndex;

    public ModeComponent(EnumSetting set, HackButton button, Hack mod, int offset){
        this.set = set;
        this.parent = button;
        this.mod = mod;
        this.offset = offset;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.modeIndex = 0;
        setShown(true);
    }
    @Override
    public void renderComponent(int mouseX, int mouseY) {
        if(!isShown())return;
        RenderUtil2D.drawRectMC(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.hovered ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_COLOR());
        // RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset,WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getValue().hashCode());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(set.getName() + ": " + set.getValue(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(set.getName() + ": " + set.getValue(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
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
        setShown(this.set.isShown());
        if(old != isShown()){
            this.parent.init(parent.mod, parent.parent, parent.offset, true);
        }
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(!isShown())return;
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
            int maxIndex = set.getModes().size();

            if (modeIndex + 1 > maxIndex)
                modeIndex = 0;
            else
                modeIndex++;

            try {
                set.setValue(set.getModes().get(modeIndex));
            } catch (Exception e) {
                modeIndex = 0;
                set.setValue(set.getModes().get(modeIndex));
            }
        }

        if (isMouseOnButton(mouseX, mouseY) && button == 1 && this.parent.isOpen) {
            int maxIndex = set.getModes().size();

            if (modeIndex == 0)
                modeIndex = maxIndex - 1;
            else
                modeIndex--;

            try {
                set.setValue(set.getModes().get(modeIndex));
            } catch (Exception e) {
                modeIndex = 0;
                set.setValue(set.getModes().get(modeIndex));
            }
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
