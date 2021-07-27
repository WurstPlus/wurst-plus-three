package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.RenderItemEvent;
import me.travis.wurstplusthree.hack.hacks.render.NoRender;
import me.travis.wurstplusthree.hack.hacks.render.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
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

    @Inject(
            method = "transformEatFirstPerson",
            at = @At("HEAD"),
            cancellable = true
    )

    private void renderEating(float y, EnumHandSide hand, ItemStack stack, CallbackInfo ci){
        if(ViewModel.INSTANCE.isEnabled()) {
            float f = (float) this.mc.player.getItemInUseCount() - y + 1.0F;
            float f1 = f / (float) stack.getMaxItemUseDuration();

            if (f1 < 0.8F) {
                float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
                GlStateManager.translate(0.0F, f2, 0.0F);
            }

            float f3 = 1.0F - (float) Math.pow(f1, 27.0D);
            int i = hand == EnumHandSide.RIGHT ? 1 : -1;
            GlStateManager.translate(
                    f3 * 0.6F * (float) i * ViewModel.INSTANCE.mainX.getValue(),
                    f3 * -0.5F * -ViewModel.INSTANCE.mainY.getValue(),
                    f3 * 0.0F * ViewModel.INSTANCE.mainZ.getValue()
            );
            GlStateManager.rotate((float) i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((float) i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
            ci.cancel();
        }
    }

    @Redirect(
            method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At(value = "INVOKE",
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
            GlStateManager.translate(event.getMainX(), event.getMainY(), event.getMainZ());
            GlStateManager.scale(event.getMainHandScaleX(), event.getMainHandScaleY(), event.getMainHandScaleZ());
            GlStateManager.rotate((float) event.getMainRAngel(), (float) event.getMainRx(), (float) event.getMainRy(), (float) event.getMainRz());
        } else {
            GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
            GlStateManager.scale(event.getOffHandScaleX(), event.getOffHandScaleY(), event.getOffHandScaleZ());
            GlStateManager.rotate((float) event.getOffRAngel(), (float) event.getOffRx(), (float) event.getOffRy(), (float) event.getOffRz());
        }
    }

}

