package me.travis.wurstplusthree.gui.components;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.settings.ColorComponent;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.theme.Theme;
import me.travis.wurstplusthree.hack.client.GuiOld;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import net.minecraft.util.text.TextFormatting;

public class ColourComponent extends ColorComponent {

    public ColourComponent(Theme theme, ColourSetting setting, Toggleable colorToggle, Animation animation) {
        super(TextFormatting.BOLD + setting.getName(), null, theme.getContainerRenderer(), animation, theme.getComponentRenderer(), setting, true, true, colorToggle);

        if (setting != GuiOld.INSTANCE.enabledColor) addComponent(new SyncButton(theme.getComponentRenderer()));
    }

    private class SyncButton extends FocusableComponent {

        public SyncButton(Renderer renderer) {
            super("Sync Color", null, renderer);
        }

        @Override
        public void render(Context context) {
            super.render(context);
            renderer.overrideColorScheme(overrideScheme);
            renderer.renderTitle(context, title, hasFocus(context), false);
            renderer.restoreColorScheme();
        }

        @Override
        public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (button == Interface.LBUTTON && context.isClicked()) {
                setting.setValue(GuiOld.INSTANCE.enabledColor.getValue());
                setting.setRainbow(GuiOld.INSTANCE.enabledColor.getRainbow());
            }
        }
    }

}
