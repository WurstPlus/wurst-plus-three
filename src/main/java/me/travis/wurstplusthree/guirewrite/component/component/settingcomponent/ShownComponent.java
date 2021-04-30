package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.input.Keyboard;

/**
 * @author BrownZombie
 * @since 29/04/2021
 */

public class ShownComponent extends Component {
    private String name;
    private boolean isHovered;
    private HackButton parent;
    private int offset;
    private int x;
    private int y;

    private Hack module;

    public ShownComponent(HackButton button, int offset) {
        this.parent = button;
        this.name = "Shown";
        this.offset = offset;

        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    @Override
    public void renderComponent() {
        module = this.parent.mod;
        RenderUtil2D.drawRect(parent.parent.getX() + WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING, this.isHovered ? WurstplusGuiNew.GUI_HOVERED_TRANSPARENCY : WurstplusGuiNew.GUI_TRANSPARENCY);
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Shown: " + (this.module.getShown() ? "True" : "False"), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, Gui.INSTANCE.fontColor.getColor().hashCode());
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_WIDTH_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.isOpen) {
            this.module.setShown(!this.module.getShown());
        }
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
