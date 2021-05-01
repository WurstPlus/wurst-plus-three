package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.MathsUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author megyn
 * @since 29/04/2021
 */

public class SliderComponent extends Component {
    private boolean hovered;

    private DoubleSetting setD = null;
    private IntSetting setI = null;
    private HackButton parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;

    private double renderWidth;

    public SliderComponent(DoubleSetting value, HackButton button, int offset) {
        this.setD = value;
        this.parent = button;
        this.offset = offset;

        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    public SliderComponent(IntSetting value, HackButton button, int offset) {
        this.setI = value;
        this.parent = button;
        this.offset = offset;

        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    @Override
    public void renderComponent() {
        // Draw background box
        net.minecraft.client.gui.Gui.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING, this.hovered ? WurstplusGuiNew.GUI_HOVERED_TRANSPARENCY : WurstplusGuiNew.GUI_TRANSPARENCY);

        // Draw slider
        RenderUtil2D.drawGradientRect(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING,
                (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()),
                (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()));

        // RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset, WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getValue().hashCode());

        // Draw slider name and value
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isInt() ? this.setI.getName() + " " + ChatFormatting.GRAY + MathsUtil.round(this.setI.getValue(), 2) : this.setD.getName() + " " + ChatFormatting.GRAY + this.setD.getValue(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, Gui.INSTANCE.fontColor.getValue().hashCode());
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

        int widthTest = WurstplusGuiNew.WIDTH - (WurstplusGuiNew.SETTING_WIDTH_OFFSET * 2);
        double diff = Math.min(widthTest, Math.max(0, mouseX - this.x));
        if (isInt()) {
            int min = setI.getMin();
            int max = setI.getMax();

            renderWidth = (widthTest) * (float)(setI.getValue() - min) / (max - min) + WurstplusGuiNew.SETTING_WIDTH_OFFSET;

            if (dragging) {
                if (diff == 0) {
                    setI.setValue(setI.getMin());
                } else {
                    int newValue = (int) roundToPlace(((diff / widthTest) * (max - min) + min), 2);
                    setI.setValue(newValue);
                }
            }
        } else {

            double min = setD.getMin();
            double max = setD.getMax();

            renderWidth = (widthTest) * (setD.getValue() - min) / (max - min) + WurstplusGuiNew.SETTING_WIDTH_OFFSET;

            if (dragging) {
                if (diff == 0) {
                    setD.setValue(setD.getMin());
                } else {
                    double newValue = roundToPlace(((diff / widthTest) * (max - min) + min), 2);
                    setD.setValue(newValue);
                }
            }
        }
    }
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        //WurstplusThree.LOGGER.info("D " + isMouseOnButtonD(mouseX, mouseY) + " I " + isMouseOnButtonI(mouseX, mouseY));
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }


    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }



    public boolean isMouseOnButtonD(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_WIDTH_OFFSET && x < this.x + (parent.parent.getWidth() / 2 + 1) - WurstplusGuiNew.SETTING_WIDTH_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + parent.parent.getWidth() / 2 + WurstplusGuiNew.SETTING_WIDTH_OFFSET && x < this.x + parent.parent.getWidth() - WurstplusGuiNew.SETTING_WIDTH_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_WIDTH_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
    }

    public boolean isInt() {
        return setI != null;
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
