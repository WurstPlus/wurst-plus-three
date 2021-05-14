package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class BooleanComponent extends Component {
    private ColorComponent p;
    private boolean hovered;
    private BooleanSetting option;
    private final HackButton parent;
    private ColourSetting coption;
    private int offset;
    private int x;
    private int y;

    public BooleanComponent(BooleanSetting option, HackButton button, int offset) {
        this.option = option;
        coption = null;
        this.parent = button;
        this.offset = offset;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    public BooleanComponent(ColourSetting setting, HackButton button, int offset, ColorComponent p) {
        this.coption = setting;
        this.parent = button;
        this.offset = offset;
        this.option = null;
        this.p = p;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }


    @Override
    public void renderComponent() {
        WurstplusGuiNew.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.hovered ? WurstplusGuiNew.GUI_HOVERED_COLOR : WurstplusGuiNew.GUI_COLOR);
        RenderUtil2D.drawBorderedRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET + 85, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + 115 - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET - 3, 1, this.coption == null ? !this.option.getValue() ? WurstplusGuiNew.GUI_COLOR : Gui.INSTANCE.buttonColor.getValue().hashCode() : coption.getRainbow() ? new Color(coption.getValue().getRed(), coption.getValue().getGreen(), coption.getValue().getBlue(), 255).hashCode() : WurstplusGuiNew.GUI_COLOR, new Color(0, 0, 0, 200).hashCode());
        RenderUtil2D.drawRectMC(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET + (this.coption == null ? this.option.getValue() ? 95 : 88 : this.coption.getRainbow() ? 95 : 88), parent.parent.getY() + offset + 5 + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + (this.coption == null ? this.option.getValue() ? 112 : 105: this.coption.getRainbow() ? 112 : 105) - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET - 5, new Color(50, 50, 50, 255).hashCode());
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.coption == null ? this.option.getName() : "Rainbow", parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
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
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (coption == null) {
            if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
                this.option.setValue(!option.getValue());
            }
        } else {
            if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
                this.coption.setRainbow(!coption.getRainbow());
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

