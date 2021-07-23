package me.travis.wurstplusthree.hack.hacks.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.PushEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

/**
 * @author Madmegsox1
 * @since 23/07/2021
 */
@Hack.Registration(name = "Velocity", description = "Anti KB", category = Hack.Category.PLAYER, priority = HackPriority.Low)
public final class Velocity extends Hack {

    public static Velocity INSTANCE;
    public Velocity(){
        INSTANCE = this;
    }

    IntSetting v = new IntSetting("Vertical", 0, 0, 100, this);
    IntSetting h = new IntSetting("Horizontal", 0, 0, 100, this);

    BooleanSetting explosions = new BooleanSetting("Explosions", true ,this);
    BooleanSetting fishHook = new BooleanSetting("Fish Hook", true, this);
    public BooleanSetting noPush = new BooleanSetting("No Push", true, this);


    @CommitEvent
    public void pushEvent(PushEvent event){
        if(nullCheck())return;
        if(!noPush.getValue())return;
        if (event.getStage() == 0) {
            event.x = event.x * 0;
            event.y = (0);
            event.z = event.z * 0;
        }
        if(event.getStage() == 1){
            event.setCancelled(true);
        }
        if(event.getStage() == 2 && event.entity == mc.player){
            event.setCancelled(true);
        }
    }


    @CommitEvent
    public void SPacket(PacketEvent.Receive event){
        if(nullCheck())return;
        if(event.getPacket() instanceof SPacketEntityStatus && this.fishHook.getValue()){
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 31)
            {
                final Entity entity = packet.getEntity(Minecraft.getMinecraft().world);
                if (entity != null && entity instanceof EntityFishHook)
                {
                    final EntityFishHook fishHook = (EntityFishHook) entity;
                    if (fishHook.caughtEntity == Minecraft.getMinecraft().player)
                    {
                        event.setCancelled(true);
                    }
                }
            }

        }

        if (event.getPacket() instanceof SPacketEntityVelocity) {
            final SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.player.getEntityId()){
                if (this.h.getValue() == 0 && this.v.getValue() == 0)
                {
                    event.setCancelled(true);
                    return;
                }

                if (this.h.getValue() != 100)
                {
                    packet.motionX = packet.motionX / 100 * this.h.getValue();
                    packet.motionZ = packet.motionZ / 100 * this.h.getValue();
                }

                if (this.v.getValue() != 100)
                {
                    packet.motionY = packet.motionY / 100 * this.v.getValue();
                }
            }

        }

        if (event.getPacket() instanceof SPacketExplosion && this.explosions.getValue())
        {
            final SPacketExplosion packet = (SPacketExplosion) event.getPacket();

            if (this.h.getValue() == 0 && this.v.getValue() == 0)
            {
                packet.motionX *= 0;
                packet.motionY *= 0;
                packet.motionZ *= 0;
                return;
            }

            if (this.h.getValue() != 100)
            {
                packet.motionX = packet.motionX / 100 * this.h.getValue();
                packet.motionZ = packet.motionZ / 100 * this.h.getValue();
            }

            if (this.v.getValue() != 100)
            {
                packet.motionY = packet.motionY / 100 * this.v.getValue();
            }
        }
    }


    @Override
    public String getDisplayInfo(){
        return String.format("H:%s%% V:%s%%", this.h.getValue(), this.v.getValue());
    }


}
