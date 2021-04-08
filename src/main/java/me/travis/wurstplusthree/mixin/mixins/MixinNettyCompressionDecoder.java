package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value={NettyCompressionDecoder.class})
public abstract class MixinNettyCompressionDecoder {
    @ModifyConstant(method={"decode"}, constant={@Constant(intValue=0x200000)})
    private int decodeHook(int n) {
        return n;
    }
}

