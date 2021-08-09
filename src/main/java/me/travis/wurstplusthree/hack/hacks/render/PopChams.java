package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

@Hack.Registration(name = "Pop Chams", description = "shows ez logs", category = Hack.Category.RENDER)
public class PopChams extends Hack {

    public static PopChams INSTANCE;

    public PopChams() {
        INSTANCE = this;
    }

    public EnumSetting mode = new EnumSetting("Mode", "Wire", Arrays.asList("Model", "Wire", "Shine", "WireModel"), this);
    public DoubleSetting width = new DoubleSetting("Width", 2.0, 0.0, 5.0, this, v -> mode.is("Wire"));
    public BooleanSetting texture = new BooleanSetting("Texture", false, this);
    public BooleanSetting lighting = new BooleanSetting("Lighting", false, this);
    public BooleanSetting blend = new BooleanSetting("Blend", false, this);
    public BooleanSetting transparent = new BooleanSetting("Transparent", false, this);
    public BooleanSetting depth = new BooleanSetting("Depth", false, this);
    public BooleanSetting xqz = new BooleanSetting("xqz", false, this);
    private final IntSetting yTravel = new IntSetting("Y Travel", 0, -10, 10, this);
    private final IntSetting aliveTicks = new IntSetting("Alive Ticks", 20, 0, 100, this);

    public HashMap<EntityPlayer, Integer> poppedPlayers = new HashMap<>();

    @Override
    public void onRender3D(Render3DEvent event) {
        List<EntityPlayer> playersToRemove = new ArrayList<>();
        for (Map.Entry<EntityPlayer, Integer> player : poppedPlayers.entrySet()) {
            if (player.getValue() > aliveTicks.getValue()) {
                playersToRemove.add(player.getKey());
            }
            player.getKey().posY += yTravel.getValue();
            mc.renderManager.renderEntityStatic(player.getKey(), event.getPartialTicks(), false);
            player.setValue(player.getValue() + 1);
        }
        for (EntityPlayer player : playersToRemove) {
            poppedPlayers.remove(player);
        }
    }
}
