package me.travis.wurstplusthree.hack.chat;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.DeathEvent;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AutoEz extends Hack {

    public static AutoEz INSTANCE;

    public AutoEz() {
        super("Auto Ez", "you just got nae nae'd by wurst plus THREE (kill me)", Category.CHAT, false);
        INSTANCE = this;
    }

    private int delayCount;
    public final ConcurrentHashMap targets = new ConcurrentHashMap();

    @Override
    public void onEnable() {
        this.delayCount = 0;
    }

    @Override
    public void onUpdate() {
        delayCount++;
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.getHealth() <= 0) {
                    if (targets.containsKey(player.getName())) {
                        this.announceDeath();
                        this.targets.remove(player.getName());
                    }
                }
            }
        }
        targets.forEach((name, timeout) -> {
            if ((int)timeout <= 0) {
                targets.remove(name);
            } else {
                targets.put(name, (int)timeout - 1);
            }

        });
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer && !WurstplusThree.FRIEND_MANAGER.isFriend(event.getEntityPlayer().getName())) {
            this.targets.put(event.getTarget(), 20);
        }
    }

    @SubscribeEvent
    public void onSendAttackPacket(PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityPlayer && !WurstplusThree.FRIEND_MANAGER.isFriend(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).getName())) {
            this.targets.put(packet.getEntityFromWorld(mc.world), 20);
        }
    }

    @SubscribeEvent
    public void onEntityDeath(DeathEvent event) {
        if (this.targets.containsKey(event.player)) {
            this.announceDeath();
            this.targets.remove(event.player);
        }
    }

    private void announceDeath() {
        if (this.delayCount < 150) return;
        delayCount = 0;
        mc.player.connection.sendPacket(new CPacketChatMessage("you just got nae nae'd by wurst+3"));
    }

}
