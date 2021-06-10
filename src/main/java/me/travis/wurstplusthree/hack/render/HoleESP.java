package me.travis.wurstplusthree.hack.render;

import com.google.common.collect.Sets;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Hack.Registration(name = "Hole ESP", description = "shows holes", category = Hack.Category.RENDER, isListening = false)
public class HoleESP extends Hack {

    IntSetting range = new IntSetting("Range", 5, 1, 20, this);
    EnumSetting customHoles = new EnumSetting("Show", "Single", Arrays.asList("Single", "Double"), this);
    EnumSetting mode = new EnumSetting("Render", "Pretty", Arrays.asList("Pretty", "Solid", "Outline", "Gradient", "Fade"), this);
    DoubleSetting Height = new DoubleSetting("Height", 1.0, -1.0, 2.0, this, s -> !mode.is("Fade"));
    DoubleSetting lineWidth = new DoubleSetting("Line Width", 1.0, -1.0, 2.0, this, s -> !mode.is("Fade"));
    BooleanSetting hideOwn = new BooleanSetting("Hide Own", false, this);
    ColourSetting bedrockColor = new ColourSetting("Bedrock Color", new Colour(0, 255, 0, 100), this);
    ColourSetting bedrockColor2 = new ColourSetting("Bedrock Outline", new Colour(0, 255, 0, 100), this);
    ColourSetting obsidianColor = new ColourSetting("Obsidian Color", new Colour(255, 0, 0, 100), this);
    ColourSetting obsidianColor2 = new ColourSetting("Obsidian Outline", new Colour(255, 0, 0, 100), this);
    EnumSetting RMode = new EnumSetting("Color Mode", "Rainbow", Arrays.asList("Rainbow", "Sin"), this);
    EnumSetting SinMode = new EnumSetting("Sine Mode", "Special", Arrays.asList("Special", "Hue", "Saturation", "Brightness"),this, s -> RMode.is("Sin"));
    IntSetting RDelay = new IntSetting("Rainbow Delay", 500, 0, 2500, this);
    IntSetting FillUp = new IntSetting("Fill Up", 80, 0, 255, this, s -> mode.is("Fade"));
    IntSetting FillDown = new IntSetting("Fill Down", 0, 0, 255, this, s -> mode.is("Fade"));
    IntSetting LineFillUp = new IntSetting("Line Fill Up", 80, 0, 255, this, s -> mode.is("Fade"));
    IntSetting LineFillDown = new IntSetting("Line Fill Down", 0, 0, 255, this, s -> mode.is("Fade"));
    BooleanSetting invertLine = new BooleanSetting("Invert Line", false, this, s -> mode.is("Fade"));
    BooleanSetting invertFill = new BooleanSetting("Invert Fill", false, this, s -> mode.is("Fade"));


    private final ConcurrentHashMap<BlockPos, Pair<Colour, Boolean>> holes = new ConcurrentHashMap<>();

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.holes.isEmpty()) return;
        this.holes.forEach(this::renderHoles);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        this.holes.clear();
        int range = this.range.getValue();

        HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                possibleHoles.add(pos);
            }
        }

        for (BlockPos pos : possibleHoles) {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null)
                    continue;

                Colour colour;
                if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                    colour = bedrockColor.getValue();
                } else {
                    colour = obsidianColor.getValue();
                }
                boolean safe = (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE);
                if (customHoles.is("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                    Pair<Colour, Boolean> p = new Pair<>(colour, safe);
                    holes.put(pos, p);
                } else if (customHoles.is("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                    Pair<Colour, Boolean> p = new Pair<>(colour, safe);
                    holes.put(pos, p);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    Pair<Colour, Boolean> p = new Pair<>(colour, safe);
                    holes.put(pos, p);
                }
            }

        }
    }

    private void renderHoles(BlockPos hole, Pair<Colour, Boolean> pair) {
        boolean safe = pair.getValue();
        if (hideOwn.getValue() && hole.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))) {
            return;
        }
        if (!mode.is("Gradient") && !mode.is("Fade")) {
            boolean outline = false;
            boolean solid = false;

            switch (mode.getValue()) {
                case "Pretty":
                    outline = true;
                    solid = true;
                    break;
                case "Solid":
                    outline = false;
                    solid = true;
                    break;
                case "Outline":
                    outline = true;
                    solid = false;
                    break;
            }
            RenderUtil.drawBoxESP(hole, safe ? bedrockColor.getValue() : obsidianColor.getValue(), safe ? bedrockColor2.getValue() : obsidianColor2.getValue(), lineWidth.getValue(), outline, solid, (float) (Height.getValue() - 1));
        } else {
            if (mode.is("Gradient")) {
                RenderUtil.drawGlowBox(hole, Height.getValue() - 1, lineWidth.getValue().floatValue(), safe ? bedrockColor.getValue() : obsidianColor.getValue(), safe ? bedrockColor2.getValue() : obsidianColor2.getValue());
            } else {
                RenderUtil.drawOpenGradientBox(hole, (!invertFill.getValue()) ? getGColor(safe, false, false) : getGColor(safe, true, false),
                        (!invertFill.getValue()) ? getGColor(safe, true, false) : getGColor(safe, false, false), 0);
                RenderUtil.drawGradientBlockOutline(hole, (invertLine.getValue()) ? getGColor(safe, false, true) : getGColor(safe, true, true),
                        (invertLine.getValue()) ? getGColor(safe, true, true) : getGColor(safe, false, true), 2f, 0);
            }
        }
    }

    private Color getGColor(boolean safe, boolean top, boolean line) {
        Color rVal;
        ColorUtil.type type = null;
        switch (SinMode.getValue()){
            case "Special":
                type = ColorUtil.type.SPECIAL;
                break;
            case "Saturation":
                type = ColorUtil.type.SATURATION;
                break;
            case "Brightness":
                type = ColorUtil.type.BRIGHTNESS;
                break;
        }

        if (!safe) {
            if (obsidianColor.getRainbow()) {
                if (RMode.is("Rainbow")) {
                    if (top) {
                        rVal = ColorUtil.releasedDynamicRainbow(0, (line) ? LineFillUp.getValue() : FillUp.getValue());
                    } else {
                        rVal = ColorUtil.releasedDynamicRainbow(RDelay.getValue(), (line) ? LineFillDown.getValue() : FillDown.getValue());
                    }
                }else {
                    if(SinMode.is("Hue")){
                        if (top) {
                            rVal = ColorUtil.getSinState(obsidianColor.getColor(),obsidianColor2.getColor() ,1000,(line) ? LineFillUp.getValue() : FillUp.getValue());
                        }else {
                            rVal = ColorUtil.getSinState(obsidianColor.getColor(),obsidianColor2.getColor() ,RDelay.getValue(), (line) ? LineFillDown.getValue() : FillDown.getValue());
                        }
                    }else {
                        if (top) {
                            rVal = ColorUtil.getSinState(obsidianColor.getColor(), 1000, (line) ? LineFillUp.getValue() : FillUp.getValue(), type);
                        } else {
                            rVal = ColorUtil.getSinState(obsidianColor.getColor(), RDelay.getValue(), (line) ? LineFillDown.getValue() : FillDown.getValue(), type);
                        }
                    }
                }
            } else {
                if (top) {
                    rVal = new Colour(obsidianColor.getColor().getRed(), obsidianColor.getColor().getGreen(), obsidianColor.getColor().getBlue(), (line) ? LineFillUp.getValue() : FillUp.getValue());
                } else {
                    rVal = new Colour(obsidianColor.getColor().getRed(), obsidianColor.getColor().getGreen(), obsidianColor.getColor().getBlue(), (line) ? LineFillDown.getValue() : FillDown.getValue());
                }
            }
        } else {
            if (bedrockColor.getRainbow()) {
                if (RMode.is("Rainbow")) {
                    if (top) {
                        rVal = ColorUtil.releasedDynamicRainbow(0, (line) ? LineFillUp.getValue() : FillUp.getValue());
                    } else {
                        rVal = ColorUtil.releasedDynamicRainbow(RDelay.getValue(), (line) ? LineFillDown.getValue() : FillDown.getValue());
                    }
                }else {
                    if(SinMode.is("Hue")){
                        if (top) {
                            rVal = ColorUtil.getSinState(bedrockColor.getColor(),bedrockColor2.getColor() ,1000,(line) ? LineFillUp.getValue() : FillUp.getValue());
                        }else {
                            rVal = ColorUtil.getSinState(bedrockColor.getColor(),bedrockColor2.getColor() ,RDelay.getValue(), (line) ? LineFillDown.getValue() : FillDown.getValue());
                        }
                    }else {
                        if (top) {
                            rVal = ColorUtil.getSinState(bedrockColor.getColor(), 1000, (line) ? LineFillUp.getValue() : FillUp.getValue(), type);
                        } else {
                            rVal = ColorUtil.getSinState(bedrockColor.getColor(), RDelay.getValue(), (line) ? LineFillDown.getValue() : FillDown.getValue(), type);
                        }
                    }
                }
            } else {
                if (top) {
                    rVal = new Colour(bedrockColor.getColor().getRed(), bedrockColor.getColor().getGreen(), bedrockColor.getColor().getBlue(), (line) ? LineFillUp.getValue() : FillUp.getValue());
                } else {
                    rVal = new Colour(bedrockColor.getColor().getRed(), bedrockColor.getColor().getGreen(), bedrockColor.getColor().getBlue(), (line) ? LineFillDown.getValue() : FillDown.getValue());
                }
            }
        }
        return rVal;
    }


    @Override
    public String getDisplayInfo() {
        return "" + holes.size();
    }

}
