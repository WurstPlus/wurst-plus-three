package me.travis.wurstplusthree.gui.component.components;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.MathsUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Timer;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import scala.Char;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SliderComponent extends Component {
    private DoubleSetting setD = null;
    private IntSetting setI = null;
    private final Timer timer = new Timer();
    private int x;
    private int y;
    private double y2;
    private boolean isChild;
    private boolean open;
    private boolean dragging = false;
    private boolean typing;
    private String typeString;
    private double renderWidth;

    public SliderComponent(DoubleSetting value) {
        super(value);
        this.isChild = value.isChild();
        this.setD = value;
        this.open = value.isOpen();
    }

    public SliderComponent(IntSetting value) {
        super(value);
        this.isChild = value.isChild();
        this.setI = value;
        this.open = value.isOpen();
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

            renderWidth = (widthTest) * (float) (setI.getValue() - min) / (max - min) + WurstplusGuiNew.SETTING_OFFSET;

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
        net.minecraft.client.gui.Gui.drawRect(x + WurstplusGuiNew.SETTING_OFFSET, y, x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT, isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : isChild ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR());
        RenderUtil2D.drawGradientRect(x + WurstplusGuiNew.SETTING_OFFSET, y, x + (int) renderWidth, y + WurstplusGuiNew.HEIGHT,
                (Gui.INSTANCE.buttonColor.getValue().hashCode()),
                (Gui.INSTANCE.buttonColor.getValue().hashCode()), isMouseOnButton(mouseX, mouseY));
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isInt() ? this.setI.getName() + " " + ChatFormatting.GRAY + setI.getValue().toString() : this.setD.getName() + " " + ChatFormatting.GRAY + this.setD.getValue(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(isInt() ? this.setI.getName() + " " + ChatFormatting.GRAY + setI.getValue().toString() : this.setD.getName() + " " + ChatFormatting.GRAY + this.setD.getValue(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        if (!open) {
            RenderUtil2D.drawRect(x + 107, y + 5, x + 107 + 1.5f, y + 5 + 1.5f, -1);
            RenderUtil2D.drawRect(x + 107, y + 7.25f, x + 107 + 1.5f, y + 7.25f + 1.5f, -1);
            RenderUtil2D.drawRect(x + 107, y + 9.5f, x + 107 + 1.5f, y + 9.5f + 1.5f, -1);
        } else {
            RenderUtil2D.drawRect(x + 104.75f, y + 7.25f, x + 104.75f + 1.5f, y + 7.25f + 1.5f, -1);
            RenderUtil2D.drawRect(x + 107, y + 7.25f, x + 107 + 1.5f, y + 7.25f + 1.5f, -1);
            RenderUtil2D.drawRect(x + 109.25f, y + 7.25f, x + 109.25f + 1.5f, y + 7.25f + 1.5f, -1);
        }
        boolean didScissor = false;
        if (y2 != 0) {
            y2 = Math.max(y2 - Gui.INSTANCE.animation.getValue(), 0);
            GL11.glScissor(x * 2, (WurstplusThree.GUI2.height - y - WurstplusGuiNew.HEIGHT - getHeight()) * 2, WurstplusGuiNew.WIDTH * 2, getHeight() * 2);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            didScissor = true;
        }
        if (open || y2 != 0) {
            RenderUtil2D.drawBorderedRect(x + WurstplusGuiNew.SETTING_OFFSET, y + (open ? -y2 : y2 - WurstplusGuiNew.HEIGHT) + WurstplusGuiNew.HEIGHT, x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + (open ? -y2 : y2 - WurstplusGuiNew.HEIGHT) + WurstplusGuiNew.HEIGHT * 2, 2, new Color(50, 50, 50, 250).hashCode(), (isChild ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR()), isMouseOnButton(mouseX, mouseY - WurstplusGuiNew.HEIGHT));
            if (Gui.INSTANCE.customFont.getValue()) {
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(typing ? typeString + getCaret() : (isInt() ? ChatFormatting.GRAY + setI.getValue().toString() : ChatFormatting.GRAY + this.setD.getValue().toString()), x + WurstplusGuiNew.SUB_FONT_SIZE, (float) (y + (open ? -y2 : y2 - WurstplusGuiNew.HEIGHT) + 5 + WurstplusGuiNew.HEIGHT), Gui.INSTANCE.fontColor.getValue().hashCode());
            } else {
                mc.fontRenderer.drawStringWithShadow(typing ? typeString + getCaret() : (isInt() ? ChatFormatting.GRAY + setI.getValue().toString() : ChatFormatting.GRAY + this.setD.getValue().toString()), x + WurstplusGuiNew.SUB_FONT_SIZE, (float) (y + (open ? -y2 : y2 - WurstplusGuiNew.HEIGHT) + 3 + WurstplusGuiNew.HEIGHT), Gui.INSTANCE.fontColor.getValue().hashCode());
            }
        }
        if (didScissor)
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public int getHeight() {
        if (open) {
            return (int) (WurstplusGuiNew.HEIGHT * 2 - y2);
        }
        return (int) (WurstplusGuiNew.HEIGHT + y2);
    }

    @Override
    public void onClose() {
        this.typing = false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY - WurstplusGuiNew.HEIGHT) && button == 0) {
            typing = true;
            typeString = isInt() ? setI.getValue().toString() : setD.getValue().toString();
        } else {
            typing = false;
        }
        if (isMouseOnButton(mouseX, mouseY)) {
            if (button == 0) {
                dragging = true;
            } else if (button == 1) {
                if (setD == null) {
                    setI.setOpen(!setI.isOpen());
                } else {
                    setD.setOpen(!setD.isOpen());
                }
                open = !open;
                y2 = WurstplusGuiNew.HEIGHT - y2;
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (!typing) return;
        if (Character.isDigit(typedChar) || key == 52) {
            typeString += typedChar;
        } else if (key == 14) {
            typeString = StringUtils.chop(typeString);
        } else if (key == 28) {
            typing = false;
            if (isInt()) {
                this.setI.setValue(Math.max(setI.getMin(), Math.min(Integer.parseInt(typeString), setI.getMax())));
            } else {
                this.setD.setValue(Math.max(setD.getMin(), Math.min(Double.parseDouble(typeString), setD.getMax())));
            }
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

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public boolean isInt() {
        return setI != null;
    }

    public String getCaret() {
        if (this.timer.passedMs(1000L)) {
            timer.reset();
        }
        if (timer.passedMs(500l)) {
            return "_";
        }
        return "";
    }
}
