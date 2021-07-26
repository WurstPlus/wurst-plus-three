package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.input.Keyboard;

public class ModuleBindComponent extends Component {
    private boolean isBinding;
    private int x;
    private int y;

    private Hack module;

    public ModuleBindComponent(Hack hack) {
        super(hack);
        this.module = hack;
    }

    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        RenderUtil2D.drawRectMC(x + WurstplusGuiNew.SETTING_OFFSET, y  , x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y  + WurstplusGuiNew.HEIGHT , isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_COLOR());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(isBinding ? "Listening..." : ((module.isHold() ? "Hold" : "Toggle") + " - " + getRenderKey()), x + WurstplusGuiNew.SUB_FONT_SIZE, y  + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(isBinding ? "Listening..." : ((module.isHold() ? "Hold" : "Toggle") + " - " + getRenderKey()), x + WurstplusGuiNew.SUB_FONT_SIZE, y  + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y   && y < this.y  + WurstplusGuiNew.HEIGHT ;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isBinding) {
            this.isBinding = false;
            switch (button) {
                case 0:
                    module.setBind(-2);
                    break;
                case 1:
                    module.setBind(-3);
                    break;
                case 2:
                    module.setBind(-4);
                    break;
                case 3:
                    module.setBind(-5);
                    break;
                case 4:
                    module.setBind(-6);
                    break;

            }
            return;
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.isBinding = !this.isBinding;
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.module.toggleHold();
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.isBinding) {
            module.setBind(key);
            if (key == Keyboard.KEY_DELETE) {
                module.setBind(-1);
            }
            this.isBinding = false;
        }
    }


    private String getRenderKey() {
        if (module == null) return "NONE";
        switch (module.getBind()) {
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
                return Keyboard.getKeyName(module.getBind());
        }
    }
}
