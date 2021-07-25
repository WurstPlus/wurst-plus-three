package me.travis.wurstplusthree.gui.component.components;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.component.HackButton;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.MathsUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.input.Mouse;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 * fat cock
 */

public class SliderComponent extends Component {
    private DoubleSetting setD = null;
    private IntSetting setI = null;
    private int x;
    private int y;
    private boolean isChild;

    private boolean dragging = false;

    private double renderWidth;

    public SliderComponent(DoubleSetting value) {
        super(value);
        this.isChild = value.isChild();
        this.setD = value;
    }

    public SliderComponent(IntSetting value) {
        super(value);
        this.isChild = value.isChild();
        this.setI = value;
    }

    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        if (!Mouse.isButtonDown(0))
            dragging = false;
        int widthTest = WurstplusGuiNew.WIDTH - (WurstplusGuiNew.SETTING_OFFSET * 2);
        double diff = Math.min(widthTest, Math.max(0, mouseX - this.x));
        if (isInt()) {
            int min = setI.getMin();
            int max = setI.getMax();

            renderWidth = (widthTest) * (float)(setI.getValue() - min) / (max - min) + WurstplusGuiNew.SETTING_OFFSET;

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

            renderWidth = (widthTest) * (setD.getValue() - min) / (max - min) + WurstplusGuiNew.SETTING_OFFSET;

            if (dragging) {
                if (diff == 0) {
                    setD.setValue(setD.getMin());
                } else {
                    double newValue = roundToPlace(((diff / widthTest) * (max - min) + min), 2);
                    setD.setValue(newValue);
                }
            }
        }
        net.minecraft.client.gui.Gui.drawRect(x + WurstplusGuiNew.SETTING_OFFSET, y  , x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y  + WurstplusGuiNew.HEIGHT , isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : isChild ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR());
        RenderUtil2D.drawGradientRect(x + WurstplusGuiNew.SETTING_OFFSET, y  , x + (int) renderWidth, y  + WurstplusGuiNew.HEIGHT ,
                (Gui.INSTANCE.buttonColor.getValue().hashCode()),
                (Gui.INSTANCE.buttonColor.getValue().hashCode()), isMouseOnButton(mouseX, mouseY));
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isInt() ? this.setI.getName() + " " + ChatFormatting.GRAY + MathsUtil.round(this.setI.getValue(), 2) : this.setD.getName() + " " + ChatFormatting.GRAY + this.setD.getValue(), x + WurstplusGuiNew.SUB_FONT_SIZE, y  + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(isInt() ? this.setI.getName() + " " + ChatFormatting.GRAY + MathsUtil.round(this.setI.getValue(), 2) : this.setD.getName() + " " + ChatFormatting.GRAY + this.setD.getValue(), x + WurstplusGuiNew.SUB_FONT_SIZE, y  + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0) {
            dragging = true;
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0) {
            dragging = true;
        }
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
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + (WurstplusGuiNew.WIDTH / 2 + 1) - WurstplusGuiNew.SETTING_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + WurstplusGuiNew.WIDTH / 2 + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y   && y < this.y  + WurstplusGuiNew.HEIGHT ;
    }

    public boolean isInt() {
        return setI != null;
    }
}
