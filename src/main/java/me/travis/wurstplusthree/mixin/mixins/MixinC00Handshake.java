package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={C00Handshake.class})
public abstract class MixinC00Handshake {
    @Redirect(method={"writePacketData"}, at=@At(value="INVOKE", target="Lnet/minecraft/network/PacketBuffer;writeString(Ljava/lang/String;)Lnet/minecraft/network/PacketBuffer;"))
    public PacketBuffer writePacketDataHook(PacketBuffer packetBuffer, String string) {
        return packetBuffer.writeString(string);
    }
}

