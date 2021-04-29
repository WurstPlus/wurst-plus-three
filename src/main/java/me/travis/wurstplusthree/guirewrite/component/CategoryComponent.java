package me.travis.wurstplusthree.guirewrite.component;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.guirewrite.component.component.settingcomponent.ColorComponent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.GuiRewrite;
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
    private int width;
    private int height;
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
                tY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
            }
        }
    }

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
                (GuiRewrite.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, GuiRewrite.INSTANCE.buttonColor.getColor().getAlpha()).hashCode() : GuiRewrite.INSTANCE.buttonColor.getColor().hashCode()),
                (GuiRewrite.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(GuiRewrite.INSTANCE.rainbowDelay.getValue(), GuiRewrite.INSTANCE.buttonColor.getColor().getAlpha()).hashCode() : GuiRewrite.INSTANCE.buttonColor.getColor().hashCode()));
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(category.getName(), this.x + WurstplusGuiNew.MODULE_FONT_INDENT, this.y + (this.height / 2) - WurstplusGuiNew.FONT_HEIGHT, GuiRewrite.INSTANCE.fontColor.getColor().hashCode());

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
                x *= WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
                RenderUtil2D.drawVerticalLine(this.x, this.y + WurstplusGuiNew.HEIGHT, x + 1, GuiRewrite.INSTANCE.lineColor.getColor().hashCode()); // Left
                RenderUtil2D.drawVerticalLine(this.x + WurstplusGuiNew.WIDTH - 1, this.y + WurstplusGuiNew.HEIGHT, x + 1, GuiRewrite.INSTANCE.lineColor.getColor().hashCode()); // Right
                RenderUtil2D.drawHorizontalLine(this.x, this.y + WurstplusGuiNew.HEIGHT + x + 1, WurstplusGuiNew.WIDTH, GuiRewrite.INSTANCE.lineColor.getColor().hashCode()); // Bottom
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