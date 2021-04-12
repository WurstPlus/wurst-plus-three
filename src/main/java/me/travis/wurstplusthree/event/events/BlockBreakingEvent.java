package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.Event;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingEvent
        extends Event {
    public BlockPos pos;
    public int breakingID;
    public int breakStage;

    public BlockBreakingEvent(BlockPos pos, int breakingID, int breakStage) {
        this.pos = pos;
        this.breakingID = breakingID;
        this.breakStage = breakStage;
    }
}

