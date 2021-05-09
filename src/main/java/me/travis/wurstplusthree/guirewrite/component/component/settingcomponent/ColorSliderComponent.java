package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class ColorSliderComponent extends Component {
    private boolean hovered;
    private HackButton parent;
    private ColorComponent p2;
    private int offset;
    private int x;
    private int y;
    private String cName;
    private int cValue;
    private boolean dragging = false;

    private double renderWidth;

    public ColorSliderComponent(HackButton button, int offset, String cName, int cValue, ColorComponent p2) {
        this.parent = button;
        this.offset = offset;
        this.cName = cName;
        this.cValue = cValue;
        this.p2 = p2;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    @Override
    public void renderComponent() {
        net.minecraft.client.gui.Gui.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.hovered ? WurstplusGuiNew.GUI_HOVERED_COLOR : WurstplusGuiNew.GUI_COLOR);
        RenderUtil2D.drawGradientRect(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET,
                (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()),
                (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()));

        // RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET + 1, parent.parent.getY() + offset, WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getValue().hashCode());
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.cName + " " + ChatFormatting.GRAY + this.cValue, parent.parent.getX() + WurstplusGuiNew.COLOR_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
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

        int widthTest = WurstplusGuiNew.WIDTH - (WurstplusGuiNew.SETTING_OFFSET * 2);
        double diff = Math.min(widthTest, Math.max(0, mouseX - this.x));
        int min = 0;
        int max = 255;

        renderWidth = (widthTest) * (float) (cValue - min) / (max - min) + WurstplusGuiNew.SETTING_OFFSET;

        if (dragging) {
            if (diff == 0) {
                cValue = 0;
            } else {
                int newValue = (int) roundToPlace(((diff / widthTest) * (max - min) + min), 2);
                cValue  = newValue;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.isOpen && this.p2.isOpen()) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.isOpen && this.p2.isOpen()) {
            dragging = true;
        }
    }

    public boolean isMouseOnButtonD(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + (parent.parent.getWidth() / 2 + 1) - WurstplusGuiNew.SETTING_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + parent.parent.getWidth() / 2 + WurstplusGuiNew.SETTING_OFFSET && x < this.x + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public HackButton getParent() {
        return parent;
    }

    public int getValue(){
        return this.cValue;
    }

    @Override
    public int getOffset() {
        return offset;
    }

}
