package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.chat.ClearChatbox;
import me.travis.wurstplusthree.hack.chat.CustomChat;
import me.travis.wurstplusthree.util.elements.Rainbow;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
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

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    private void overrideChatBackgroundColour(int left, int top, int right, int bottom, int color) {
        if (!ClearChatbox.INSTANCE.isEnabled()) {
            Gui.drawRect(left, top, right, bottom, color);
        }
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadowMaybe(FontRenderer fontRenderer, String message, float x, float y, int color) {
        if (!CustomChat.INSTANCE.isEnabled()) return fontRenderer.drawStringWithShadow(message, x, y, color);
        if (CustomChat.INSTANCE.customFont.getValue()) {
            if (CustomChat.INSTANCE.rainbow.getValue()) {
                WurstplusThree.GUI_FONT_MANAGER.drawStringRainbow(message, x, y, true);
            } else {
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(message, x, y, color);
            }
        } else {
            if (CustomChat.INSTANCE.rainbow.getValue()) {
                fontRenderer.drawStringWithShadow(message, x, y, Rainbow.getColour().getRGB());
            } else {
                fontRenderer.drawStringWithShadow(message, x, y, color);
            }
        }
        return 0;
    }

    @Redirect(method={"setChatLine"}, at=@At(value="INVOKE", target="Ljava/util/List;size()I", ordinal=0, remap=false))
    public int drawnChatLinesSize(List<ChatLine> list) {
        return CustomChat.INSTANCE.isEnabled() && CustomChat.INSTANCE.infinite.getValue() ? -2147483647 : list.size();
    }

    @Redirect(method={"setChatLine"}, at=@At(value="INVOKE", target="Ljava/util/List;size()I", ordinal=2, remap=false))
    public int chatLinesSize(List<ChatLine> list) {
        return CustomChat.INSTANCE.isEnabled() && CustomChat.INSTANCE.infinite.getValue() ? -2147483647 : list.size();
    }

}

