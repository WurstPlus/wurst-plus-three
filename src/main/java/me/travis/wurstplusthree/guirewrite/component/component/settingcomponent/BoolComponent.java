package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.GuiRewrite;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class BoolComponent extends Component {
    private boolean hovered;
    private BooleanSetting option;
    private HackButton parent;
    private int offset;
    private int x;
    private int y;

    public BoolComponent(BooleanSetting option, HackButton button, int offset) {
        this.option = option;
        this.parent = button;
        this.offset = offset;

        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    @Override
    public void renderComponent() {
        if (this.option.getValue()) {
            RenderUtil2D.drawGradientRect(parent.parent.getX() + WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING,
                    (GuiRewrite.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, GuiRewrite.INSTANCE.buttonColor.getColor().getAlpha()).hashCode() : GuiRewrite.INSTANCE.buttonColor.getColor().hashCode()),
                    (GuiRewrite.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(GuiRewrite.INSTANCE.rainbowDelay.getValue(), GuiRewrite.INSTANCE.buttonColor.getColor().getAlpha()).hashCode() : GuiRewrite.INSTANCE.buttonColor.getColor().hashCode()));
        } else {
            WurstplusGuiNew.drawRect(parent.parent.getX() + WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING, this.hovered ? WurstplusGuiNew.GUI_HOVERED_TRANSPARENCY : WurstplusGuiNew.GUI_TRANSPARENCY);
        }
        RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset,WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getColor().hashCode());
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.option.getName(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, GuiRewrite.INSTANCE.fontColor.getColor().hashCode());
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
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
            this.option.setValue(!option.getValue());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.MODULE_WIDTH_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
    }
}
