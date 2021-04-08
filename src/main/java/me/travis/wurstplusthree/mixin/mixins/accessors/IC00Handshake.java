package me.travis.wurstplusthree.mixin.mixins.accessors;

import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={C00Handshake.class})
public interface IC00Handshake {
    @Accessor(value="ip")
    public String getIp();

    @Accessor(value="ip")
    public void setIp(String var1);
}

