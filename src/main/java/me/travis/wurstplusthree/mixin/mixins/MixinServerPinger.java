package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.client.network.ServerPinger;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ServerPinger.class})
public class MixinServerPinger {
}

