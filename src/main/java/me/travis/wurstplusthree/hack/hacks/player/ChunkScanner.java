package me.travis.wurstplusthree.hack.hacks.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.mixin.mixins.accessors.IChunkProviderClient;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.ClientMessage;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.io.IOException;

/**
 * @author Madmegsox1
 * @since 15/07/2021
 */

@Hack.Registration(name = "Chunk Scanner", description = "Finds coords thx to azrn", category = Hack.Category.PLAYER)
public class ChunkScanner extends Hack {

    IntSetting delay = new IntSetting("Delay", 200, 0, 1000, this);
    IntSetting loop = new IntSetting("Loop Per Tick", 1, 1, 100, this);
    IntSetting startX = new IntSetting("Start X", 0, 0, 1000000, this);
    IntSetting startZ = new IntSetting("Start Z", 0, 0, 1000000, this);
    BooleanSetting saveCoords = new BooleanSetting("Save Coords", true ,this);

    private BlockPos playerPos = null;
    private int renderDistanceDiameter = 0;
    private long time = 0;
    private int count = 0;
    private int x, z;



    @Override
    public void onUpdate() {
        playerPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);

        if (renderDistanceDiameter == 0) {
            IChunkProviderClient chunkProviderClient;
            chunkProviderClient = (IChunkProviderClient) mc.world.getChunkProvider();
            renderDistanceDiameter = (int) Math.sqrt(chunkProviderClient.getLoadedChunks().size());
        }

        if (time == 0) {
            time = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - time > delay.getValue()) {
            for(int i =0; i < loop.getValue(); i++) {
                final int x = getSpiralCoords(count)[0] * renderDistanceDiameter * 16 + startX.getValue();
                final int z = getSpiralCoords(count)[1] * renderDistanceDiameter * 16 + startZ.getValue();
                final BlockPos position = new BlockPos(x, 0, z);
                this.x = x;
                this.z = z;
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, playerPos, EnumFacing.EAST));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, position, EnumFacing.EAST));
                this.playerPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
                time = System.currentTimeMillis();
                count++;
            }
        }
    }

    @CommitEvent
    public final void CPacketEvent(PacketEvent.Receive event){
        if(event.getPacket() instanceof SPacketBlockChange) {
            final int x = ((SPacketBlockChange) event.getPacket()).getBlockPosition().getX();
            final int z = ((SPacketBlockChange) event.getPacket()).getBlockPosition().getZ();
            IChunkProviderClient chunkProviderClient;
            chunkProviderClient = (IChunkProviderClient) mc.world.getChunkProvider();
            for (Chunk chunk : chunkProviderClient.getLoadedChunks().values()) {
                if (chunk.x == x / 16 || chunk.z == z / 16) {
                    return;
                }
            }

            String text = ChatFormatting.DARK_RED + "[" + ChatFormatting.RED + "CE" + ChatFormatting.DARK_RED + "] " + ChatFormatting.RESET;

            ClientMessage.sendMessage(text + "Player found at X: "+ ChatFormatting.GREEN + x + ChatFormatting.RESET + " Z: " + ChatFormatting.GREEN + z);
            try {
                if(saveCoords.getValue()) {
                    WurstplusThree.CONFIG_MANAGER.saveCoords("X: " + x + " Z: " + z);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] getSpiralCoords(int n) {
        int x = 0;
        int z = 0;
        int d = 1;
        int lineNumber = 1;
        int[] coords = {0, 0};
        for (int i = 0; i < n; i++) {
            if (2 * x * d < lineNumber) {
                x += d;
                coords = new int[]{x, z};
            } else if (2 * z * d < lineNumber) {
                z += d;
                coords = new int[]{x, z};
            } else {
                d *= -1;
                lineNumber++;
                n++;
            }
        }
        return coords;
    }


    @Override
    public void onEnable() {
        playerPos = null;
        count = 0;
        time = 0;
    }

    @Override
    public void onDisable() {
        playerPos = null;
        count = 0;
        time = 0;
    }

    @Override
    public String getDisplayInfo(){
        return x + " , " + z;
    }
}
