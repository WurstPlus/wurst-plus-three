package me.travis.wurstplusthree.hack.hacks.render;

import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.ColorUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Madmegsox1
 * @since 19/07/2021
 */

@Hack.Registration(name = "Pearl Tracer", category = Hack.Category.RENDER, description = "Draws shit", priority = HackPriority.Lowest)
public class PearlTracer extends Hack{

    BooleanSetting chat = new BooleanSetting("Chat", true, this);
    BooleanSetting render = new BooleanSetting("Render", true, this);
    IntSetting aliveTime = new IntSetting("Alive Time", 5, 0, 20, this);
    DoubleSetting thick = new DoubleSetting("Thick", 3.0, 0.0, 10.0, this);
    IntSetting rDelay = new IntSetting("RDelay", 120, 0, 360, this);
    ColourSetting color = new ColourSetting("Color", new Colour(255,255,255), this);


    private final HashMap<UUID, List<Vec3d>> poses = new HashMap<>();
    private final HashMap<UUID, Double> time = new HashMap<>();

    @Override
    public void onUpdate(){
        UUID toRemove = null;
        for(UUID uuid : time.keySet()){
            if(time.get(uuid) <= 0){
                poses.remove(uuid);
                toRemove = uuid;
            }else {
                time.replace(uuid, time.get(uuid) - 0.05);
            }
        }
        if(toRemove != null){
            time.remove(toRemove);
            toRemove = null;
        }

        for(Entity e : mc.world.getLoadedEntityList()){
            if(!(e instanceof EntityEnderPearl))continue;
            if(!this.poses.containsKey(e.getUniqueID())){
                if(chat.getValue()){
                    for (EntityPlayer entityPlayer : this.mc.world.playerEntities) {
                        if (entityPlayer.getDistance(e) < 4.0F && !entityPlayer.getName().equals(this.mc.player.getName())) {
                            ClientMessage.sendMessage(entityPlayer.getName() + " just through a pearl!");
                            break;
                        }
                    }
                }
                this.poses.put(e.getUniqueID(), new ArrayList<>(Collections.singletonList(e.getPositionVector())));
                this.time.put(e.getUniqueID(), (double) aliveTime.getValue());
            }else {
                this.time.replace(e.getUniqueID(), (double) aliveTime.getValue());
                List<Vec3d> v = this.poses.get(e.getUniqueID());
                v.add(e.getPositionVector());
            }
        }

    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(!render.getValue() && !poses.isEmpty())return;
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(thick.getAsFloat());

        for(UUID uuid : poses.keySet()){
            if(poses.get(uuid).size() <= 2)continue;
            int delay = 0;
            GL11.glBegin(1);
            for (int i = 1; i < poses.get(uuid).size(); ++i) {
                delay += rDelay.getValue();
                Color c = color.getRainbow() ? ColorUtil.releasedDynamicRainbow(delay, color.getValue().getAlpha()) : color.getValue();
                GL11.glColor4d(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);

                List<Vec3d> pos = poses.get(uuid);
                GL11.glVertex3d(pos.get(i).x - mc.getRenderManager().viewerPosX,pos.get(i).y - mc.getRenderManager().viewerPosY, pos.get(i).z - mc.getRenderManager().viewerPosZ);
                GL11.glVertex3d(pos.get(i - 1).x - mc.getRenderManager().viewerPosX,pos.get(i - 1).y - mc.getRenderManager().viewerPosY, pos.get(i - 1).z - mc.getRenderManager().viewerPosZ);
            }
            GL11.glEnd();
        }

        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
