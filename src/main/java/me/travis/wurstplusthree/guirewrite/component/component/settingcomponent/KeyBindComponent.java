package me.travis.wurstplusthree.guirewrite.component.component.settingcomponent;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.guirewrite.WurstplusGuiNew;
import me.travis.wurstplusthree.guirewrite.component.Component;
import me.travis.wurstplusthree.guirewrite.component.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.type.KeySetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.input.Keyboard;

/**
 * @author megyn
 * @since 29/04/2021
 */

public class KeyBindComponent extends Component {
    private String name;
    private boolean isHovered;
    private boolean isBinding;
    private HackButton parent;
    private KeySetting setting;
    private int offset;
    private int x;
    private int y;
    private boolean normal;

    private Hack module;

    public KeyBindComponent(HackButton button, int offset) {
        this.parent = button;
        this.name = "Bind";
        this.offset = offset;
        this.normal = true;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    public KeyBindComponent(KeySetting setting, HackButton button, int offset) {
        this.parent = button;
        this.setting = setting;
        this.name = setting.getName();
        this.offset = offset;
        this.normal = false;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
    }

    @Override
    public void renderComponent() {
        if (normal) {
            module = this.parent.mod;
            RenderUtil2D.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING, this.isHovered ? WurstplusGuiNew.GUI_HOVERED_TRANSPARENCY : WurstplusGuiNew.GUI_TRANSPARENCY);
            if(module.getBind() == -1){
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isBinding ? "Listening..." : (name + " - " + "NONE"), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, Gui.INSTANCE.fontColor.getValue().hashCode());
            }else{
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isBinding ? "Listening..." : (name + " - " + Keyboard.getKeyName(module.getBind())), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, Gui.INSTANCE.fontColor.getValue().hashCode());
            }

        } else {
            RenderUtil2D.drawRect(parent.parent.getX() + WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.MODULE_SPACING, parent.parent.getX() + parent.parent.getWidth() - WurstplusGuiNew.SETTING_WIDTH_OFFSET, parent.parent.getY() + offset + WurstplusGuiNew.HEIGHT + WurstplusGuiNew.MODULE_SPACING, this.isHovered ? WurstplusGuiNew.GUI_HOVERED_TRANSPARENCY : WurstplusGuiNew.GUI_TRANSPARENCY);
            if(setting.getKey() == -1){
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isBinding ? "Listening..." : (name + " - " + "NONE"), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, Gui.INSTANCE.fontColor.getValue().hashCode());
            }else {
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isBinding ? "Listening..." : (name + " - " + Keyboard.getKeyName(setting.getKey())), parent.parent.getX() + WurstplusGuiNew.SUB_FONT_INDENT, parent.parent.getY() + offset + 3 + WurstplusGuiNew.MODULE_SPACING, Gui.INSTANCE.fontColor.getValue().hashCode());
            }
        }
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
            this.isBinding = !this.isBinding;
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.isBinding) {
            if(this.normal) {
                module.setBind(key);
                this.isBinding = false;
            }else {
                setting.setKey(key);
                this.isBinding = false;
            }
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
