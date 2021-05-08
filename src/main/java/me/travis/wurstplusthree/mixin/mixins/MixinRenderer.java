package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Madmegsox1
 * @since 03/05/2021
 */

@Mixin(Render.class)
abstract class MixinRenderer<T extends Entity> {

    @Shadow
    protected abstract boolean bindEntityTexture(T entity);

    @Shadow
    protected abstract int getTeamColor(T entityIn);

    @Shadow
    protected boolean renderOutlines;

    @Shadow
    @Final
    protected RenderManager renderManager;
}