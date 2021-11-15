



package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.*;
import me.earth.phobos.features.setting.*;
import me.earth.phobos.event.events.*;
import net.minecraft.init.*;
import me.earth.phobos.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.earth.phobos.util.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.*;

public class FastPlace extends Module
{
    private final Setting<Boolean> all;
    private final Setting<Boolean> obby;
    private final Setting<Boolean> enderChests;
    private final Setting<Boolean> crystals;
    private final Setting<Boolean> exp;
    private final Setting<Boolean> Minecart;
    private final Setting<Boolean> feetExp;
    private final Setting<Boolean> fastCrystal;
    private BlockPos mousePos;
    
    public FastPlace() {
        super("FastPlace",  "Fast everything.",  Module.Category.PLAYER,  true,  false,  false);
        this.all = (Setting<Boolean>)this.register(new Setting("All", false));
        this.obby = (Setting<Boolean>)this.register(new Setting("Obsidian", false,  v -> !this.all.getValue()));
        this.enderChests = (Setting<Boolean>)this.register(new Setting("EnderChests", false,  v -> !this.all.getValue()));
        this.crystals = (Setting<Boolean>)this.register(new Setting("Crystals", false,  v -> !this.all.getValue()));
        this.exp = (Setting<Boolean>)this.register(new Setting("Experience", false,  v -> !this.all.getValue()));
        this.Minecart = (Setting<Boolean>)this.register(new Setting("Minecarts", false,  v -> !this.all.getValue()));
        this.feetExp = (Setting<Boolean>)this.register(new Setting("ExpFeet", false));
        this.fastCrystal = (Setting<Boolean>)this.register(new Setting("PacketCrystal", false));
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.feetExp.getValue()) {
            final boolean mainHand = FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE;
            final boolean offHand = FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE;
            if (FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown() && ((FastPlace.mc.player.getActiveHand() == EnumHand.MAIN_HAND && mainHand) || (FastPlace.mc.player.getActiveHand() == EnumHand.OFF_HAND && offHand))) {
                Phobos.rotationManager.lookAtVec3d(FastPlace.mc.player.getPositionVector());
            }
        }
    }
    
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        if (InventoryUtil.holdingItem(ItemExpBottle.class) && this.exp.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(BlockObsidian.class) && this.obby.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(BlockEnderChest.class) && this.enderChests.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(ItemMinecart.class) && this.Minecart.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.all.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (this.crystals.getValue() || this.all.getValue())) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if (this.fastCrystal.getValue() && FastPlace.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            final boolean offhand = FastPlace.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            if (offhand || FastPlace.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                final RayTraceResult result = FastPlace.mc.objectMouseOver;
                if (result == null) {
                    return;
                }
                switch (result.typeOfHit) {
                    case MISS: {
                        this.mousePos = null;
                        break;
                    }
                    case BLOCK: {
                        this.mousePos = FastPlace.mc.objectMouseOver.getBlockPos();
                        break;
                    }
                    case ENTITY: {
                        final Entity entity;
                        if (this.mousePos == null || (entity = result.entityHit) == null) {
                            break;
                        }
                        if (!this.mousePos.equals((Object)new BlockPos(entity.posX,  entity.posY - 1.0,  entity.posZ))) {
                            break;
                        }
                        FastPlace.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.mousePos,  EnumFacing.DOWN,  offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  0.0f,  0.0f,  0.0f));
                        break;
                    }
                }
            }
        }
    }
}
