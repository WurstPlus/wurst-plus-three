package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.type.KeySetting;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.input.Keyboard;


public class KeyBindComponent extends Component {
    private String name;
    private boolean isBinding;
    private KeySetting setting;
    private int x;
    private int y;

    public KeyBindComponent(KeySetting setting) {
        super(setting);
        this.setting = setting;
        this.name = setting.getName();
    }

    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        RenderUtil2D.drawRectMC(x + WurstplusGuiNew.SETTING_OFFSET, y , x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT , isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : this.setting.isChild() ? WurstplusGuiNew.GUI_CHILDBUTTON() : WurstplusGuiNew.GUI_COLOR());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isBinding ? "Listening..." : (name + " - " + getRenderKey()), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(isBinding ? "Listening..." : (name + " - " + getRenderKey()), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }


    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y  && y < this.y + WurstplusGuiNew.HEIGHT ;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isBinding) {
                WurstplusThree.LOGGER.info(button);
                switch (button) {
                    case 0:
                        setting.setKey(-2);
                        break;
                    case 1:
                        setting.setKey(-3);
                        break;
                    case 2:
                        setting.setKey(-4);
                        break;
                    case 3:
                        setting.setKey(-5);
                        break;
                    case 4:
                        setting.setKey(-6);
                        break;
                }
            return;
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.isBinding = !this.isBinding;
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.isBinding) {
            setting.setKey(key);
            if (key == Keyboard.KEY_DELETE) {
                setting.setKey(-1);
            }
            this.isBinding = false;
        }
    }

    private String getRenderKey() {
        switch (setting.getKey()) {
            case -1:
                return "NONE";
            case -2:
                return "M0";
            case -3:
                return "M1";
            case -4:
                return "M2";
            case -5:
                return "M3";
            case -6:
                return "M4";
            default:
                return Keyboard.getKeyName(setting.getKey());
        }
    }
}
