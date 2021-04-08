package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={Biome.BiomeProperties.class})
public class MixinBiomeProperties {
}

