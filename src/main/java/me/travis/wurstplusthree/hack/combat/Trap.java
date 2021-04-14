package me.travis.wurstplusthree.hack.combat;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class Trap extends Hack {

    public Trap() {
        super("Trap", "traps people", Category.COMBAT, false, false);
    }

    EnumSetting mode = new EnumSetting("Mode", "Extra", Arrays.asList("Extra", "Face", "Normal", "Feet"),this);
    IntSetting blocksPerTick = new IntSetting("Speed", 4, 0, 8, this);
    BooleanSetting rotate = new BooleanSetting("Rotate", true, this);

    private final Vec3d[] offsetsDefault = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsetsFace = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsetsFeet = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsetsExtra = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 0.0),
            new Vec3d(0.0, 4.0, 0.0)
    };

}
