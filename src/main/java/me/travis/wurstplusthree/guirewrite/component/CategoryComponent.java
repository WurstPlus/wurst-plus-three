package me.travis.wurstplusthree.guirewrite.component;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */

public class CategoryComponent {
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
                HackButton moduleButton = new HackButton(mod, this, tY);
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

    public void renderFrame() {
        RenderUtil2D.drawGradientRect(this.x, this.y, this.x + width, this.y + height,
                (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()),
                (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()));
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(category.getName(), this.x + WurstplusGuiNew.MODULE_FONT_SIZE, this.y + (this.height / 2) - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());

        if (this.isOpen) {
            if (!this.components.isEmpty()) {
                int x = 0;
                for (Component component : components) {
                    component.renderComponent();
                    x++;
                    if (component instanceof HackButton) {
                        if (((HackButton) component).isOpen) {
                            x += ((HackButton) component).subCompLength;
                        }
                    }
                }
                x *= WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                RenderUtil2D.drawVLine(this.x, this.y + WurstplusGuiNew.HEIGHT, x + 1, Gui.INSTANCE.lineColor.getValue().hashCode()); // Left
                RenderUtil2D.drawVLine(this.x + WurstplusGuiNew.WIDTH - 1, this.y + WurstplusGuiNew.HEIGHT, x + 1, Gui.INSTANCE.lineColor.getValue().hashCode()); // Right
                RenderUtil2D.drawHLine(this.x, this.y + WurstplusGuiNew.HEIGHT + x + 1, WurstplusGuiNew.WIDTH, Gui.INSTANCE.lineColor.getValue().hashCode()); // Bottom
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