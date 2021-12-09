package me.travis.wurstplusthree.mixin.mixins.accessors;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Madmegsox1
 * @since 15/07/2021
 */
@Mixin(value = ChunkProviderClient.class)
public interface IChunkProviderClient {
    @Accessor(value = "loadedChunks")
    public Long2ObjectMap<Chunk> getLoadedChunks();
}
