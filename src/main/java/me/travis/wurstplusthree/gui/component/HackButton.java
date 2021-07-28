package me.travis.wurstplusthree.gui.component;


import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.gui.component.components.*;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.hacks.client.Gui;
import me.travis.wurstplusthree.hud.HudComponent;
import me.travis.wurstplusthree.setting.Feature;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class HackButton extends Component {
    public Hack mod;
    public HudComponent hudComponent;
    private final ArrayList<Component> subcomponents = new ArrayList<>();
    public boolean isOpen = false;
    int x;
    int y;
    double y2;

    public HackButton(Feature feature) {
        super(feature);
        if (feature instanceof Hack) {
            this.mod = (Hack) feature;
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
                    subcomponents.add(new ParentComponent((ParentSetting) s));
                } else if (s instanceof EnumSetting) {
                    subcomponents.add(new ModeComponent((EnumSetting) s));
                }
            }
            subcomponents.add(new ShownComponent(this.mod));
            subcomponents.add(new ModuleBindComponent(this.mod));
        } else if (feature instanceof HudComponent){
            hudComponent = (HudComponent) feature;
            for (Setting s : hudComponent.getSettings()) {
                this.hudComponent = (HudComponent) feature;
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
                    subcomponents.add(new ParentComponent((ParentSetting) s));
                } else if (s instanceof EnumSetting) {
                    subcomponents.add(new ModeComponent((EnumSetting) s));
                }
            }
        }
    }

    @Override
    public void renderComponent(int MouseX, int MouseY, int x, int y) {
        this.x = x;
        this.y = y;
        if ((mod != null && mod.isEnabled()) || (hudComponent != null && hudComponent.isEnabled())) {
            RenderUtil2D.drawGradientRect(x + WurstplusGuiNew.MODULE_WIDTH, y,
                    x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH, y + WurstplusGuiNew.HEIGHT,
                    (Gui.INSTANCE.buttonColor.getValue().hashCode()),
                    (Gui.INSTANCE.buttonColor.getValue().hashCode()), isMouseOnButton(MouseX, MouseY));
        } else {
            RenderUtil2D.drawRectMC(x + WurstplusGuiNew.MODULE_WIDTH, y, x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH, y + WurstplusGuiNew.HEIGHT, isMouseOnButton(MouseX, MouseY) ? WurstplusGuiNew.GUI_HOVERED_COLOR() : WurstplusGuiNew.GUI_MODULECOLOR());
        }
        String name = this.mod == null ? this.hudComponent.getName() : this.mod.getName();
        if (Gui.INSTANCE.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(name, x + WurstplusGuiNew.MODULE_FONT_SIZE + 2, y + WurstplusGuiNew.HEIGHT / 2f - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        } else {
            mc.fontRenderer.drawStringWithShadow(name, x + WurstplusGuiNew.MODULE_FONT_SIZE + 2, y + WurstplusGuiNew.HEIGHT / 2f - WurstplusGuiNew.FONT_HEIGHT, Gui.INSTANCE.fontColor.getValue().hashCode());
        }
        if ((mod != null && !this.mod.getSettings().isEmpty()) || (hudComponent != null && !hudComponent.getSettings().isEmpty())) {
            if (!isOpen) {
                RenderUtil2D.drawRect(x + 107, y + 5, x + 107 + 1.5f, y + 5 + 1.5f, -1);
                RenderUtil2D.drawRect(x + 107, y + 7.25f, x + 107 + 1.5f, y + 7.25f + 1.5f, -1);
                RenderUtil2D.drawRect(x + 107, y + 9.5f, x + 107 + 1.5f, y + 9.5f + 1.5f, -1);
            } else {
                RenderUtil2D.drawRect(x + 104.75f, y + 7.25f, x + 104.75f + 1.5f, y + 7.25f + 1.5f, -1);
                RenderUtil2D.drawRect(x + 107, y + 7.25f, x + 107 + 1.5f, y + 7.25f + 1.5f, -1);
                RenderUtil2D.drawRect(x + 109.25f, y + 7.25f, x + 109.25f + 1.5f,y + 7.25f +  1.5f, -1);
            }
        }
        boolean didScissor = false;
        if (y2 != 0) {
            y2 = Math.max(y2 - Gui.INSTANCE.animation.getValue(), 0);
            GL11.glScissor(x * 2, (WurstplusThree.GUI2.height - y - WurstplusGuiNew.HEIGHT - getHeight()) * 2, WurstplusGuiNew.WIDTH * 2, getHeight() * 2);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            didScissor = true;
        }
        if (isOpen) {
            int offset = WurstplusGuiNew.HEIGHT;
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.renderComponent(MouseX, MouseY, x, y + offset - (int) y2);
                offset = offset + comp.getHeight();
            }
        } else if (didScissor) {
            int offset = WurstplusGuiNew.HEIGHT - getHeightTarget();
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.renderComponent(MouseX, MouseY, x, y + offset + (int) y2);
                offset = offset + comp.getHeight();
            }
        }
        if (didScissor)
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        if (mod == null) return;
        if (isMouseOnButton(mouseX, mouseY) && Gui.INSTANCE.toolTips.getValue()) {
            int length = WurstplusThree.GUI_FONT_MANAGER.getTextWidth(mod.getDescription());
            int height = WurstplusThree.GUI_FONT_MANAGER.getTextHeight();
            RenderUtil2D.drawRectMC(mouseX + 6, mouseY + 9, mouseX + length + 10, mouseY + height + 13, Gui.INSTANCE.toolTipColor.getValue().hashCode());
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(mod.getDescription(), mouseX + 8, mouseY + 11, new Color(255, 255, 255).hashCode());
        }
        for (Component component : subcomponents) {
            if (this.isOpen && y2 == 0) {
                if (component.getSetting() != null && !component.getSetting().isShown()) continue;
                component.renderToolTip(mouseX, mouseY);
            }
        }
    }

    @Override
    public int getHeight() {
        if (isOpen)
            return (int) (getHeightTarget() + WurstplusGuiNew.HEIGHT - y2);
        return (int) (WurstplusGuiNew.HEIGHT + y2);
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
    public void onClose() {
        for (Component comp : subcomponents) {
            comp.onClose();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            if (mod != null)
                this.mod.toggle();
            if (hudComponent != null)
                this.hudComponent.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.isOpen = !this.isOpen;
            y2 = getHeightTarget() - y2;
        }
        if (this.isOpen && y2 == 0)
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.mouseClicked(mouseX, mouseY, button);
            }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.isOpen && y2 == 0)
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.mouseReleased(mouseX, mouseY, mouseButton);
            }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.isOpen && y2 == 0)
            for (Component comp : this.subcomponents) {
                if (comp.getSetting() != null && !comp.getSetting().isShown()) continue;
                comp.keyTyped(typedChar, key);
            }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x + WurstplusGuiNew.MODULE_WIDTH && x < this.x + WurstplusGuiNew.WIDTH - WurstplusGuiNew.MODULE_WIDTH && y > this.y && y < this.y + WurstplusGuiNew.HEIGHT;
    }
}
