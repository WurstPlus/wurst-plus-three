package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.client.GuiRewrite;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import me.travis.wurstplusthree.util.elements.Colour;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class ColorComponent extends Component {
    private ColourSetting set;
    private HackButton parent;
    private int offset;
    private int opY;
    private boolean isOpen;
    private int x;
    private int y;
    private ColorSliderComponent r;
    private ColorSliderComponent g;
    private ColorSliderComponent b;
    private ColorSliderComponent a;
    private ArrayList<Component> colorComponents;

    public ColorComponent(ColourSetting value, HackButton button, int offset) {
        this.set = value;
        this.parent = button;
        this.offset = offset;
        this.isOpen = false;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.r = new ColorSliderComponent(parent, offset, "Red", set.getColor().getRed(), this);
        this.g = new ColorSliderComponent(parent, offset , "Green", set.getColor().getGreen(), this);
        this.b = new ColorSliderComponent(parent, offset, "Blue", set.getColor().getBlue(), this);
        this.a = new ColorSliderComponent(parent, offset, "Alpha", set.getColor().getBlue(), this);
        this.colorComponents = new ArrayList<>();
        colorComponents.add(r);
        parent.addOpY( WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING);
        colorComponents.add(g);
        parent.addOpY( WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING);
        colorComponents.add(b);
        parent.addOpY( WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING);
        colorComponents.add(a);
        parent.addOpY( WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING);
    }

    @Override
    public void renderComponent() {
        RenderUtil2D.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING, this.set.getColor().hashCode());
        // RenderUtil2D.drawVerticalLine(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset, WurstplusGuiNew.HEIGHT + 2, GuiRewrite.INSTANCE.lineColor.getColor().hashCode());
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(set.getName(), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, GuiRewrite.INSTANCE.fontColor.getColor().hashCode());
        if(this.isOpen) {
            for (Component component : colorComponents) {
                component.renderComponent();
            }
            boolean flag = false;
            for (Component component : this.parent.getChildren()) {
                if (!flag && component == this) {
                    flag = true;
                    continue;
                }
                if (flag) {
                    component.setOff(offset + (WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING) * 5);
                }
            }
        }
    }

    @Override
    public void setOff(int newOff){
        offset = newOff;
        opY = offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
        for(Component c : colorComponents){
            c.setOff(opY);
            opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button){
        if(isMouseOnButton(mouseX, mouseY) && parent.isOpen&&button == 1) {
            setOpen(!isOpen);
            this.parent.parent.refresh();
        }
        for(Component c : colorComponents){
            c.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY){
        for(Component c : colorComponents){
            c.updateComponent(mouseX, mouseY);
            set.setValue(new Colour(r.getValue(), g.getValue(), b.getValue(), a.getValue()));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for(Component c : colorComponents){
            c.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET && x < this.parent.parent.getX() + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_WIDTH_OFFSET && y > this.parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING && y < this.parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING;
    }
    public void setOpen(boolean v){
        this.isOpen = v;
    }

    public boolean isOpen(){
        return this.isOpen;
    }

    @Override
    public HackButton getParent() {
        return parent;
    }

}
