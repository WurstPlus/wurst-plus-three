package me.travis.wurstplusthree.event.events;

import me.travis.wurstplusthree.event.EventStage;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * @author Madmegsox1
 * @since 04/05/2021
 */

public class BlockCollisionBoundingBoxEvent extends EventStage {
    private BlockPos pos;
    private AxisAlignedBB _boundingBox;

    public BlockCollisionBoundingBoxEvent(BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return _boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox)
    {
        this._boundingBox = boundingBox;
    }
}
