package me.travis.wurstplusthree.gui.component;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public class CategoryComponent implements Globals {
    public ArrayList<Component> components;
    public Hack.Category category;
    private final int width;
    private final int height;
    public int x;
    public int y;
    public boolean isOpen;
    private boolean isDragging;
    public int dragX;
    public int dragY;

    public CategoryComponent(Hack.Category cat) {
        this.category = cat;
        this.components = new ArrayList<>();
        this.width = WurstplusGuiNew.WIDTH;
        this.height = WurstplusGuiNew.HEIGHT;
        this.x = 5;
        this.y = 5;
        this.dragX = 0;
        this.isOpen = true;
        this.isDragging = false;

        int tY = this.height;

        for (Hack mod : WurstplusThree.HACKS.getHacksAlp()) {
            if (mod.getCategory().equals(category)) {
                HackButton moduleButton = new HackButton();
                moduleButton.init(mod, this, tY, false);
                this.components.add(moduleButton);
                tY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
            }
        }
    }

    public float animationValue = 0;

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public void renderFrame(int mouseX, int mouseY) {
        RenderUtil2D.drawGradientRect(this.x + 4, this.y, this.x + width - 5, this.y + height, (Gui.INSTANCE.headButtonColor.getValue().hashCode()), (Gui.INSTANCE.headButtonColor.getValue().hashCode()), false);
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(category.getName(), this.x + WurstplusGuiNew.MODULE_FONT_SIZE, this.y + (this.height / 2f) - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(category.getName(), this.x + WurstplusGuiNew.MODULE_FONT_SIZE, this.y + (this.height / 2f) - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        if (this.isOpen) {
            if (!this.components.isEmpty()) {
                int x = 0;
                for (Component component : components) {
                    component.renderComponent(mouseX, mouseY);
                    x++;
                    if (component instanceof HackButton) {
                        if (((HackButton) component).isOpen) {
                            x += ((HackButton) component).subCompLength;
                        }
                    }
                }
                x *= WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                switch (Gui.INSTANCE.type.getValue()) {
                    case "Sin":
                        ColorUtil.type type = ColorUtil.type.SPECIAL;
                        switch (Gui.INSTANCE.SinMode.getValue()){
                            case "Special":
                                type = ColorUtil.type.SPECIAL;
                                break;
                            case "Hue":
                                type = ColorUtil.type.HUE;
                                break;
                            case "Saturation":
                                type = ColorUtil.type.SATURATION;
                                break;
                            case "Brightness":
                                type = ColorUtil.type.BRIGHTNESS;
                                break;
                        }
                        RenderUtil2D.drawVLineG(this.x + 4, this.y + 1 + WurstplusGuiNew.HEIGHT - 1, x,
                                ColorUtil.getSinState(Gui.INSTANCE.buttonColor.getColor(), 1000, 255, type).hashCode(),
                                ColorUtil.getSinState(Gui.INSTANCE.buttonColor.getColor(), Gui.INSTANCE.rainbowDelay.getValue(), 255, type).hashCode());
                        break;
                    case "Rainbow":
                        RenderUtil2D.drawVLineG(this.x + 4, this.y + 1 + WurstplusGuiNew.HEIGHT - 1, x,
                                ColorUtil.releasedDynamicRainbow(0, 255).hashCode(),
                                ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), 255).hashCode());
                        break;
                    case "None":
                        RenderUtil2D.drawVLine(this.x + 4, this.y + 1 + WurstplusGuiNew.HEIGHT - 1, x, Gui.INSTANCE.buttonColor.getValue().hashCode());
                        break;
                }
            }
        }
    }

    public void refresh() {
        int off = this.height;
        for (Component comp : components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public int getY() {
        return y;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public int getWidth() {
        return width;
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - dragX);
            this.setY(mouseY - dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
        if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
            return true;
        }
        return false;
    }
}