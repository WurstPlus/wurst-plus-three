package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.commands.ClipBind;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.KeyUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Madmegsox1
 * @since 01/06/2021
 * - shit code from my old client
 */

@Hack.Registration(name = "Auto Clip", description = "Clips ppl when you kill them", category = Hack.Category.MISC, isListening = false)
public class AutoClip extends Hack {

    public static AutoClip INSTANCE;

    public AutoClip() {
        INSTANCE = this;
    }

    DoubleSetting delay = new DoubleSetting("Delay", 2.0, 0.0, 20.0, this);
    BooleanSetting test = new BooleanSetting("Test Bind", false, this);

    private int delayCount;
    private boolean shouldClip;
    private String target;
    public final ConcurrentHashMap<String, Integer> targets = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        this.delayCount = 0;
        this.shouldClip = false;
    }

    @Override
    public void onUpdate() {
        if(test.getValue()){
            KeyUtil.clip(ClipBind.getKeys());
            test.setValue(false);
        }
        if(shouldClip){
            this.Clip();
            delayCount++;
        }
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.getHealth() <= 0) {
                    if (targets.containsKey(player.getName())) {
                        this.shouldClip = true;
                        target = player.getName();
                        this.targets.remove(player.getName());
                    }
                }
            }
        }
        targets.forEach((name, timeout) -> {
            if (timeout <= 0) {
                targets.remove(name);
            } else {
                targets.put(name, timeout - 1);
            }

        });
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer && !WurstplusThree.FRIEND_MANAGER.isFriend(event.getEntityPlayer().getName())) {
            this.targets.put(event.getTarget().getName(), 20);
        }
    }

    @CommitEvent
    public void onSendAttackPacket(PacketEvent.Send event) {
        CPacketUseEntity packet;
        if (event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityPlayer && !WurstplusThree.FRIEND_MANAGER.isFriend(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).getName())) {
            this.targets.put(Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).getName(), 20);
        }
    }

    private void Clip() {
        if (this.delayCount > delay.getValue() * 20) {
            delayCount = 0;
            ClientMessage.sendMessage("Clipping " + target);
            target = "";
            KeyUtil.clip(ClipBind.getKeys());
            this.shouldClip = false;
        }
    }
}
