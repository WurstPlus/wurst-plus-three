package me.travis.wurstplusthree.event;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.*;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.GLUProjection;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Events implements Globals {

    private Object EventManager;

    public Events() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!nullCheck() && event.getEntity().getEntityWorld().isRemote && event.getEntityLiving().equals(mc.player)) {
            WurstplusThree.HACKS.onUpdate();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!nullCheck()) {
            WurstplusThree.HACKS.onTick();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (nullCheck()) {
            return;
        }
        if (event.getStage() == 0) {
            WurstplusThree.ROTATION_MANAGER.updateRotations();
            WurstplusThree.POS_MANAGER.updatePosition();
        }
        if (event.getStage() == 1) {
            WurstplusThree.ROTATION_MANAGER.restoreRotations();
            WurstplusThree.POS_MANAGER.restorePosition();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            ScaledResolution resolution = new ScaledResolution(mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            WurstplusThree.HACKS.onRender2D(render2DEvent);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        mc.profiler.startSection("wurstplus");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0f);
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        GLUProjection projection = GLUProjection.getInstance();
        IntBuffer viewPort = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projectionPort = GLAllocation.createDirectFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projectionPort);
        GL11.glGetInteger(2978, viewPort);
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        projection.updateMatrices(viewPort, modelView, projectionPort, (double) scaledResolution.getScaledWidth() / (double) mc.displayWidth, (double) scaledResolution.getScaledHeight() / (double) mc.displayHeight);
        WurstplusThree.HACKS.onRender3D(render3dEvent);
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        mc.profiler.endSection();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        WurstplusThree.HACKS.onLogout();
        WurstplusThree.POP_MANAGER.onLogout();
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        WurstplusThree.HACKS.onLogin();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(WurstplusThree.COMMANDS.getPrefix())) {
            event.setCanceled(true);
            try {
                mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                if (event.getMessage().length() > 1) {
                    WurstplusThree.COMMANDS.executeCommand(event.getMessage().substring(1));
                } else {
                    ClientMessage.sendErrorMessage("enter a command");
                }
            } catch (Exception e) {
                e.printStackTrace();
                ClientMessage.sendErrorMessage("error handling command");
            }
            event.setMessage("");
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0) {
            return;
        }
        WurstplusThree.SERVER_MANAGER.onPacketReceived();
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                MinecraftForge.EVENT_BUS.post(new TotemPopEvent(player));
                WurstplusThree.POP_MANAGER.onTotemPop(player);
            }
        }
    }



}
