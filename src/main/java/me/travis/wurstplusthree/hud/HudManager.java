package me.travis.wurstplusthree.hud;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.gui.HudEditor;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.ReflectionUtil;
import me.travis.wurstplusthree.util.RenderUtil2D;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HudManager implements Globals {
    private int dragX;
    private int dragY;
    private HudComponent dragComponent;
    private final List<HudComponent> components = new ArrayList<>();

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
                    dragX = component.getX() - mouseX;
                    dragY = component.getY() - mouseY;
                    break;
                }
            }
        }
    }

    public void onMouseReleased() {
        dragComponent = null;
    }

    public void onGuiRender(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        for (HudComponent comp : components) {
            if (!comp.isEnabled()) continue;
            //RenderUtil2D.drawBorderedRect(comp.getX(), comp.getY(), comp.getX() + comp.getWidth(), comp.getY() + comp.getHeight(), 1, me.travis.wurstplusthree.hack.hacks.client.HudEditor.INSTANCE.backGround.getValue().hashCode(), me.travis.wurstplusthree.hack.hacks.client.HudEditor.INSTANCE.outLine.getValue().hashCode());
            RenderUtil2D.drawBorderedRect(comp.getX(), comp.getY(), comp.getX() + comp.getWidth(), comp.getY() + comp.getHeight(), 1, new Color(20, 20, 20, 50).hashCode(), me.travis.wurstplusthree.hack.hacks.client.HudEditor.INSTANCE.outLine.getValue().hashCode(), false);
            comp.renderComponent(scaledResolution.getScaledHeight(), scaledResolution.getScaledWidth());
        }
        if (dragComponent == null) return;
        dragComponent.setX(mouseX + dragX);
        dragComponent.setY(mouseY + dragY);
    }

    public List<HudComponent> getComponents() {
        return components;
    }
}
