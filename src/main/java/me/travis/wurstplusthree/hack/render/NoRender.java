package me.travis.wurstplusthree.hack.render;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Hack.Registration(name = "No Render", description = "stops rendering things", category = Hack.Category.RENDER, isListening = false)
public class NoRender extends Hack {

    public static NoRender INSTANCE;

    public NoRender() {
        INSTANCE = this;
    }

    public BooleanSetting armour = new BooleanSetting("Armour", true, this);
    public BooleanSetting fire = new BooleanSetting("Fire", true, this);
    public BooleanSetting blind = new BooleanSetting("Blind", true, this);
    public BooleanSetting nausea = new BooleanSetting("Nausea", true, this);
    public BooleanSetting hurtcam = new BooleanSetting("Hurt Cam", true, this);
    public BooleanSetting skylight = new BooleanSetting("Sky Light", false, this);
    public BooleanSetting bossbar = new BooleanSetting("Bossbar", false, this);
    public BooleanSetting weather = new BooleanSetting("Weather", false, this);
    public BooleanSetting time = new BooleanSetting("Change Time", false, this);
    public BooleanSetting block = new BooleanSetting("Block", true , this);
    public BooleanSetting water = new BooleanSetting("Water", true, this);
    public IntSetting newTime = new IntSetting("Time", 0, 0, 23000, this, s -> time.getValue());

    @Override
    public void onUpdate() {
        if (blind.getValue() && mc.player.isPotionActive(MobEffects.BLINDNESS))
            mc.player.removePotionEffect(MobEffects.BLINDNESS);
        if (nausea.getValue() && mc.player.isPotionActive(MobEffects.NAUSEA))
            mc.player.removePotionEffect(MobEffects.NAUSEA);
        if (time.getValue()) {
            mc.world.setWorldTime((long) newTime.getValue());
        }
        if (weather.getValue())
            mc.world.setRainStrength(0f);
    }

    @SubscribeEvent
    public void renderBlockEvent(RenderBlockOverlayEvent event){
        if(block.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK){
            event.setCanceled(true);
        }
        if(water.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER){
            event.setCanceled(true);
        }
    }

    @CommitEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate && time.getValue()) {
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof SPacketUpdateBossInfo && bossbar.getValue()) {
            event.setCancelled(true);
        }
    }

    // retarded fix idk why it needs it
    @Override
    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

}
