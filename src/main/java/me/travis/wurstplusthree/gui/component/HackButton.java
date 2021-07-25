package me.travis.wurstplusthree.gui.component;


import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.components.*;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class HackButton extends Component {
    public Hack mod;
    private final ArrayList<Component> subcomponents = new ArrayList<>();
    public boolean isOpen = false;
    int x;
    int y;
    int y2;

    public HackButton(Hack mod) {
        super(mod);
        this.mod = mod;
        for (Setting s : mod.getSettings()) {
            if (s.isChild()) continue;
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
        subcomponents.add(new ShownComponent(this.mod));
        subcomponents.add(new ModuleBindComponent(this.mod));
    }

    @Override
    public void renderComponent(int MouseX, int MouseY, int x, int y) {
        this.x = x;
        this.y = y;
        if (mod.isEnabled()) {
            RenderUtil2D.drawGradientRect(x + WurstplusGuiNew.MODULE_WIDTH, y,
                    x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH, y + WurstplusGuiNew.HEIGHT,
                    (Gui.INSTANCE.buttonColor.getValue().hashCode()),
                    (Gui.INSTANCE.buttonColor.getValue().hashCode()), isMouseOnButton(MouseX, MouseY));
        } else {
            RenderUtil2D.drawRectMC(x + WurstplusGuiNew.MODULE_WIDTH, y, x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH, y + WurstplusGuiNew.HEIGHT, isMouseOnButton(MouseX, MouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_MODULECOLOR());
        }
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(this.mod.getName(), x + WurstplusGuiNew.MODULE_FONT_SIZE, y + WurstplusGuiNew.HEIGHT / 2f - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(this.mod.getName(), x + WurstplusGuiNew.MODULE_FONT_SIZE, y + WurstplusGuiNew.HEIGHT / 2f - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        if (isOpen) {
            if (y2 != 0) {
                y2--;
            }
            GL11.glScissor(x * 2, (WurstplusThree.GUI2.height - y - WurstplusGuiNew.HEIGHT - getHeight()) * 2, WurstplusGuiNew.WIDTH * 2, getHeight() * 2);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            int offset = WurstplusGuiNew.HEIGHT;
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.renderComponent(MouseX, MouseY, x, y + offset - y2);
                offset = offset + comp.getHeight();
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        renderArrow();
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        if (isMouseOnButton(mouseX, mouseY) && Gui.INSTANCE.toolTips.getValue()) {
            int length = WurstplusThree.GUI_FONT_MANAGER.getTextWidth(mod.getDescription());
            int height = WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
            RenderUtil2D.drawRectMC(mouseX + 6, mouseY + 9, mouseX + length + 10, mouseY + height + 13, Gui.INSTANCE.toolTipColor.getValue().hashCode());
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(mod.getDescription(), mouseX + 8, mouseY + 11, new Color(255, 255, 255).hashCode());
        }
        for (Component component : subcomponents) {
            if (this.isOpen) {
                if (component.getSetting() != null && !component.getSetting().isShown()) continue;
                component.renderToolTip(mouseX, mouseY);
            }
        }
    }

    @Override
    public int getHeight() {
        if (isOpen)
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
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.isOpen = !this.isOpen;
            if (isOpen)
                y2 = getHeightTarget();
        }
        if (this.isOpen)
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.mouseClicked(mouseX, mouseY, button);
            }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.isOpen)
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.mouseReleased(mouseX, mouseY, mouseButton);
            }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.isOpen)
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.keyTyped(typedChar, key);
            }
    }


    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.MODULE_WIDTH && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }

    private void renderArrow() {
        switch (Gui.INSTANCE.arrowType.getValue()) {
            case "Type1":
                if (this.isOpen) {
                    RenderUtil.drawTriangleOutline(x + 105f, y + 12f, 5f, 2, 1, 1, Gui.INSTANCE.fontColor.getValue().hashCode());
                } else {
                    RenderUtil.drawTriangleOutline(x + 105f, y + 12f, 5f, 1, 2, 1, Gui.INSTANCE.fontColor.getValue().hashCode());
                }
                break;
            case "Type2":
                if (this.isOpen) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x + 102f, y + 12f, 0);
                    GL11.glRotatef(-90f, 0f, 0f, 1f);
                    GL11.glTranslated(-(x + 102f), -(y + 12f), 0);
                    RenderUtil.drawTriangleOutline(x + 105f, y + 12f, 5f, 2, 1, 1, Gui.INSTANCE.fontColor.getValue().hashCode());
                    GL11.glPopMatrix();
                } else {
                    RenderUtil.drawTriangleOutline(x + 105f, y + 12f, 5f, 2, 1, 1, Gui.INSTANCE.fontColor.getValue().hashCode());
                }
                break;
        }
    }
}
