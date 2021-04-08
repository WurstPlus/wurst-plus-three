package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityBoat.class})
public abstract class MixinEntityBoat/* extends MixinEntity*/ {
    @Shadow
    public abstract double getMountedYOffset();

}

