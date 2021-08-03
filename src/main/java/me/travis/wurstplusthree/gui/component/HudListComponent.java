package me.travis.wurstplusthree.gui.component;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.util.ArrayList;

public class HudListComponent implements Globals {
    public ArrayList<Component> components;
    private final int width;
    private final int height;
    public int x;
    public int y;
    public boolean isOpen;
    private boolean isDragging;
    public int dragX;
    public int dragY;

    public HudListComponent() {
        this.components = new ArrayList<>();
        this.width = WurstplusGuiNew.WIDTH;
        this.height = WurstplusGuiNew.HEIGHT;
        this.x = 5;
        this.y = 5;
        this.dragX = 0;
        this.isOpen = true;
        this.isDragging = false;
        for (HudComponent component : WurstplusThree.HUD.getComponents()) {
            HackButton moduleButton = new HackButton(component);
            this.components.add(moduleButton);
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
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Components", this.x + WurstplusGuiNew.MODULE_FONT_SIZE, this.y + (this.height / 2f) - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow("Components", this.x + WurstplusGuiNew.MODULE_FONT_SIZE, this.y + (this.height / 2f) - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        if (this.isOpen) {
            if (!this.components.isEmpty()) {
                int offset = this.height;
                for (Component component : components) {
                    component.renderComponent(mouseX, mouseY, x, y + offset);
                    offset = offset + component.getHeight();
                }
                switch (Gui.INSTANCE.type.getValue()) {
                    case "Sin":
                        ColorUtil.type type = ColorUtil.type.SPECIAL;
                        switch (Gui.INSTANCE.SinMode.getValue()) {
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
                        RenderUtil2D.drawVLineG(this.x + 4, this.y + 1 + WurstplusGuiNew.HEIGHT - 1, offset - WurstplusGuiNew.HEIGHT,
                                ColorUtil.getSinState(Gui.INSTANCE.buttonColor.getColor(), 1000, 255, type).hashCode(),
                                ColorUtil.getSinState(Gui.INSTANCE.buttonColor.getColor(), Gui.INSTANCE.rainbowDelay.getValue(), 255, type).hashCode());
                        break;
                    case "Rainbow":
                        RenderUtil2D.drawVLineG(this.x + 4, this.y + 1 + WurstplusGuiNew.HEIGHT - 1, offset - WurstplusGuiNew.HEIGHT,
                                ColorUtil.releasedDynamicRainbow(0, 255).hashCode(),
                                ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), 255).hashCode());
                        break;
                    case "None":
                        RenderUtil2D.drawVLine(this.x + 4, this.y + 1 + WurstplusGuiNew.HEIGHT - 1, offset - WurstplusGuiNew.HEIGHT, Gui.INSTANCE.buttonColor.getValue().hashCode());
                        break;
                }
            }
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

    public void onClose() {
        for (Component comp : components) {
            comp.onClose();
        }
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - dragX);
            this.setY(mouseY - dragY);
        }
    }

    public boolean isWithinHeader(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }
}