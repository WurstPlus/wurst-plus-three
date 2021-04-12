package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.hack.chat.ClearChatbox;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value={GuiNewChat.class})
public class MixinGuiNewChat
extends Gui {
    @Shadow
    @Final
    public List<ChatLine> drawnChatLines;
    private ChatLine chatLine;

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColour(int left, int top, int right, int bottom, int color) {
        if (!ClearChatbox.INSTANCE.isEnabled()) {
            Gui.drawRect(left, top, right, bottom, color);
        }
    }

}

