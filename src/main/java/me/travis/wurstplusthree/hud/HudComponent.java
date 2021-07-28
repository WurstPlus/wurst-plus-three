package me.travis.wurstplusthree.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.gui.component.components.BooleanComponent;
import me.travis.wurstplusthree.setting.Feature;
import me.travis.wurstplusthree.setting.Setting;
import net.minecraftforge.common.MinecraftForge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class HudComponent extends Feature {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Registration {
        String name();
        boolean enabled() default false;
        int x() default 20;
        int y() default 50;
    }

    private Registration getComponent(){
        return getClass().getAnnotation(Registration.class);
    }

    private final String name = getComponent().name();
    private boolean isEnabled = getComponent().enabled();
    private int x = getComponent().x();
    private int y = getComponent().y();

    public int getHeight() {
        return 20;
    }

    public int getWidth() {
        return 30;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void onEnable() {
    }

    public void renderComponent() {
    }

    public void renderComponent(int height, int width) {
        this.renderComponent();
    }

    public void onDisable() {
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void enable() {
        this.isEnabled = true;
        this.onEnable();
        MinecraftForge.EVENT_BUS.register(this);
        WurstplusThree.EVENT_PROCESSOR.addEventListener(this);
    }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        WurstplusThree.EVENT_PROCESSOR.removeEventListener(this);
        this.isEnabled = false;
        this.onDisable();
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled && !this.isEnabled()) {
            this.enable();
        }
        if (!enabled && this.isEnabled()) {
            this.disable();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayInfo() {
        return null;
    }

    public List<Setting> getSettings() {
        List<Setting> settings = new ArrayList<>();
        for (Setting setting : WurstplusThree.SETTINGS.getSettings()) {
            if (setting.getParent() == this) {
                settings.add(setting);
            }
        }
        return settings;
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.getSettings()) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + getWidth() && mouseY > y && mouseY < y + getHeight();
    }
}

