package me.travis.wurstplusthree.gui.component.components;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.gui.component.HackButton;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class ParentComponent extends Component {
    private ParentSetting option;
    public Hack mod;
    private final ArrayList<Component> subcomponents = new ArrayList<>();
    private int x;
    private int y;
    private int y2;

    public ParentComponent(ParentSetting option, Hack hack) {
        super(option);
        this.mod = hack;
        this.option = option;
        for (Setting s : mod.getSettings()) {
            if (!s.isChild()) continue;
            if (s.getParentSetting() != option) continue;
            if (s instanceof BooleanSetting) {
                subcomponents.add(new BooleanComponent((BooleanSetting) s));
            } else if (s instanceof ColourSetting) {
                subcomponents.add(new ColorComponent((ColourSetting) s));
            } else if (s instanceof IntSetting) {
                subcomponents.add(new SliderComponent((IntSetting) s));
            } else if (s instanceof DoubleSetting) {
                subcomponents.add(new SliderComponent((DoubleSetting) s));
            } else if (s instanceof KeySetting) {
                subcomponents.add(new KeyBindComponent((KeySetting) s));
            } else if (s instanceof ParentSetting) {
                subcomponents.add(new ParentComponent((ParentSetting) s, mod));
            } else if (s instanceof EnumSetting) {
                subcomponents.add(new ModeComponent((EnumSetting) s));
            }
        }
    }


    @Override
    public void renderComponent(int mouseX, int mouseY, int x, int y) {
        this.x = x;
        this.y = y;
        RenderUtil2D.drawGradientRect(x + WurstplusGuiNew.SETTING_OFFSET, y , x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT  - 2, isMouseOnButton(mouseX, mouseY) ? Gui.INSTANCE.groupHoverColor.getValue().hashCode(): Gui.INSTANCE.groupColor.getValue().hashCode(), isMouseOnButton(mouseX, mouseY) ? Gui.INSTANCE.groupHoverColor.getValue().hashCode(): Gui.INSTANCE.groupColor.getValue().hashCode(), isMouseOnButton(mouseX, mouseY));
        WurstplusGuiNew.drawRect(x + WurstplusGuiNew.SETTING_OFFSET, y  + WurstplusGuiNew.HEIGHT - 2, x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET, y + WurstplusGuiNew.HEIGHT , isMouseOnButton(mouseX, mouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_CHILDBUTTON());
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.option.getName(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow((this.option.getValue() ? "-" : "+"), x + 90 + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(this.option.getName(), x + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
            mc.fontRenderer.drawStringWithShadow((this.option.getValue() ? "-" : "+"), x + 90 + WurstplusGuiNew.SUB_FONT_SIZE, y + 3 , Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        if (this.option.getValue()) {
            boolean didScissor = false;
            if (y2 != 0) {
                y2--;
                GL11.glScissor(x * 2, (WurstplusThree.GUI2.height - y - WurstplusGuiNew.HEIGHT - getHeight()) * 2, WurstplusGuiNew.WIDTH * 2, getHeight() * 2);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                didScissor = true;
            }
            int offset = WurstplusGuiNew.HEIGHT;
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.renderComponent(mouseX, mouseY, x, y + offset - y2);
                offset = offset + comp.getHeight();
            }
            if (didScissor) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        if (this.option.getValue())
            for (Component component : subcomponents) {
                if (component.getSetting() != null && !component.getSetting().isShown()) continue;
                component.renderToolTip(mouseX, mouseY);
            }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.option.getValue())
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.mouseReleased(mouseX, mouseY, mouseButton);
            }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.option.getValue())
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.keyTyped(typedChar, key);
            }
    }


    @Override
    public int getHeight() {
        if (this.option.getValue())
            return getHeightTarget() + WurstplusGuiNew.HEIGHT - y2;
        return WurstplusGuiNew.HEIGHT;
    }

    private int getHeightTarget() {
        int val = 0;
        for (Component c : subcomponents) {
            if (c.getSetting() != null && !c.getSetting().isShown()) continue;
            val = val + c.getHeight();
        }
        return val;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && (button == 0 || button == 1)) {
            this.option.toggle();
            y2 = getHeightTarget();
        }
        if (this.option.getValue())
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.mouseClicked(mouseX, mouseY, button);
            }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x+ WurstplusGuiNew.SETTING_OFFSET && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.SETTING_OFFSET && y > this.y  && y < this.y + WurstplusGuiNew.HEIGHT ;
    }
}
