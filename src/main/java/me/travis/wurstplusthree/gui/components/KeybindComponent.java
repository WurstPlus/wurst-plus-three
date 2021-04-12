package me.travis.wurstplusthree.gui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.theme.Renderer;
import org.lwjgl.input.Keyboard;

public class KeybindComponent extends com.lukflug.panelstudio.settings.KeybindComponent {

    public KeybindComponent(Renderer renderer, KeybindSetting keybind) {
        super(renderer, keybind);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        context.setHeight(renderer.getHeight(false));

        if (hasFocus(context) && (scancode == Keyboard.KEY_DELETE || scancode == Keyboard.KEY_BACK)) {
            keybind.setKey(Keyboard.KEY_NONE);
            releaseFocus();
            return;
        }

        super.handleKey(context, scancode);
    }

}
