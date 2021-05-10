package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Colour;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class ColorComponent extends Component {
    private final ColourSetting set;
    private final HackButton parent;
    private int offset;
    private boolean isOpen;
    private boolean firstTimeOpen;
    private int x;
    private int y;
    private final ColorSliderComponent r;
    private final ColorSliderComponent g;
    private final ColorSliderComponent b;
    private final ColorSliderComponent a;
    private final BoolComponent bc;
    private final ArrayList<Component> colorComponents;

    // TODO : ADD RAINBOW BUTTON

    public ColorComponent(ColourSetting value, HackButton button, int offset) {
        this.set = value;
        this.parent = button;
        this.offset = offset;
        this.isOpen = false;
        this.firstTimeOpen = true;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.r = new ColorSliderComponent(parent, offset, "Red", set.getValue().getRed(), this);
        this.g = new ColorSliderComponent(parent, offset, "Green", set.getValue().getGreen(), this);
        this.b = new ColorSliderComponent(parent, offset, "Blue", set.getValue().getBlue(), this);
        this.a = new ColorSliderComponent(parent, offset, "Alpha", set.getValue().getAlpha(), this);
        this.bc = new BoolComponent(set, button, offset, this);
        this.colorComponents = new ArrayList<>();
        colorComponents.add(r);
        parent.addOpY(WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET);
        colorComponents.add(g);
        parent.addOpY(WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET);
        colorComponents.add(b);
        parent.addOpY(WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET);
        colorComponents.add(a);
        parent.addOpY(WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET);
        colorComponents.add(bc);
        parent.addOpY(WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET);

    }

    @Override
    public void renderComponent() {
        RenderUtil2D.drawRectMC(parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET, this.set.getValue().hashCode());
        // RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset, WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getValue().hashCode());
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(set.getName(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_SIZE, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_OFFSET, Gui.INSTANCE.fontColor.getValue().hashCode());
        if (this.isOpen) {
            for (Component component : colorComponents) {
                component.renderComponent();
            }
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
        for (Component c : colorComponents) {
            c.setOff(opY);
            opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && parent.isOpen && button == 1) {
            for (Component comp : parent.parent.getComponents()) {
                if (comp instanceof HackButton) {
                    if (((HackButton) comp).isOpen) {
                        for (Component comp2 : ((HackButton) comp).getChildren()) {
                            if (comp2 instanceof ColorComponent) {
                                if (((ColorComponent) comp2).isOpen && comp2 != this) {
                                    ((ColorComponent) comp2).setOpen(false);
                                    this.parent.parent.refresh();
                                }
                            }
                        }
                    }
                }
            }
            setOpen(!isOpen);
            this.parent.parent.refresh();
        }
        for (Component c : colorComponents) {
            c.mouseClicked(mouseX, mouseY, button);
        }
        if (!this.isOpen && !this.firstTimeOpen) {
            this.firstTimeOpen = true;
        }
        if (this.isOpen && firstTimeOpen) {
            boolean flag = false;
            for (Component component : this.parent.getChildren()) {
                if (!flag && component == this) {
                    flag = true;
                    continue;
                }
                if (flag) {
                    component.setOff(component.getOffset() + (WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET) * 5);
                }
            }
            this.firstTimeOpen = false;
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        for (Component c : colorComponents) {
            c.updateComponent(mouseX, mouseY);
            set.setValue(new Colour(r.getValue(), g.getValue(), b.getValue(), a.getValue()));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component c : colorComponents) {
            c.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.SETTING_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_OFFSET && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
    }

    public void setOpen(boolean v) {
        this.isOpen = v;
    }

    public boolean isOpen() {
        return this.isOpen;
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
