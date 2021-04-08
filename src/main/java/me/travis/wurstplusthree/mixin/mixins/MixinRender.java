package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Render.class})
public class MixinRender<T extends Entity> {
    @Inject(method={"shouldRender"}, at={@At(value="HEAD")}, cancellable=true)
    public void shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> info) {
        if (livingEntity == null || camera == null || livingEntity.getRenderBoundingBox() == null) {
            info.setReturnValue(false);
        }
    }
}

