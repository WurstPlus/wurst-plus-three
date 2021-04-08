package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={LayerBipedArmor.class})
public abstract class MixinLayerBipedArmor
extends LayerArmorBase<ModelBiped> {
    public MixinLayerBipedArmor(RenderLivingBase<?> rendererIn) {
        super(rendererIn);
    }
}

