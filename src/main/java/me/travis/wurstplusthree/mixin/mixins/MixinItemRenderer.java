package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.RenderItemEvent;
import me.travis.wurstplusthree.hack.hacks.render.NoRender;
import me.travis.wurstplusthree.hack.hacks.render.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class}, priority = Integer.MAX_VALUE)
public abstract class MixinItemRenderer {

    Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = {"renderFireInFirstPerson"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.fire.getValue()) {
            info.cancel();
        }
    }

    @Redirect(
            method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"
            )
    )
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
        RenderItemEvent event = new RenderItemEvent(0.56F, -0.52F + y * -0.6F, -0.72F, -0.56F, -0.52F + y * -0.6F, -0.72F,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                1.0, 1.0, 1.0,
                1.0, 1.0, 1.0
        );
        WurstplusThree.EVENT_PROCESSOR.postEvent(event);
        if (hand == EnumHandSide.RIGHT) {
            GlStateManager.translate(
                    (!(mc.player.isHandActive() && mc.player.getItemInUseCount() > 0 && mc.player.getActiveHand() == EnumHand.MAIN_HAND && ViewModel.INSTANCE.fixEating.getValue())) ? event.getMainX() : 1 * 0.56F,
                    event.getMainY(),
                    (!(mc.player.isHandActive() && mc.player.getItemInUseCount() > 0 && mc.player.getActiveHand() == EnumHand.MAIN_HAND && ViewModel.INSTANCE.fixEating.getValue())) ? event.getMainZ() : -0.72F
            );
            GlStateManager.scale(event.getMainHandScaleX(), event.getMainHandScaleY(), event.getMainHandScaleZ());
            GlStateManager.rotate((float) event.getMainRAngel(), (float) event.getMainRx(), (float) event.getMainRy(), (float) event.getMainRz());
        } else {
            GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
            GlStateManager.scale(event.getOffHandScaleX(), event.getOffHandScaleY(), event.getOffHandScaleZ());
            GlStateManager.rotate((float) event.getOffRAngel(), (float) event.getOffRx(), (float) event.getOffRy(), (float) event.getOffRz());
        }
    }

}

