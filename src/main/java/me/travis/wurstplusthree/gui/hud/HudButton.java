package me.travis.wurstplusthree.gui.hud;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.CategoryComponent;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.hud.element.HudDragComponent;
import me.travis.wurstplusthree.gui.hud.element.HudElement;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.util.RenderUtil2D;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 17/06/2021
 */

public class HudButton extends Component {

    public final HudElement element;
    public final CategoryComponent parent;
    public int offset;
    private boolean isHovered;
    private final ArrayList<HudDragComponent> dragComponents = new ArrayList<>();

    public HudButton(HudElement element, CategoryComponent parent, int offset){
        this.element = element;
        this.parent = parent;
        this.offset = offset;
        this.dragComponents.add(new HudDragComponent(element, element.getWidth(), element.getHeight()));
    }

    @Override
    public void renderComponent(int MouseX, int MouseY) {
        if (element.isEnabled()) {
            RenderUtil2D.drawGradientRect(parent.getX() + WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET,
                    parent.getX() + parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + WurstplusGuiNew.HEIGHT + this.offset + WurstplusGuiNew.MODULE_OFFSET,
                    (Gui.INSTANCE.buttonColor.getValue().hashCode()),
                    (Gui.INSTANCE.buttonColor.getValue().hashCode()), isHovered);
            for(HudDragComponent dragComponent : dragComponents){
                dragComponent.renderComponent(MouseX, MouseY);
            }
        } else {
            RenderUtil2D.drawRectMC(parent.getX() + WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET, parent.getX() + parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + WurstplusGuiNew.HEIGHT + this.offset + WurstplusGuiNew.MODULE_OFFSET, this.isHovered ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_MODULECOLOR());
        }
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.element.getName(), parent.getX() + WurstplusGuiNew.MODULE_FONT_SIZE, parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET + WurstplusGuiNew.HEIGHT / 2f - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(this.element.getName(), parent.getX() + WurstplusGuiNew.MODULE_FONT_SIZE, parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET + WurstplusGuiNew.HEIGHT / 2f - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        for(HudDragComponent dragComponent : dragComponents){
            dragComponent.updateComponent(mouseX, mouseY);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.getX() + WurstplusGuiNew.MODULE_WIDTH && x < parent.getX() + parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH && y > this.parent.getY() + this.offset && y < this.parent.getY() + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET + this.offset;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            if (element.getName().equalsIgnoreCase("return")) {
                mc.displayGuiScreen(WurstplusThree.GUI2);
                return;
            }
            this.element.toggle();
        }
        for (HudDragComponent dragComponent : dragComponents){
            dragComponent.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for(HudDragComponent dragComponent : dragComponents){
            dragComponent.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public ArrayList<HudDragComponent> getDragComponents() {
        return this.dragComponents;
    }

}
