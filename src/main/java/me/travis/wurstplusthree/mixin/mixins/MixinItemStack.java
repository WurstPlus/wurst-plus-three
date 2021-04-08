package me.travis.wurstplusthree.mixin.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemStack.class})
public abstract class MixinItemStack {
    @Shadow
    private int itemDamage;

    @Inject(method={"<init>(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)V"}, at={@At(value="RETURN")})
    @Dynamic
    private void initHook(Item item, int idkWhatDisIsIPastedThis, int dura, NBTTagCompound compound, CallbackInfo info) {
        this.itemDamage = this.checkDurability((ItemStack)ItemStack.class.cast(this), this.itemDamage, dura);
    }

    @Inject(method={"<init>(Lnet/minecraft/nbt/NBTTagCompound;)V"}, at={@At(value="RETURN")})
    private void initHook2(NBTTagCompound compound, CallbackInfo info) {
        this.itemDamage = this.checkDurability((ItemStack)ItemStack.class.cast(this), this.itemDamage, compound.getShort("Damage"));
    }

    private int checkDurability(ItemStack item, int damage, int dura) {
        int trueDura = damage;
        if (dura < 0) {
            trueDura = dura;
        }
        return trueDura;
    }
}

