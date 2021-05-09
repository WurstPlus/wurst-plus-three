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

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class BoolComponent extends Component {
    private final boolean rainbow;
    private ColorComponent p;
    private boolean hovered;
    private BooleanSetting option;
    private ColourSetting coption;
    private final HackButton parent;
    private int offset;
    private int x;
    private int y;

    public BoolComponent(BooleanSetting option, HackButton button, int offset) {
        this.option = option;
        this.parent = button;
        this.offset = offset;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;

        this.rainbow = false;
    }

    public BoolComponent(ColourSetting setting, HackButton button, int offset, ColorComponent p) {
        this.coption = setting;
        this.parent = button;
        this.offset = offset;
        this.option = null;
        this.p = p;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;

        this.rainbow = true;
    }


    @Override
    public void renderComponent() {
        if (this.option == null) {
            if (this.coption.getRainbow()) {
                RenderUtil2D.drawGradientRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET,
                        (ColorUtil.releasedDynamicRainbow(0, Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode()),
                        (ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode())
                );
            } else {
                WurstplusGuiNew.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.hovered ? WurstplusGuiNew.GUI_HOVERED_COLOR : WurstplusGuiNew.GUI_COLOR);
            }
        } else {
            if (this.option.getValue()) {
                RenderUtil2D.drawGradientRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET,
                        (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()),
                        (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode())
                );
            } else {
                WurstplusGuiNew.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.hovered ? WurstplusGuiNew.GUI_HOVERED_COLOR : WurstplusGuiNew.GUI_COLOR);
            }
        }
        // RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset,WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getValue().hashCode());
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow((this.option != null ? this.option.getName() : "rainbow"), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
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
            if (rainbow) {
                if(p.isOpen()) {
                    this.coption.setRainbow(!this.coption.getRainbow());
                }
            } else {
                this.option.setValue(!option.getValue());
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
