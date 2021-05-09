package me.travis.wurstplusthree.guirewrite.component.component;


import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.CategoryComponent;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.settingcomponent.*;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 29/04/2021
 */

public class HackButton extends Component {

    public Hack mod;
    public CategoryComponent parent;
    private final ArrayList<Component> subcomponents;
    public boolean isOpen;
    private boolean isHovered;
    public int offset;
    public int subCompLength = 0;
    public int opY;

    public ArrayList<Component> getChildren() {
        ArrayList<Component> children = new ArrayList<>();
        for (Component component : this.subcomponents) {
            if (component.getParent() == this) {
                children.add(component);
            }
        }
        return children;
    }

    public HackButton(Hack mod, CategoryComponent parent, int offset) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;

        this.subcomponents = new ArrayList<>();
        this.isOpen = false;
        opY = offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
        if (WurstplusThree.SETTINGS.getSettingFromHack(mod) != null) {
            for (Setting s : WurstplusThree.SETTINGS.getSettingFromHack(mod)) {
                if (s instanceof BooleanSetting) {
                    this.subcomponents.add(new BoolComponent((BooleanSetting) s, this, opY));
                    opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                } else if (s instanceof EnumSetting) {
                    this.subcomponents.add(new ModeComponent((EnumSetting) s, this, mod, opY));
                    opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                } else if (s instanceof IntSetting) {
                    this.subcomponents.add(new SliderComponent((IntSetting) s, this, opY));
                    opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                } else if (s instanceof DoubleSetting) {
                    this.subcomponents.add(new SliderComponent((DoubleSetting) s, this, opY));
                    opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                } else if (s instanceof ColourSetting) {
                    this.subcomponents.add(new ColorComponent((ColourSetting) s, this, opY));
                    opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
                }
                else if (s instanceof KeySetting){
                    this.subcomponents.add(new KeyBindComponent((KeySetting) s, this, opY));
                }
            }
            this.subcomponents.add(new KeyBindComponent(this, opY));
            this.subcomponents.add(new ShownComponent(this, opY));
        }
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
        }
    }

    @Override
    public void renderComponent() {
        subCompLength = 0;
        if (mod.isEnabled()) {
            RenderUtil2D.drawGradientRect(parent.getX() + WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET,
                    parent.getX() + parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + WurstplusGuiNew.HEIGHT + this.offset + WurstplusGuiNew.MODULE_OFFSET,
                    (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(0, Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()),
                    (Gui.INSTANCE.rainbow.getValue() ? ColorUtil.releasedDynamicRainbow(Gui.INSTANCE.rainbowDelay.getValue(), Gui.INSTANCE.buttonColor.getValue().getAlpha()).hashCode() : Gui.INSTANCE.buttonColor.getValue().hashCode()));
        } else {
            RenderUtil2D.drawRectMC(parent.getX() + WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET, parent.getX() + parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH, this.parent.getY() + WurstplusGuiNew.HEIGHT + this.offset + WurstplusGuiNew.MODULE_OFFSET, this.isHovered ? WurstplusGuiNew.GUI_HOVERED_COLOR : WurstplusGuiNew.GUI_COLOR);
        }
        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.mod.getName(), parent.getX() + WurstplusGuiNew.MODULE_FONT_SIZE, parent.getY() + this.offset + WurstplusGuiNew.MODULE_OFFSET + WurstplusGuiNew.HEIGHT / 2 - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        if (this.isOpen) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    comp.renderComponent();
                    if (comp instanceof ColorComponent) {
                        if (((ColorComponent) comp).isOpen()) {
                            subCompLength += 6;
                        } else {
                            subCompLength++;
                        }
                    } else {
                        subCompLength++;
                    }
                }
            }
        }
        renderArrow();
    }

    @Override
    public int getHeight() {
        if (this.isOpen) {
            int val = 0;
            for (Component c : subcomponents) {
                if (c instanceof ColorComponent) {
                    if (((ColorComponent) c).isOpen()) {
                        val += 5;
                    }
                }
            }
            return ((WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET) * (this.subcomponents.size() + 1 + val));
        }
        return WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.isOpen = !this.isOpen;
            this.parent.refresh();
            for(Component comp : parent.getComponents()){
                if(comp instanceof HackButton){
                    if(((HackButton) comp).isOpen){
                        if(((HackButton) comp).isOpen){
                            for(Component comp2 : ((HackButton) comp).getChildren()){
                                if(comp2 instanceof ColorComponent){
                                    ((ColorComponent) comp2).setOpen(false);
                                    this.parent.refresh();
                                }
                            }
                        }
                    }
                }
            }
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }


    public boolean isMouseOnButton(int x, int y) {
        if (x > parent.getX() + WurstplusGuiNew.MODULE_WIDTH && x < parent.getX() + parent.getWidth() - WurstplusGuiNew.MODULE_WIDTH && y > this.parent.getY() + this.offset && y < this.parent.getY() + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_OFFSET + this.offset) {
            return true;
        }
        return false;
    }

    private void renderArrow(){
        switch (Gui.INSTANCE.arrowType.getValue()){
            case "Type1":
                if(this.isOpen){
                    RenderUtil.drawTriangleOutline(parent.getX() + 105f, parent.getY() + offset + 12f, 5f, 2, 1, 1,Gui.INSTANCE.fontColor.getValue().hashCode());
                }
                else {
                    RenderUtil.drawTriangleOutline(parent.getX() + 105f, parent.getY() + offset + 12f, 5f, 1, 2, 1,Gui.INSTANCE.fontColor.getValue().hashCode());
                }
                break;
            case "Type2":
                if(this.isOpen){
                    GL11.glPushMatrix();
                    GL11.glTranslated(parent.getX() + 102f, parent.getY() + offset + 12f, 0);
                    GL11.glRotatef(-90f, 0f, 0f, 1f);
                    GL11.glTranslated(-(parent.getX() + 102f), -(parent.getY() + offset + 12f), 0);
                    RenderUtil.drawTriangleOutline(parent.getX() + 105f, parent.getY() + offset + 12f, 5f, 2, 1, 1,Gui.INSTANCE.fontColor.getValue().hashCode());
                    GL11.glPopMatrix();
                }
                else {
                    RenderUtil.drawTriangleOutline(parent.getX() + 105f, parent.getY() + offset + 12f, 5f, 2, 1, 1,Gui.INSTANCE.fontColor.getValue().hashCode());
                }
                break;
        }
    }

    public void addOpY(int v) {
        opY += v;
    }
}
