package me.travis.wurstplusthree.hack.misc;

import com.mojang.authlib.GameProfile;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;

import java.util.UUID;

public class FakePlayer extends Hack {

    public FakePlayer() {
        super("Fake Player", "spawns a dripped out fake player", Category.MISC, false);
    }

    final private ItemStack[] armour = new ItemStack[] {
            new ItemStack(Items.GOLDEN_BOOTS),
            new ItemStack(Items.GOLDEN_LEGGINGS),
            new ItemStack(Items.GOLDEN_CHESTPLATE),
            new ItemStack(Items.GOLDEN_HELMET)
    };

    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.disable();
            return;
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("dbc45ea7-e8bd-4a3e-8660-ac064ce58216"), "travis"));
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-1337, clonedPlayer);

        for(int i = 0; i < 4; i++) {
            ItemStack item = armour[i];
            item.addEnchantment(Enchantments.BLAST_PROTECTION,4);
            clonedPlayer.inventory.armorInventory.set(i, item);
        }

        clonedPlayer.onLivingUpdate();

    }

    @Override
    public void onDisable() {
        if (!nullCheck()) {
            mc.world.removeEntityFromWorld(-1337);
        }
    }
}
