package me.travis.wurstplusthree.hud;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.HudEditor;
import me.travis.wurstplusthree.gui.component.Component;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.ReflectionUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public class HudManager implements Globals {
    private int dragX;
    private int dragY;
    private HudComponent dragComponent;
    private final List<HudComponent> components = new ArrayList<>();
    public ArrayList<HudComponent> RULIST = new ArrayList<>();
    public ArrayList<HudComponent> RDLIST = new ArrayList<>();
    public ArrayList<HudComponent> LULIST = new ArrayList<>();
    public ArrayList<HudComponent> LDLIST = new ArrayList<>();

    public HudManager() {
        try {
            ArrayList<Class<?>> modClasses = ReflectionUtil.getClassesForPackage("me.travis.wurstplusthree.hud.components");
            modClasses.spliterator().forEachRemaining(aClass -> {
                if (HudComponent.class.isAssignableFrom(aClass)) {
                    try {
                        HudComponent module = (HudComponent) aClass.getConstructor().newInstance();
                        components.add(module);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onRender2D(Render2DEvent event) {
        if (mc.currentScreen instanceof HudEditor) return;
        sortComponents();
        for (HudComponent component : components) {
            if (component.isEnabled())
                component.renderComponent(event.scaledResolution.getScaledHeight(), event.scaledResolution.getScaledWidth());
        }
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (HudComponent component : components) {
            if (component.isMouseOver(mouseX, mouseY) && component.isEnabled()) {
                if (mouseButton == 1) {
                    component.disable();
                    break;
                } else if (mouseButton == 0) {
                    this.dragComponent = component;
                    RULIST.remove(component);
                    RDLIST.remove(component);
                    LULIST.remove(component);
                    LDLIST.remove(component);
                    dragX = component.getX() - mouseX;
                    dragY = component.getY() - mouseY;
                    break;
                }
            }
        }
    }

    public void sortComponents() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int OFFSET = 0;
        for (HudComponent component : RULIST) {
            if (!component.isEnabled()) continue;
            component.setX(0);
            component.setY(OFFSET);
            OFFSET+=component.getHeight();
        }
        OFFSET = 0;
        if (mc.ingameGUI.getChatGUI().getChatOpen()) OFFSET = 15;
        for (HudComponent component : RDLIST) {
            if (!component.isEnabled()) continue;
            component.setX(0);
            component.setY(scaledResolution.getScaledHeight() - OFFSET - component.getHeight());
            OFFSET+=component.getHeight();
        }
        OFFSET = 0;
        for (HudComponent component : LULIST) {
            if (!component.isEnabled()) continue;
            component.setX(scaledResolution.getScaledWidth() - component.getWidth());
            component.setY(OFFSET);
            OFFSET+=component.getHeight();
        }
        OFFSET = 0;
        for (HudComponent component : LDLIST) {
            if (!component.isEnabled()) continue;
            component.setX(scaledResolution.getScaledWidth() - component.getWidth());
            component.setY(scaledResolution.getScaledHeight() - OFFSET - component.getHeight());
            OFFSET+=component.getHeight();
        }
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        if (dragComponent != null) {
            if (mouseX < 100 && mouseY < 100) {
                RULIST.add(dragComponent);
            } else if (mouseX < 100 && mouseY > scaledResolution.getScaledHeight() - 100) {
                RDLIST.add(dragComponent);
            } else if (mouseX > scaledResolution.getScaledWidth() - 100 && mouseY < 100) {
                LULIST.add(dragComponent);
            } else if (mouseX > scaledResolution.getScaledWidth() - 100 && mouseY > scaledResolution.getScaledHeight() - 100) {
                LDLIST.add(dragComponent);
            }
        }
        dragComponent = null;
    }

    public void onGuiRender(int mouseX, int mouseY, float partialTicks) {
        sortComponents();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        for (HudComponent comp : components) {
            if (!comp.isEnabled()) continue;
            RenderUtil2D.drawBorderedRect(comp.getX(), comp.getY(), comp.getX() + comp.getWidth(), comp.getY() + comp.getHeight(), 1, me.travis.wurstplusthree.hack.hacks.client.HudEditor.INSTANCE.backGround.getValue().hashCode(), me.travis.wurstplusthree.hack.hacks.client.HudEditor.INSTANCE.outLine.getValue().hashCode(), false);
            comp.renderComponent(scaledResolution.getScaledHeight(), scaledResolution.getScaledWidth());
        }
        if (dragComponent == null) return;
        dragComponent.setX(Math.min(scaledResolution.getScaledWidth() - dragComponent.getWidth(), Math.max(mouseX + dragX, 0)));
        dragComponent.setY(Math.min(scaledResolution.getScaledHeight() - dragComponent.getHeight(), Math.max(mouseY + dragY, 0)));
    }

    public List<HudComponent> getComponents() {
        return components;
    }

    public final HudComponent getComponentByName(String name) {
        for (HudComponent c : components) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
