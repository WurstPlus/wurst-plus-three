package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.ColorCopyEvent;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorComponent extends Component {
    private final ColourSetting set;
    private Color finalColor;
    private boolean isOpen;
    private final int booleanButtonOffset = 80;
    boolean pickingColor = false;
    boolean pickingHue = false;
    boolean pickingAlpha = false;
    int x;
    int y;
    private double y2;

    public ColorComponent(ColourSetting value) {
        super(value);
        this.set = value;
        this.isOpen = false;
    }

    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        RenderUtil2D.drawRectMC(this.x + WurstplusGuiNew.SETTING_OFFSET, this.y, this.x + WurstplusGuiNew.SETTING_OFFSET + 95, this.y + WurstplusGuiNew.HEIGHT, Gui.INSTANCE.buttonColor.getValue().hashCode());
        RenderUtil2D.drawRectMC(this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET - 5, this.y, this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, this.y + WurstplusGuiNew.HEIGHT, Gui.INSTANCE.buttonColor.getValue().hashCode());
        RenderUtil2D.drawRectMC(this.x + WurstplusGuiNew.SETTING_OFFSET + 95, this.y, this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET - 5, this.y + 3, Gui.INSTANCE.buttonColor.getValue().hashCode());
        RenderUtil2D.drawRectMC(this.x + WurstplusGuiNew.SETTING_OFFSET + 95, this.y + WurstplusGuiNew.HEIGHT, this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET - 5, this.y + WurstplusGuiNew.HEIGHT - 3, Gui.INSTANCE.buttonColor.getValue().hashCode());
        RenderUtil2D.drawBorderedRect(this.x + WurstplusGuiNew.SETTING_OFFSET + 95, this.y + 3, this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET - 5, this.y + WurstplusGuiNew.HEIGHT - 3, 1, this.set.getValue().hashCode(), this.set.isChild() ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR(), false);
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(set.getName(), this.x + WurstplusGuiNew.SUB_FONT_SIZE, this.y + 3, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(set.getName(), this.x + WurstplusGuiNew.SUB_FONT_SIZE, this.y + 3, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        boolean didScissor = false;
        if (y2 != 0) {
            y2 = Math.max(y2 - Gui.INSTANCE.animation.getValue(), 0);
            GL11.glScissor(x * 2, (WurstplusThree.GUI2.height - WurstplusGuiNew.HEIGHT - y - getHeight()) * 2, WurstplusGuiNew.WIDTH * 2, getHeight() * 2);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            didScissor = true;
        }
        if (isOpen || didScissor) {
            WurstplusGuiNew.drawRect(this.x + WurstplusGuiNew.SETTING_OFFSET, y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + WurstplusGuiNew.HEIGHT, this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + WurstplusGuiNew.HEIGHT + booleanButtonOffset, this.set.isChild() ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR());
            this.drawPicker(set, this.x + 7, this.y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 19, this.x + 100, this.y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 19, this.x + 7, this.y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 72, mouseX, mouseY);
            set.setValue(finalColor);
            RenderUtil2D.drawBorderedRect(this.x + WurstplusGuiNew.SETTING_OFFSET + 85, this.y + (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 4 + booleanButtonOffset, this.x + 115 - WurstplusGuiNew.SETTING_OFFSET, this.y + (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + WurstplusGuiNew.HEIGHT - 2 + booleanButtonOffset, 1, this.set.getRainbow() ? new Color(set.getValue().getRed(), set.getValue().getGreen(), set.getValue().getBlue(), 255).hashCode() : this.set.isChild() ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR(), new Color(0, 0, 0, 200).hashCode(), false);
            RenderUtil2D.drawRectMC(this.x + WurstplusGuiNew.SETTING_OFFSET + (this.set.getRainbow() ? 95 : 88), this.y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 6 + booleanButtonOffset, this.x + (this.set.getRainbow() ? 112 : 105) - WurstplusGuiNew.SETTING_OFFSET, this.y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + WurstplusGuiNew.HEIGHT - 4 + booleanButtonOffset, new Color(50, 50, 50, 255).hashCode());
            if (Gui.INSTANCE.customFont.getValue()) {
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Rainbow", this.x + WurstplusGuiNew.SUB_FONT_SIZE, this.y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 5 + booleanButtonOffset, Gui.INSTANCE.fontColor.getValue().hashCode());
            } else {
                mc.fontRenderer.drawStringWithShadow("Rainbow", this.x + WurstplusGuiNew.SUB_FONT_SIZE, y + (int) (isOpen ? -y2 : y2 - WurstplusGuiNew.HEIGHT * 5) + 5 + booleanButtonOffset, Gui.INSTANCE.fontColor.getValue().hashCode());
            }
            if (didScissor) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        if (isMouseOnButton(mouseX, mouseY) && Gui.INSTANCE.toolTips.getValue()) {
            String hex = String.format("#%02x%02x%02x", set.getValue().getRed(), set.getValue().getGreen(), set.getValue().getBlue());
            String text = "R: " + set.getValue().getRed() + " G: " + set.getValue().getGreen() + " B: " + set.getValue().getBlue() + " A: " + set.getValue().getAlpha() + "  " + hex;
            int length = WurstplusThree.GUI_FONT_MANAGER.getTextWidth(text);
            int height = WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
            RenderUtil2D.drawRectMC(mouseX + 1, mouseY + 4, mouseX + length + 5, mouseY + height + 8, Gui.INSTANCE.toolTipColor.getValue().hashCode());
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(text, mouseX + 3, mouseY + 8, new Color(255, 255, 255).hashCode());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            setOpen(!isOpen);
            y2 = WurstplusGuiNew.HEIGHT * 5 - y2;
        }

        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            WurstplusThree.GUI2.colorClipBoard = set.getValue();
            ColorCopyEvent event = new ColorCopyEvent(set.getValue(), this, ColorCopyEvent.EventType.COPY);
            WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        }

        if (isMouseOnButton(mouseX, mouseY) && button == 2) {
            this.set.setValue(WurstplusThree.GUI2.colorClipBoard);
            ColorCopyEvent event = new ColorCopyEvent(set.getValue(), this, ColorCopyEvent.EventType.PAST);
            WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        }
        if (this.isOpen && y2 == 0 && mouseOver(this.x + WurstplusGuiNew.SETTING_OFFSET + 85, this.y + 4 + booleanButtonOffset, this.x + 115 - WurstplusGuiNew.SETTING_OFFSET, this.y + WurstplusGuiNew.HEIGHT - 2 + booleanButtonOffset, mouseX, mouseY)) {
            this.set.setRainbow(!this.set.getRainbow());
        }
    }

    public void drawPicker(ColourSetting subColor, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float[] color = new float[]{
                0, 0, 0, 0
        };

        try {
            color = new float[]{
                    Color.RGBtoHSB(subColor.getColor().getRed(), subColor.getColor().getGreen(), subColor.getColor().getBlue(), null)[0], Color.RGBtoHSB(subColor.getColor().getRed(), subColor.getColor().getGreen(), subColor.getColor().getBlue(), null)[1], Color.RGBtoHSB(subColor.getColor().getRed(), subColor.getColor().getGreen(), subColor.getColor().getBlue(), null)[2], subColor.getColor().getAlpha() / 255f
            };
        } catch (Exception ignored) {

        }

        int pickerWidth = 90;
        int pickerHeight = 51;
        int hueSliderWidth = 10;
        int hueSliderHeight = 59;
        int alphaSliderHeight = 10;
        int alphaSliderWidth = 90;
        if (!pickingColor && !pickingHue && !pickingAlpha) {
            if (Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY)) {
                pickingColor = true;
            } else if (Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY)) {
                pickingHue = true;
            } else if (Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))
                pickingAlpha = true;
        }

        if (pickingHue) {
            float restrictedY = (float) Math.min(Math.max(hueSliderY, mouseY), hueSliderY + hueSliderHeight);
            color[0] = (restrictedY - (float) hueSliderY) / hueSliderHeight;
            color[0] = (float) Math.min(0.97, color[0]);
        }

        if (pickingAlpha) {
            float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1 - (restrictedX - (float) alphaSliderX) / pickerWidth;
        }

        if (pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
            color[2] = (float) Math.max(0.04000002, color[2]);
            color[1] = (float) Math.max(0.022222223, color[1]);
        }

        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);

        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;

        RenderUtil2D.drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, 255);

        drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, color[0]);

        int cursorX = (int) (pickerX + color[1] * pickerWidth);
        int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);

        RenderUtil2D.drawRectMC(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);

        finalColor = alphaIntegrate(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);

        drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth, alphaSliderHeight, finalColor.getRed() / 255f, finalColor.getGreen() / 255f, finalColor.getBlue() / 255f, color[3]);
    }

    public static Color alphaIntegrate(Color color, float alpha) {
        float red = (float) color.getRed() / 255;
        float green = (float) color.getGreen() / 255;
        float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }

    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtil2D.drawRectMC(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;

            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                RenderUtil2D.drawGradientRect(x, y + step * (height / 6f), x + width, y + (step + 1) * (height / 6f), previousStep, nextStep, false);
                step++;
            }
            int sliderMinY = (int) (y + height * hue) - 4;
            RenderUtil2D.drawRectMC(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                RenderUtil2D.gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                step++;
            }

            int sliderMinX = (int) (x + (width * hue));
            RenderUtil2D.drawRectMC(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        }
    }

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = height / 2;

        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                RenderUtil2D.drawRectMC(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, 0xFFFFFFFF);
                RenderUtil2D.drawRectMC(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, 0xFF909090);

                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    RenderUtil2D.drawRectMC(minX, y, maxX, y + height, 0xFF909090);
                    RenderUtil2D.drawRectMC(minX, y + checkerBoardSquareSize, maxX, y + height, 0xFFFFFFFF);
                }
            }

            left = !left;
        }

        RenderUtil2D.drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        RenderUtil2D.drawRectMC(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
    }

    @Override
    public int getHeight() {
        if (isOpen) {
            return WurstplusGuiNew.HEIGHT * 6 - (int) y2;
        } else return WurstplusGuiNew.HEIGHT + (int) y2;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        pickingColor = false;
        pickingHue = false;
        pickingAlpha = false;
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    public void setOpen(boolean v) {
        this.isOpen = v;
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }
}
