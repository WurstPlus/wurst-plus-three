package me.travis.wurstplusthree.hack;

import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.gui.WurstplusGui;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.hack.player.Sprint;
import me.travis.wurstplusthree.util.Globals;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hacks implements Globals {

    private List<Hack> hacks = new ArrayList<>();

    public Hacks() {
        // chat
        // client
        this.hacks.add(Gui.INSTANCE);
        // combat
        // misc
        // player
        this.hacks.add(new Sprint());
        // render
    }

    public List<Hack> getHacks() {
        return this.hacks;
    }

    public Hack getHackByName(String name) {
        for (Hack hack : this.hacks) {
            if (hack.getName().equalsIgnoreCase(name)) {
                return hack;
            }
        }
        return null;
    }

    public void enablehack(String name) {
        this.getHackByName(name).enable();
    }

    public void disablehack(String name) {
        this.getHackByName(name).disable();
    }

    public boolean ishackEnabled(String name) {
        return this.getHackByName(name).isEnabled();
    }

    public void onLoad() {
        this.hacks.stream().filter(Hack::isListening).forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        this.hacks.forEach(Hack::onLoad);
    }

    public void onUpdate() {
        this.hacks.stream().filter(Hack::isEnabled).forEach(Hack::onUpdate);
    }

    public void onTick() {
        this.hacks.stream().filter(Hack::isEnabled).forEach(Hack::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.hacks.stream().filter(Hack::isEnabled).forEach(hack -> hack.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.hacks.stream().filter(Hack::isEnabled).forEach(hack -> hack.onRender3D(event));
    }

    public void onLogout() {
        this.hacks.forEach(Hack::onLogout);
    }

    public void onLogin() {
        this.hacks.forEach(Hack::onLogin);
    }

    public void onUnload() {
        this.hacks.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.hacks.forEach(Hack::onUnload);
    }

    public void onUnloadPost() {
        for (Hack hack : this.hacks) {
            hack.disable();
        }
    }

    public void onKeyDown(int key) {
        if (key == 0 || mc.currentScreen instanceof WurstplusGui) {
            return;
        }
        for (Hack hack : this.hacks) {
            if (hack.getBind() == key) {
                hack.toggle();
            }
        }
    }

    public List<Hack.Category> getCategories() {
        return Arrays.asList(Hack.Category.values());
    }

    public List<Hack> getHacksByCategory(Hack.Category cat) {
        List<Hack> hacks = new ArrayList<>();
        for (Hack hack : this.hacks) {
            if (hack.getCategory() == cat) {
                hacks.add(hack);
            }
        }
        return hacks;
    }

}
