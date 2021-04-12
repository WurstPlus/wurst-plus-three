package me.travis.wurstplusthree.gui;

import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.hud.HUDClickGUI;
import com.lukflug.panelstudio.mc12.GLInterface;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;
import com.lukflug.panelstudio.settings.*;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.theme.SettingsColorScheme;
import com.lukflug.panelstudio.theme.Theme;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.gui.components.ColourComponent;
import me.travis.wurstplusthree.gui.components.KeybindComponent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.client.Gui;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.*;

import java.awt.*;

public class WurstplusGui extends MinecraftHUDGUI {

    private final Toggleable colorToggle;
    private final GUIInterface guiInterface;
    private final Theme theme;
    private final HUDClickGUI gui;

    public WurstplusGui() {
        guiInterface = new GUIInterface(true) {
            @Override
            protected String getResourcePrefix() {
                return "wurstplusthree:gui/";
            }

            @Override
            public void drawString(Point pos, String s, Color c) {
                GLInterface.end();
                int x = pos.x + 3, y = pos.y + 2;
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(s, x, y, c.getRGB());
                GLInterface.begin();
            }

            @Override
            public int getFontWidth(String s) {
                return WurstplusThree.MENU_FONT_MANAGER.getTextWidth(s);
            }

            @Override
            public int getFontHeight() {
                return WurstplusThree.MENU_FONT_MANAGER.getTextHeight();
            }
        };
        colorToggle = new Toggleable() {
            @Override
            public void toggle() {

            }

            @Override
            public boolean isOn() {
                return false;
            }
        };
        ColorScheme scheme = new SettingsColorScheme(Gui.INSTANCE.enabledColor, Gui.INSTANCE.backgroundColor, Gui.INSTANCE.settingBackgroundColor, Gui.INSTANCE.outlineColor, Gui.INSTANCE.fontColor, Gui.INSTANCE.opacity);
        theme = new GameSenseTheme(scheme, 12, 2, 5);
        gui = new HUDClickGUI(guiInterface,null) {
            @Override
            public void handleScroll(int diff) {
                super.handleScroll(diff);
                if (Gui.INSTANCE.scrolling.getValue().equals("Screen")) {
                    for (FixedComponent component : components) {
                        if (!hudComponents.contains(component)) {
                            Point p = component.getPosition(guiInterface);
                            p.translate(0, -diff);
                            component.setPosition(guiInterface, p);
                        }
                    }
                }
            }
        };
        Point pos = new Point(10, 10);
        for (Hack.Category category : WurstplusThree.HACKS.getCategories()) {
            DraggableContainer panel = new DraggableContainer(category.name(), null, theme.getPanelRenderer(), new SimpleToggleable(false), new SettingsAnimation(Gui.INSTANCE.animationSpeed), null, new Point(pos), 100) {
                @Override
                protected int getScrollHeight(int childHeight) {
                    if (Gui.INSTANCE.scrolling.getValue().equals("Screen")) {
                        return childHeight;
                    }
                    return Math.min(childHeight, Math.max(12 * 4, WurstplusGui.this.height - getPosition(guiInterface).y - renderer.getHeight(open.getValue() != 0) - 12));
                }
            };
            gui.addComponent(panel);
            pos.translate(110, 0);
            for (Hack hack : WurstplusThree.HACKS.getHacksByCategory(category)) {
                addModule(panel, hack);
            }
        }
    }

    public void addModule(CollapsibleContainer panel, Hack hack) {
        CollapsibleContainer container = new CollapsibleContainer(hack.getName(), null, theme.getContainerRenderer(), new SimpleToggleable(false), new SettingsAnimation(Gui.INSTANCE.animationSpeed), new Toggleable() {
            @Override
            public void toggle() {
                hack.toggle();
            }
            @Override
            public boolean isOn() {
                return hack.isEnabled();
            }
        });
        panel.addComponent(container);
        for (Setting setting : hack.getSettings()) {
            if (setting instanceof BooleanSetting) {
                container.addComponent(new BooleanComponent(setting.getName(), null, theme.getComponentRenderer(), (BooleanSetting) setting));
            } else if (setting instanceof IntSetting) {
                container.addComponent(new NumberComponent(setting.getName(), null, theme.getComponentRenderer(), (IntSetting) setting, ((IntSetting) setting).getMin(), ((IntSetting) setting).getMax()));
            } else if (setting instanceof DoubleSetting) {
                container.addComponent(new NumberComponent(setting.getName(), null, theme.getComponentRenderer(), (DoubleSetting) setting, ((DoubleSetting) setting).getMin(), ((DoubleSetting) setting).getMax()));
            } else if (setting instanceof EnumSetting) {
                container.addComponent(new EnumComponent(setting.getName(), null, theme.getComponentRenderer(), (EnumSetting) setting));
            } else if (setting instanceof ColourSetting) {
                container.addComponent(new ColourComponent(theme, (ColourSetting) setting, colorToggle, new SettingsAnimation(Gui.INSTANCE.animationSpeed)));
            }
        }
        container.addComponent(new KeybindComponent(theme.getComponentRenderer(), new KeybindSetting() {
            @Override
            public int getKey() {
                return hack.getBind();
            }

            @Override
            public void setKey(int key) {
                hack.setBind(key);
            }

            @Override
            public String getKeyName() {
                return hack.getBindName();
            }
        }));
    }


    @Override
    protected com.lukflug.panelstudio.hud.HUDClickGUI getHUDGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return Gui.INSTANCE.scrollSpeed.getValue();
    }
}
