package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.hack.hacks.player.PlayerSpoofer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract int renderString(String var1, float var2, float var3, int var4, boolean var5);

    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);

    @Redirect(method={"drawString(Ljava/lang/String;FFIZ)I"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"))
    public int renderStringHook(FontRenderer fontrenderer, String text, float x, float y, int color, boolean dropShadow) {
        return this.renderString(text, x, y, color, dropShadow);
    }

    @Redirect(method={"renderString(Ljava/lang/String;FFIZ)I"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
        if(Minecraft.getMinecraft().player != null) {
            if (PlayerSpoofer.INSTANCE.isEnabled() && PlayerSpoofer.INSTANCE.name != null) {
                this.renderStringAtPos(text.replace(PlayerSpoofer.INSTANCE.getOldName(), PlayerSpoofer.INSTANCE.name), shadow);
            }
            else {
                this.renderStringAtPos(text, shadow);
            }
        }
        else {
            this.renderStringAtPos(text, shadow);
        }
    }
}

