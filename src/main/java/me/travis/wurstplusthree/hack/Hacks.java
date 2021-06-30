package me.travis.wurstplusthree.hack;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.gui.WurstplusGuiNew;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.ReflectionUtil;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Hacks implements Globals {

    private final List<Hack> hacks = new ArrayList<>();
    private final List<Hack> drawnHacks = new ArrayList<>();

    public Hacks() {

        try {
            ArrayList<Class<?>> modClasses = ReflectionUtil.getClassesForPackage("me.travis.wurstplusthree.hack.hacks");
            modClasses.spliterator().forEachRemaining(aClass -> {
                if(Hack.class.isAssignableFrom(aClass)) {
                    try {
                        Hack module = (Hack) aClass.getConstructor().newInstance();
                        hacks.add(module);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            hacks.sort(Comparator.comparing(Hack::getPriority));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public final List<Hack> getHacks() {
        return this.hacks;
    }

    public final List<Hack> getDrawnHacks() {
        return this.drawnHacks;
    }

    public final boolean isDrawHack(Hack hack) {
        return this.drawnHacks.contains(hack);
    }

    public final void addDrawHack(Hack hack) {
        this.drawnHacks.add(hack);
    }

    public final void removeDrawnHack(Hack hack) {
        if (!isDrawHack(hack)) return;
        this.drawnHacks.remove(hack);
    }

    public final Hack getHackByName(String name) {
        for (Hack hack : this.hacks) {
            if (hack.getName().equalsIgnoreCase(name)) {
                return hack;
            }
        }
        return null;
    }

    public final void enablehack(String name) {
        this.getHackByName(name).enable();
    }

    public final void disablehack(String name) {
        this.getHackByName(name).disable();
    }

    public final boolean ishackEnabled(String name) {
        try {
            return this.getHackByName(name).isEnabled();
        } catch (NullPointerException error) {
            return false;
        }
    }

    public final void onUpdate() {
        this.hacks.stream().filter(Hack::isEnabled).spliterator().forEachRemaining(Hack::onUpdate);
        if (mc.currentScreen == null) {
            for (Hack hack : hacks) {
                if (hack.isHold() && hack.getBind() >= 0) {
                    if (Keyboard.isKeyDown(hack.getBind())) {
                        if (!hack.isEnabled()) {
                            hack.enable();
                        }
                    } else {
                        if (hack.isEnabled()) {
                            hack.disable();
                        }
                    }
                }
            }
        }
    }


    public final void onTick() {
        this.hacks.stream().filter(Hack::isEnabled).spliterator().forEachRemaining(Hack::onTick);
    }

    public final void onRender2D(Render2DEvent event) {
        this.hacks.stream().filter(Hack::isEnabled).spliterator().forEachRemaining(hack -> hack.onRender2D(event));
    }

    public final void onRender3D(Render3DEvent event) {
        this.hacks.stream().filter(Hack::isEnabled).spliterator().forEachRemaining(hack -> hack.onRender3D(event));
    }

    public final void onLogout() {
        this.hacks.spliterator().forEachRemaining(Hack::onLogout);
    }

    public final void onLogin() {
        this.hacks.spliterator().forEachRemaining(Hack::onLogin);
    }

    public final void onUnload() {
        this.hacks.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.hacks.forEach(WurstplusThree.EVENT_PROCESSOR::removeEventListener);
        this.hacks.forEach(Hack::onUnload);
    }

    public final void unloadAll() {
        for (Hack hack : this.hacks) {
            hack.disable();
        }
    }

    public final void onKeyDown(int key) {
        if (key <= 0 || mc.currentScreen instanceof WurstplusGuiNew) {
            return;
        }
        for (Hack hack : this.hacks) {
            if (hack.isHold()) continue;
            if (hack.getBind() == key) {
                hack.toggle();
            }
        }
    }

    public final List<Hack.Category> getCategories() {
        List<Hack.Category> cats = new ArrayList<>();
        for (Hack.Category category : Hack.Category.values()) {
            if (category.getName().equalsIgnoreCase("hidden") || category.getName().equalsIgnoreCase("hud")) continue;
            cats.add(category);
        }
        return cats;
    }

    public final List<Hack> getHacksByCategory(Hack.Category cat) {
        List<Hack> hacks = new ArrayList<>();
        for (Hack hack : this.hacks) {
            if (hack.getCategory() == cat) {
                hacks.add(hack);
            }
        }
        hacks.sort(Comparator.comparing(Hack::getName));
        return hacks;
    }

    public final List<Hack> getEnabledHacks() {
        List<Hack> hacks = new ArrayList<>();
        for (Hack hack : this.hacks) {
            if (hack.isEnabled()) {
                hacks.add(hack);
            }
        }
        return hacks;
    }

    public final List<Hack> getHacksAlp() {
        List<Hack> sortedHacks = new ArrayList<>(this.hacks);
        sortedHacks.sort(Comparator.comparing(Hack::getName));
        return sortedHacks;
    }

    public final List<Hack> getSortedHacks(boolean reverse, boolean customFont) {
        if (customFont) {
            if (reverse) {
                List<Hack> list = this.getEnabledHacks().stream().sorted(Comparator.comparing(hack ->
                        WurstplusThree.GUI_FONT_MANAGER.getTextWidth(hack.getFullArrayString()))).collect(Collectors.toList());
                Collections.reverse(list);
                return list;
            }  else {
                return this.getEnabledHacks().stream().sorted(Comparator.comparing(hack ->
                        WurstplusThree.GUI_FONT_MANAGER.getTextWidth(hack.getFullArrayString()))).collect(Collectors.toList());
            }
        } else {
            if (reverse) {
                List<Hack> list = this.getEnabledHacks().stream().sorted(Comparator.comparing(hack ->
                        mc.fontRenderer.getStringWidth(hack.getFullArrayString()))).collect(Collectors.toList());
                Collections.reverse(list);
                return list;
            }  else {
                return this.getEnabledHacks().stream().sorted(Comparator.comparing(hack ->
                        mc.fontRenderer.getStringWidth(hack.getFullArrayString()))).collect(Collectors.toList());
            }
        }
    }
}
