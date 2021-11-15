Skip to content
Search or jump to…
Pull requests
Issues
Marketplace
Explore
 
@SubNuclear 
kyv3-v2
/
New-PerryPhobos-SRC
Public
1
166
Code
Issues
4
Pull requests
Actions
Projects
Wiki
Security
Insights
New-PerryPhobos-SRC/src/main/java/me/earth/phobos/features/modules/combat/AutoCrystal.java /
@kyv3-v2
kyv3-v2 m
Latest commit 26e9bc7 5 days ago
 History
 1 contributor
1707 lines (1631 sloc)  96.9 KB
   




package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.*;
import net.minecraft.entity.player.*;
import java.util.concurrent.atomic.*;
import me.earth.phobos.features.setting.*;
import net.minecraft.entity.item.*;
import net.minecraft.world.*;
import me.earth.phobos.*;
import net.minecraft.network.play.server.*;
import me.earth.phobos.features.modules.misc.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import java.awt.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.gameevent.*;
import me.earth.phobos.features.gui.*;
import me.earth.phobos.features.command.*;
import me.earth.phobos.event.events.*;
import java.util.concurrent.*;
import me.earth.phobos.util.*;
import org.lwjgl.input.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.state.*;
import com.mojang.authlib.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import io.netty.util.internal.*;
import java.util.*;

public class AutoCrystal extends Module
{
    public static EntityPlayer target;
    public static Set<BlockPos> lowDmgPos;
    public static Set<BlockPos> placedPos;
    public static Set<BlockPos> brokenPos;
    private static AutoCrystal instance;
    public final TimerUtil yawStepTimer;
    private final TimerUtil switchTimer;
    private final TimerUtil manualTimer;
    private final TimerUtil breakTimer;
    private final TimerUtil placeTimer;
    private final TimerUtil syncTimer;
    private final TimerUtil predictTimer;
    private final TimerUtil renderTimer;
    private final AtomicBoolean shouldInterrupt;
    private final TimerUtil syncroTimer;
    private final Map<EntityPlayer,  TimerUtil> totemPops;
    private final Queue<CPacketUseEntity> packetUseEntities;
    private final AtomicBoolean threadOngoing;
    private final List<RenderPos> positions;
    private final Setting<Settings> setting;
    public final Setting<Boolean> attackOppositeHand;
    public final Setting<Boolean> removeAfterAttack;
    public final Setting<Boolean> antiBlock;
    private final Setting<Integer> eventMode;
    private final Setting<Integer> switchCooldown;
    public Setting<Boolean> place;
    public Setting<Integer> placeDelay;
    public Setting<Float> placeRange;
    public Setting<Float> minDamage;
    public Setting<Float> maxSelfPlace;
    public Setting<Integer> wasteAmount;
    public Setting<Boolean> wasteMinDmgCount;
    public Setting<Float> facePlace;
    public Setting<Boolean> antiSurround;
    public Setting<Boolean> limitFacePlace;
    public Setting<Boolean> cc;
    public Setting<Boolean> oneDot15;
    public Setting<Boolean> doublePop;
    public Setting<Double> popHealth;
    public Setting<Float> popDamage;
    public Setting<Integer> popTime;
    public Setting<Float> minMinDmg;
    public Setting<Boolean> explode;
    public Setting<Switch> switchMode;
    public Setting<Integer> breakDelay;
    public Setting<Float> breakRange;
    public Setting<Integer> packets;
    public Setting<Float> maxSelfBreak;
    public Setting<Boolean> instant;
    public Setting<PredictTimer> instantTimer;
    public Setting<Integer> predictDelay;
    public Setting<Boolean> resetBreakTimer;
    public Setting<Boolean> predictCalc;
    public Setting<Boolean> superSafe;
    public Setting<Boolean> antiCommit;
    public Setting<Boolean> manual;
    public Setting<Boolean> manualMinDmg;
    public Setting<Integer> manualBreak;
    public Setting<Boolean> sync;
    public Setting<Boolean> render;
    public Setting<Boolean> justRender;
    public Setting<Boolean> fakeSwing;
    public Setting<RenderMode> renderMode;
    private final Setting<Boolean> fadeFactor;
    private final Setting<Boolean> scaleFactor;
    private final Setting<Boolean> slabFactor;
    private final Setting<Boolean> onlyplaced;
    private final Setting<Float> duration;
    private final Setting<Integer> max;
    private final Setting<Float> slabHeight;
    private final Setting<Float> moveSpeed;
    private final Setting<Float> accel;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> box;
    private final Setting<Integer> bRed;
    private final Setting<Integer> bGreen;
    private final Setting<Integer> bBlue;
    private final Setting<Integer> bAlpha;
    public Setting<Boolean> outline;
    private final Setting<Integer> oRed;
    private final Setting<Integer> oGreen;
    private final Setting<Integer> oBlue;
    private final Setting<Integer> oAlpha;
    private final Setting<Float> lineWidth;
    public Setting<Boolean> text;
    public Setting<Boolean> holdFacePlace;
    public Setting<Boolean> holdFaceBreak;
    public Setting<Boolean> slowFaceBreak;
    public Setting<Boolean> actualSlowBreak;
    public Setting<Integer> facePlaceSpeed;
    public Setting<Boolean> antiNaked;
    public Setting<Float> range;
    public Setting<Target> targetMode;
    public Setting<Boolean> doublePopOnDamage;
    public Setting<Boolean> webAttack;
    public Setting<Integer> minArmor;
    public Setting<AutoSwitch> autoSwitch;
    public Setting<Bind> switchBind;
    public Setting<Boolean> offhandSwitch;
    public Setting<Boolean> switchBack;
    public Setting<Boolean> lethalSwitch;
    public Setting<Boolean> mineSwitch;
    public Setting<Rotate> rotate;
    public Setting<Boolean> YawStep;
    public Setting<Integer> YawStepVal;
    public Setting<Integer> YawStepTicks;
    public Setting<Boolean> YawStepDebugMessages;
    public Setting<Boolean> rotateFirst;
    public Setting<Boolean> suicide;
    public Setting<Boolean> fullCalc;
    public Setting<Boolean> sound;
    public Setting<Float> soundRange;
    public Setting<Float> soundPlayer;
    public Setting<Boolean> soundConfirm;
    public Setting<Boolean> extraSelfCalc;
    public Setting<AntiFriendPop> antiFriendPop;
    public Setting<Boolean> noCount;
    public Setting<Boolean> calcEvenIfNoDamage;
    public Setting<Boolean> predictFriendDmg;
    public Setting<Raytrace> raytrace;
    public Setting<Float> placetrace;
    public Setting<Float> breaktrace;
    public Setting<Boolean> breakSwing;
    public Setting<Boolean> placeSwing;
    public Setting<Boolean> exactHand;
    public Setting<Logic> logic;
    public Setting<DamageSync> damageSync;
    public Setting<Integer> damageSyncTime;
    public Setting<Float> dropOff;
    public Setting<Integer> confirm;
    public Setting<Boolean> syncedFeetPlace;
    public Setting<Boolean> fullSync;
    public Setting<Boolean> syncCount;
    public Setting<Boolean> hyperSync;
    public Setting<Boolean> gigaSync;
    public Setting<Boolean> syncySync;
    public Setting<Boolean> enormousSync;
    public Setting<Boolean> holySync;
    public Setting<ThreadMode> threadMode;
    public Setting<Integer> threadDelay;
    public Setting<Boolean> syncThreadBool;
    public Setting<Integer> syncThreads;
    public Setting<Boolean> predictPos;
    public Setting<Integer> predictTicks;
    public Setting<Integer> rotations;
    public Setting<Boolean> predictRotate;
    public Setting<Float> predictOffset;
    public boolean rotating;
    private Queue<Entity> attackList;
    private Map<Entity,  Float> crystalMap;
    private Entity efficientTarget;
    private double currentDamage;
    private double renderDamage;
    private double lastDamage;
    private boolean didRotation;
    private boolean switching;
    private BlockPos placePos;
    private BlockPos renderPos;
    private boolean mainHand;
    private boolean offHand;
    private int crystalCount;
    private int minDmgCount;
    private int lastSlot;
    private float yaw;
    private float pitch;
    private BlockPos webPos;
    private BlockPos lastPos;
    private boolean posConfirmed;
    private boolean foundDoublePop;
    private int rotationPacketsSpoofed;
    private ScheduledExecutorService executor;
    private Thread thread;
    private EntityPlayer currentSyncTarget;
    private BlockPos syncedPlayerPos;
    private BlockPos syncedCrystalPos;
    private PlaceInfo placeInfo;
    private boolean addTolowDmg;
    private boolean shouldSilent;
    private BlockPos lastRenderPos;
    private AxisAlignedBB renderBB;
    private float timePassed;
    
    public AutoCrystal() {
        super("AutoCrystal",  "Does this need a explanation?",  Category.COMBAT,  true,  false,  false);
        this.yawStepTimer = new TimerUtil();
        this.switchTimer = new TimerUtil();
        this.manualTimer = new TimerUtil();
        this.breakTimer = new TimerUtil();
        this.placeTimer = new TimerUtil();
        this.syncTimer = new TimerUtil();
        this.predictTimer = new TimerUtil();
        this.renderTimer = new TimerUtil();
        this.shouldInterrupt = new AtomicBoolean(false);
        this.syncroTimer = new TimerUtil();
        this.totemPops = new ConcurrentHashMap<EntityPlayer,  TimerUtil>();
        this.packetUseEntities = new LinkedList<CPacketUseEntity>();
        this.threadOngoing = new AtomicBoolean(false);
        this.positions = new ArrayList<RenderPos>();
        this.setting = (Setting<Settings>)this.register(new Setting("Settings", Settings.PLACE));
        this.attackOppositeHand = (Setting<Boolean>)this.register(new Setting("OppositeHand", false,  v -> this.setting.getValue() == Settings.DEV));
        this.removeAfterAttack = (Setting<Boolean>)this.register(new Setting("AttackRemove", false,  v -> this.setting.getValue() == Settings.DEV));
        this.antiBlock = (Setting<Boolean>)this.register(new Setting("AntiFeetPlace", false,  v -> this.setting.getValue() == Settings.DEV));
        this.eventMode = (Setting<Integer>)this.register(new Setting("Updates", 3, 1, 3,  v -> this.setting.getValue() == Settings.DEV));
        this.switchCooldown = (Setting<Integer>)this.register(new Setting("Cooldown", 500, 0, 1000,  v -> this.setting.getValue() == Settings.MISC));
        this.place = (Setting<Boolean>)this.register(new Setting("Place", true,  v -> this.setting.getValue() == Settings.PLACE));
        this.placeDelay = (Setting<Integer>)this.register(new Setting("PlaceDelay", 25, 0, 500,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.placeRange = (Setting<Float>)this.register(new Setting("PlaceRange", 6.0f, 0.0f, 10.0f,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.minDamage = (Setting<Float>)this.register(new Setting("MinDamage", 7.0f, 0.1f, 20.0f,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.maxSelfPlace = (Setting<Float>)this.register(new Setting("MaxSelfPlace", 10.0f, 0.1f, 36.0f,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteAmount = (Setting<Integer>)this.register(new Setting("WasteAmount", 2, 1, 5,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.wasteMinDmgCount = (Setting<Boolean>)this.register(new Setting("CountMinDmg", true,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.facePlace = (Setting<Float>)this.register(new Setting("FacePlace", 8.0f, 0.1f, 36.0f,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.antiSurround = (Setting<Boolean>)this.register(new Setting("AntiSurround", true,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.limitFacePlace = (Setting<Boolean>)this.register(new Setting("LimitFacePlace", true,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.cc = (Setting<Boolean>)this.register(new Setting("1.12-1.13", false,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.oneDot15 = (Setting<Boolean>)this.register(new Setting("1.15", false,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.doublePop = (Setting<Boolean>)this.register(new Setting("AntiTotem", false,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue()));
        this.popHealth = (Setting<Double>)this.register(new Setting("PopHealth", 1.0, 0.0, 3.0,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.doublePop.getValue()));
        this.popDamage = (Setting<Float>)this.register(new Setting("PopDamage", 4.0f, 0.0f, 6.0f,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.doublePop.getValue()));
        this.popTime = (Setting<Integer>)this.register(new Setting("PopTime", 500, 0, 1000,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.doublePop.getValue()));
        this.minMinDmg = (Setting<Float>)this.register(new Setting("MinMinDmg", 0.0f, 0.0f, 3.0f,  v -> this.setting.getValue() == Settings.DEV && this.place.getValue()));
        this.explode = (Setting<Boolean>)this.register(new Setting("Break", true,  v -> this.setting.getValue() == Settings.BREAK));
        this.switchMode = (Setting<Switch>)this.register(new Setting("Attack", Switch.BREAKSLOT,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("BreakDelay", 50, 0, 500,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.breakRange = (Setting<Float>)this.register(new Setting("BreakRange", 6.0f, 0.0f, 10.0f,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.packets = (Setting<Integer>)this.register(new Setting("Packets", 1, 1, 6,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.maxSelfBreak = (Setting<Float>)this.register(new Setting("MaxSelfBreak", 10.0f, 0.1f, 36.0f,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue()));
        this.instant = (Setting<Boolean>)this.register(new Setting("Predict", true,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue()));
        this.instantTimer = (Setting<PredictTimer>)this.register(new Setting("PredictTimer", PredictTimer.NONE,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue() && this.instant.getValue()));
        this.predictDelay = (Setting<Integer>)this.register(new Setting("PredictDelay", 12, 0, 500,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue() && this.instant.getValue() && this.instantTimer.getValue() == PredictTimer.PREDICT));
        this.resetBreakTimer = (Setting<Boolean>)this.register(new Setting("ResetBreakTimer", true,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue() && this.instant.getValue()));
        this.predictCalc = (Setting<Boolean>)this.register(new Setting("PredictCalc", true,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue() && this.instant.getValue()));
        this.superSafe = (Setting<Boolean>)this.register(new Setting("SuperSafe", true,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue() && this.instant.getValue()));
        this.antiCommit = (Setting<Boolean>)this.register(new Setting("AntiOverCommit", true,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.place.getValue() && this.instant.getValue()));
        this.manual = (Setting<Boolean>)this.register(new Setting("Manual", true,  v -> this.setting.getValue() == Settings.BREAK));
        this.manualMinDmg = (Setting<Boolean>)this.register(new Setting("ManMinDmg", true,  v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.manualBreak = (Setting<Integer>)this.register(new Setting("ManualDelay", 500, 0, 500,  v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue()));
        this.sync = (Setting<Boolean>)this.register(new Setting("Sync", true,  v -> this.setting.getValue() == Settings.BREAK && (this.explode.getValue() || this.manual.getValue())));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", true,  v -> this.setting.getValue() == Settings.RENDER));
        this.justRender = (Setting<Boolean>)this.register(new Setting("JustRender", false,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.fakeSwing = (Setting<Boolean>)this.register(new Setting("FakeSwing", false,  v -> this.setting.getValue() == Settings.DEV && this.justRender.getValue()));
        this.renderMode = (Setting<RenderMode>)this.register(new Setting("Mode", RenderMode.STATIC,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.fadeFactor = (Setting<Boolean>)this.register(new Setting("Fade", true,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue()));
        this.scaleFactor = (Setting<Boolean>)this.register(new Setting("Shrink", false,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue()));
        this.slabFactor = (Setting<Boolean>)this.register(new Setting("Slab", false,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue()));
        this.onlyplaced = (Setting<Boolean>)this.register(new Setting("OnlyPlaced", false,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue()));
        this.duration = (Setting<Float>)this.register(new Setting("Duration", 1500.0f, 0.0f, 5000.0f,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue()));
        this.max = (Setting<Integer>)this.register(new Setting("MaxPositions", 15, 1, 30,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.FADE && this.render.getValue()));
        this.slabHeight = (Setting<Float>)this.register(new Setting("SlabDepth", 1.0f, 0.1f, 1.0f,  v -> this.setting.getValue() == Settings.RENDER && (this.renderMode.getValue() == RenderMode.STATIC || this.renderMode.getValue() == RenderMode.GLIDE) && this.render.getValue()));
        this.moveSpeed = (Setting<Float>)this.register(new Setting("Speed", 900.0f, 0.0f, 1500.0f,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GLIDE && this.render.getValue()));
        this.accel = (Setting<Float>)this.register(new Setting("Deceleration", 0.8f, 0.0f, 1.0f,  v -> this.setting.getValue() == Settings.RENDER && this.renderMode.getValue() == RenderMode.GLIDE && this.render.getValue()));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("CSync", false,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", true,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.bRed = (Setting<Integer>)this.register(new Setting("BoxRed", 150, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.bGreen = (Setting<Integer>)this.register(new Setting("BoxGreen", 0, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.bBlue = (Setting<Integer>)this.register(new Setting("BoxBlue", 150, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.bAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 40, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.box.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.oRed = (Setting<Integer>)this.register(new Setting("OutlineRed", 255, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.oGreen = (Setting<Integer>)this.register(new Setting("OutlineGreen", 50, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.oBlue = (Setting<Integer>)this.register(new Setting("OutlineBlue", 255, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.oAlpha = (Setting<Integer>)this.register(new Setting("OutlineAlpha", 255, 0, 255,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.5f, 0.1f, 5.0f,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() && this.outline.getValue()));
        this.text = (Setting<Boolean>)this.register(new Setting("Text", false,  v -> this.setting.getValue() == Settings.RENDER && this.render.getValue()));
        this.holdFacePlace = (Setting<Boolean>)this.register(new Setting("HoldFacePlace", false,  v -> this.setting.getValue() == Settings.MISC));
        this.holdFaceBreak = (Setting<Boolean>)this.register(new Setting("HoldSlowBreak", false,  v -> this.setting.getValue() == Settings.MISC && this.holdFacePlace.getValue()));
        this.slowFaceBreak = (Setting<Boolean>)this.register(new Setting("SlowFaceBreak", false,  v -> this.setting.getValue() == Settings.MISC));
        this.actualSlowBreak = (Setting<Boolean>)this.register(new Setting("ActuallySlow", false,  v -> this.setting.getValue() == Settings.MISC));
        this.facePlaceSpeed = (Setting<Integer>)this.register(new Setting("FaceSpeed", 500, 0, 500,  v -> this.setting.getValue() == Settings.MISC));
        this.antiNaked = (Setting<Boolean>)this.register(new Setting("AntiNaked", true,  v -> this.setting.getValue() == Settings.MISC));
        this.range = (Setting<Float>)this.register(new Setting("Range", 12.0f, 0.1f, 20.0f,  v -> this.setting.getValue() == Settings.MISC));
        this.targetMode = (Setting<Target>)this.register(new Setting("Target", Target.CLOSEST,  v -> this.setting.getValue() == Settings.MISC));
        this.doublePopOnDamage = (Setting<Boolean>)this.register(new Setting("DamagePop", false,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.doublePop.getValue() && this.targetMode.getValue() == Target.DAMAGE));
        this.webAttack = (Setting<Boolean>)this.register(new Setting("WebAttack", true,  v -> this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE));
        this.minArmor = (Setting<Integer>)this.register(new Setting("MinArmor", 5, 0, 125,  v -> this.setting.getValue() == Settings.MISC));
        this.autoSwitch = (Setting<AutoSwitch>)this.register(new Setting("Switch", AutoSwitch.TOGGLE,  v -> this.setting.getValue() == Settings.MISC));
        this.switchBind = (Setting<Bind>)this.register(new Setting("SwitchBind", new Bind(-1),  v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE));
        this.offhandSwitch = (Setting<Boolean>)this.register(new Setting("Offhand", true,  v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT));
        this.switchBack = (Setting<Boolean>)this.register(new Setting("Switchback", true,  v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.offhandSwitch.getValue() && this.autoSwitch.getValue() != AutoSwitch.SILENT));
        this.lethalSwitch = (Setting<Boolean>)this.register(new Setting("LethalSwitch", false,  v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT));
        this.mineSwitch = (Setting<Boolean>)this.register(new Setting("MineSwitch", true,  v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.autoSwitch.getValue() != AutoSwitch.SILENT));
        this.rotate = (Setting<Rotate>)this.register(new Setting("Rotate", Rotate.OFF,  v -> this.setting.getValue() == Settings.MISC));
        this.YawStep = (Setting<Boolean>)this.register(new Setting("YawStep", false,  v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF));
        this.YawStepVal = (Setting<Integer>)this.register(new Setting("YawStepValue", 0, 0, 180,  v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF && this.YawStep.getValue()));
        this.YawStepTicks = (Setting<Integer>)this.register(new Setting("YawStepTicks", 1, 1, 20,  v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF && this.YawStep.getValue()));
        this.YawStepDebugMessages = (Setting<Boolean>)this.register(new Setting("YawStep Debug", false,  v -> this.setting.getValue() == Settings.MISC && this.rotate.getValue() != Rotate.OFF && this.YawStep.getValue()));
        this.rotateFirst = (Setting<Boolean>)this.register(new Setting("FirstRotation", false,  v -> this.setting.getValue() == Settings.DEV && this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() == 2));
        this.suicide = (Setting<Boolean>)this.register(new Setting("Suicide", false,  v -> this.setting.getValue() == Settings.MISC));
        this.fullCalc = (Setting<Boolean>)this.register(new Setting("ExtraCalc", false,  v -> this.setting.getValue() == Settings.MISC));
        this.sound = (Setting<Boolean>)this.register(new Setting("Sound", true,  v -> this.setting.getValue() == Settings.MISC));
        this.soundRange = (Setting<Float>)this.register(new Setting("SoundRange", 12.0f, 0.0f, 12.0f,  v -> this.setting.getValue() == Settings.MISC));
        this.soundPlayer = (Setting<Float>)this.register(new Setting("SoundPlayer", 6.0f, 0.0f, 12.0f,  v -> this.setting.getValue() == Settings.MISC));
        this.soundConfirm = (Setting<Boolean>)this.register(new Setting("SoundConfirm", true,  v -> this.setting.getValue() == Settings.MISC));
        this.extraSelfCalc = (Setting<Boolean>)this.register(new Setting("MinSelfDmg", false,  v -> this.setting.getValue() == Settings.MISC));
        this.antiFriendPop = (Setting<AntiFriendPop>)this.register(new Setting("AntiFriendPop", AntiFriendPop.NONE,  v -> this.setting.getValue() == Settings.MISC));
        this.noCount = (Setting<Boolean>)this.register(new Setting("AntiCount", false,  v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK)));
        this.calcEvenIfNoDamage = (Setting<Boolean>)this.register(new Setting("BigFriendCalc", false,  v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.targetMode.getValue() != Target.DAMAGE));
        this.predictFriendDmg = (Setting<Boolean>)this.register(new Setting("PredictFriend", false,  v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.instant.getValue()));
        this.raytrace = (Setting<Raytrace>)this.register(new Setting("Raytrace", Raytrace.NONE,  v -> this.setting.getValue() == Settings.MISC));
        this.placetrace = (Setting<Float>)this.register(new Setting("Placetrace", 4.5f, 0.0f, 10.0f,  v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK));
        this.breaktrace = (Setting<Float>)this.register(new Setting("Breaktrace", 4.5f, 0.0f, 10.0f,  v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE));
        this.breakSwing = (Setting<Boolean>)this.register(new Setting("BreakSwing", true,  v -> this.setting.getValue() == Settings.DEV));
        this.placeSwing = (Setting<Boolean>)this.register(new Setting("PlaceSwing", false,  v -> this.setting.getValue() == Settings.DEV));
        this.exactHand = (Setting<Boolean>)this.register(new Setting("ExactHand", false,  v -> this.setting.getValue() == Settings.DEV && this.placeSwing.getValue()));
        this.logic = (Setting<Logic>)this.register(new Setting("Logic", Logic.BREAKPLACE,  v -> this.setting.getValue() == Settings.DEV));
        this.damageSync = (Setting<DamageSync>)this.register(new Setting("DamageSync", DamageSync.NONE,  v -> this.setting.getValue() == Settings.DEV));
        this.damageSyncTime = (Setting<Integer>)this.register(new Setting("SyncDelay", 500, 0, 500,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.dropOff = (Setting<Float>)this.register(new Setting("DropOff", 5.0f, 0.0f, 10.0f,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK));
        this.confirm = (Setting<Integer>)this.register(new Setting("Confirm", 250, 0, 1000,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.syncedFeetPlace = (Setting<Boolean>)this.register(new Setting("FeetSync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.fullSync = (Setting<Boolean>)this.register(new Setting("FullSync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.syncCount = (Setting<Boolean>)this.register(new Setting("SyncCount", true,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.hyperSync = (Setting<Boolean>)this.register(new Setting("HyperSync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.gigaSync = (Setting<Boolean>)this.register(new Setting("GigaSync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.syncySync = (Setting<Boolean>)this.register(new Setting("SyncySync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.enormousSync = (Setting<Boolean>)this.register(new Setting("EnormousSync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.holySync = (Setting<Boolean>)this.register(new Setting("UnbelievableSync", false,  v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue()));
        this.threadMode = (Setting<ThreadMode>)this.register(new Setting("Thread", ThreadMode.NONE,  v -> this.setting.getValue() == Settings.DEV));
        this.threadDelay = (Setting<Integer>)this.register(new Setting("ThreadDelay", 50, 1, 1000,  v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
        this.syncThreadBool = (Setting<Boolean>)this.register(new Setting("ThreadSync", true,  v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
        this.syncThreads = (Setting<Integer>)this.register(new Setting("SyncThreads", 1000, 1, 10000,  v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE && this.syncThreadBool.getValue()));
        this.predictPos = (Setting<Boolean>)this.register(new Setting("PredictPos", false,  v -> this.setting.getValue() == Settings.DEV));
        this.predictTicks = (Setting<Integer>)this.register(new Setting("ExtrapolationTicks", 2, 1, 20,  v -> this.setting.getValue() == Settings.DEV && this.predictPos.getValue()));
        this.rotations = (Setting<Integer>)this.register(new Setting("Spoofs", 1, 1, 20,  v -> this.setting.getValue() == Settings.DEV));
        this.predictRotate = (Setting<Boolean>)this.register(new Setting("PredictRotate", false,  v -> this.setting.getValue() == Settings.DEV));
        this.predictOffset = (Setting<Float>)this.register(new Setting("PredictOffset", 0.0f, 0.0f, 4.0f,  v -> this.setting.getValue() == Settings.DEV));
        this.attackList = new ConcurrentLinkedQueue<Entity>();
        this.crystalMap = new HashMap<Entity,  Float>();
        this.lastSlot = -1;
        AutoCrystal.instance = this;
    }
    
    public static AutoCrystal getInstance() {
        if (AutoCrystal.instance == null) {
            AutoCrystal.instance = new AutoCrystal();
        }
        return AutoCrystal.instance;
    }
    
    @Override
    public void onTick() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 3) {
            this.doAutoCrystal();
        }
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            this.postProcessing();
        }
        if (event.getStage() != 0) {
            return;
        }
        if (this.eventMode.getValue() == 2) {
            this.doAutoCrystal();
        }
    }
    
    public void postTick() {
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 1) {
            this.doAutoCrystal();
        }
    }
    
    @Override
    public void onToggle() {
        AutoCrystal.brokenPos.clear();
        AutoCrystal.placedPos.clear();
        this.totemPops.clear();
        this.rotating = false;
    }
    
    @Override
    public void onDisable() {
        this.positions.clear();
        this.lastRenderPos = null;
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }
    
    @Override
    public void onEnable() {
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }
    
    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "§aSwitch";
        }
        if (AutoCrystal.target != null) {
            return AutoCrystal.target.getName();
        }
        return null;
    }
    
    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && this.eventMode.getValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
            final CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
            packet2.yaw = this.yaw;
            packet2.pitch = this.pitch;
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
        BlockPos pos = null;
        CPacketUseEntity packet3;
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet3 = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet3.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            pos = Objects.requireNonNull(packet3.getEntityFromWorld((World)AutoCrystal.mc.world)).getPosition();
            if (this.removeAfterAttack.getValue()) {
                Objects.requireNonNull(packet3.getEntityFromWorld((World)AutoCrystal.mc.world)).setDead();
                AutoCrystal.mc.world.removeEntityFromWorld(packet3.entityId);
            }
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet3 = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet3.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            final EntityEnderCrystal crystal = (EntityEnderCrystal)packet3.getEntityFromWorld((World)AutoCrystal.mc.world);
            if (this.antiBlock.getValue() && EntityUtil.isCrystalAtFeet(crystal,  this.range.getValue()) && pos != null) {
                this.rotateToPos(pos);
                BlockUtil.placeCrystalOnBlock(this.placePos,  this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  this.placeSwing.getValue(),  this.exactHand.getValue(),  this.shouldSilent);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH,  receiveCanceled = true)
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (!this.justRender.getValue() && this.switchTimer.passedMs(this.switchCooldown.getValue()) && this.explode.getValue() && this.instant.getValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !this.syncedFeetPlace.getValue() || this.damageSync.getValue() == DamageSync.NONE)) {
            final SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket();
            final BlockPos pos;
            if (packet2.getType() == 51 && AutoCrystal.mc.player.getDistanceSq(pos = new BlockPos(packet2.getX(),  packet2.getY(),  packet2.getZ())) + this.predictOffset.getValue() <= MathUtil.square(this.breakRange.getValue()) && (this.instantTimer.getValue() == PredictTimer.NONE || (this.instantTimer.getValue() == PredictTimer.BREAK && this.breakTimer.passedMs(this.breakDelay.getValue())) || (this.instantTimer.getValue() == PredictTimer.PREDICT && this.predictTimer.passedMs(this.predictDelay.getValue())))) {
                if (this.predictSlowBreak(pos.down())) {
                    return;
                }
                if (this.predictFriendDmg.getValue() && (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL) && this.isRightThread()) {
                    for (final EntityPlayer friend : AutoCrystal.mc.world.playerEntities) {
                        if (friend != null && !AutoCrystal.mc.player.equals((Object)friend) && friend.getDistanceSq(pos) <= MathUtil.square(this.range.getValue() + this.placeRange.getValue()) && Phobos.friendManager.isFriend(friend)) {
                            if (DamageUtil.calculateDamage(pos,  (Entity)friend) <= EntityUtil.getHealth((Entity)friend) + 0.5) {
                                continue;
                            }
                            return;
                        }
                    }
                }
                if (AutoCrystal.placedPos.contains(pos.down())) {
                    Label_0621: {
                        if (this.isRightThread() && this.superSafe.getValue()) {
                            if (!DamageUtil.canTakeDamage(this.suicide.getValue())) {
                                break Label_0621;
                            }
                            final float selfDamage;
                            if ((selfDamage = DamageUtil.calculateDamage(pos,  (Entity)AutoCrystal.mc.player)) - 0.5 <= EntityUtil.getHealth((Entity)AutoCrystal.mc.player)) {
                                if (selfDamage <= this.maxSelfBreak.getValue()) {
                                    break Label_0621;
                                }
                            }
                        }
                        else if (!this.superSafe.getValue()) {
                            break Label_0621;
                        }
                        return;
                    }
                    this.attackCrystalPredict(packet2.getEntityID(),  pos);
                }
                else if (this.predictCalc.getValue() && this.isRightThread()) {
                    float selfDamage = -1.0f;
                    if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        selfDamage = DamageUtil.calculateDamage(pos,  (Entity)AutoCrystal.mc.player);
                    }
                    if (selfDamage + 0.5 < EntityUtil.getHealth((Entity)AutoCrystal.mc.player) && selfDamage <= this.maxSelfBreak.getValue()) {
                        for (final EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                            if (player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue()) && EntityUtil.isValid((Entity)player,  this.range.getValue() + this.breakRange.getValue()) && (!this.antiNaked.getValue() || !DamageUtil.isNaked(player))) {
                                final float damage;
                                if ((damage = DamageUtil.calculateDamage(pos,  (Entity)player)) <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth((Entity)player)) {
                                    continue;
                                }
                                if (this.predictRotate.getValue() && this.eventMode.getValue() != 2 && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
                                    this.rotateToPos(pos);
                                }
                                this.attackCrystalPredict(packet2.getEntityID(),  pos);
                                break;
                            }
                        }
                    }
                }
            }
        }
        else if (!this.soundConfirm.getValue() && event.getPacket() instanceof SPacketExplosion) {
            final SPacketExplosion packet3 = (SPacketExplosion)event.getPacket();
            final BlockPos pos2 = new BlockPos(packet3.getX(),  packet3.getY(),  packet3.getZ()).down();
            this.removePos(pos2);
        }
        else if (event.getPacket() instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packet4 = (SPacketDestroyEntities)event.getPacket();
            for (final int id : packet4.getEntityIDs()) {
                final Entity entity = AutoCrystal.mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    AutoCrystal.brokenPos.remove(new BlockPos(entity.getPositionVector()).down());
                    AutoCrystal.placedPos.remove(new BlockPos(entity.getPositionVector()).down());
                }
            }
        }
        else if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet5 = (SPacketEntityStatus)event.getPacket();
            if (packet5.getOpCode() == 35 && packet5.getEntity((World)AutoCrystal.mc.world) instanceof EntityPlayer) {
                this.totemPops.put((EntityPlayer)packet5.getEntity((World)AutoCrystal.mc.world),  new TimerUtil().reset());
            }
        }
        else {
            final SPacketSoundEffect packet6;
            if (event.getPacket() instanceof SPacketSoundEffect && (packet6 = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet6.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                final BlockPos pos = new BlockPos(packet6.getX(),  packet6.getY(),  packet6.getZ());
                if (this.sound.getValue() || this.threadMode.getValue() == ThreadMode.SOUND) {
                    if (fullNullCheck()) {
                        return;
                    }
                    NoSoundLag.removeEntities(packet6,  this.soundRange.getValue());
                }
                if (this.soundConfirm.getValue()) {
                    this.removePos(pos);
                }
                if (this.threadMode.getValue() == ThreadMode.SOUND && this.isRightThread() && AutoCrystal.mc.player != null && AutoCrystal.mc.player.getDistanceSq(pos) < MathUtil.square(this.soundPlayer.getValue())) {
                    this.handlePool(true);
                }
            }
        }
    }
    
    private boolean predictSlowBreak(final BlockPos pos) {
        return this.antiCommit.getValue() && AutoCrystal.lowDmgPos.remove(pos) && this.shouldSlowBreak(false);
    }
    
    private boolean isRightThread() {
        return AutoCrystal.mc.isCallingFromMinecraftThread() || (!Phobos.eventManager.ticksOngoing() && !this.threadOngoing.get());
    }
    
    private void attackCrystalPredict(final int entityID,  final BlockPos pos) {
        if (this.predictRotate.getValue() && (this.eventMode.getValue() != 2 || this.threadMode.getValue() != ThreadMode.NONE) && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
            this.rotateToPos(pos);
        }
        final CPacketUseEntity attackPacket = new CPacketUseEntity();
        attackPacket.entityId = entityID;
        attackPacket.action = CPacketUseEntity.Action.ATTACK;
        AutoCrystal.mc.player.connection.sendPacket((Packet)attackPacket);
        if (this.breakSwing.getValue()) {
            AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        if (this.resetBreakTimer.getValue()) {
            this.breakTimer.reset();
        }
        this.predictTimer.reset();
    }
    
    private void removePos(final BlockPos pos) {
        if (this.damageSync.getValue() == DamageSync.PLACE) {
            if (AutoCrystal.placedPos.remove(pos)) {
                this.posConfirmed = true;
            }
        }
        else if (this.damageSync.getValue() == DamageSync.BREAK && AutoCrystal.brokenPos.remove(pos)) {
            this.posConfirmed = true;
        }
    }
    
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (!this.render.getValue()) {
            return;
        }
        final Color boxC = new Color(this.bRed.getValue(),  this.bGreen.getValue(),  this.bBlue.getValue(),  this.bAlpha.getValue());
        final Color outlineC = new Color(this.oRed.getValue(),  this.oGreen.getValue(),  this.oBlue.getValue(),  this.oAlpha.getValue());
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && (this.box.getValue() || this.outline.getValue())) {
            if (this.renderMode.getValue() == RenderMode.FADE) {
                this.positions.removeIf(pos -> pos.getPos().equals((Object)this.renderPos));
                this.positions.add(new RenderPos(this.renderPos,  0.0f));
            }
            if (this.renderMode.getValue() == RenderMode.STATIC) {
                RenderUtil.drawSexyBoxPhobosIsRetardedFuckYouESP(new AxisAlignedBB(this.renderPos),  boxC,  outlineC,  this.lineWidth.getValue(),  this.outline.getValue(),  this.box.getValue(),  this.colorSync.getValue(),  1.0f,  1.0f,  this.slabHeight.getValue());
            }
            if (this.renderMode.getValue() == RenderMode.GLIDE) {
                if (this.lastRenderPos == null || AutoCrystal.mc.player.getDistance(this.renderBB.minX,  this.renderBB.minY,  this.renderBB.minZ) > this.range.getValue()) {
                    this.lastRenderPos = this.renderPos;
                    this.renderBB = new AxisAlignedBB(this.renderPos);
                    this.timePassed = 0.0f;
                }
                if (!this.lastRenderPos.equals((Object)this.renderPos)) {
                    this.lastRenderPos = this.renderPos;
                    this.timePassed = 0.0f;
                }
                final double xDiff = this.renderPos.getX() - this.renderBB.minX;
                final double yDiff = this.renderPos.getY() - this.renderBB.minY;
                final double zDiff = this.renderPos.getZ() - this.renderBB.minZ;
                float multiplier = this.timePassed / this.moveSpeed.getValue() * this.accel.getValue();
                if (multiplier > 1.0f) {
                    multiplier = 1.0f;
                }
                RenderUtil.drawSexyBoxPhobosIsRetardedFuckYouESP(this.renderBB = this.renderBB.offset(xDiff * multiplier,  yDiff * multiplier,  zDiff * multiplier),  boxC,  outlineC,  this.lineWidth.getValue(),  this.outline.getValue(),  this.box.getValue(),  this.colorSync.getValue(),  1.0f,  1.0f,  this.slabHeight.getValue());
                if (this.text.getValue()) {
                    RenderUtil.drawText(this.renderBB.offset(0.0,  1.0f - this.slabHeight.getValue() / 2.0f - 0.4,  0.0),  ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f",  this.renderDamage)) + "");
                }
                if (this.renderBB.equals((Object)new AxisAlignedBB(this.renderPos))) {
                    this.timePassed = 0.0f;
                }
                else {
                    this.timePassed += 50.0f;
                }
            }
        }
        if (this.renderMode.getValue() == RenderMode.FADE) {
            final float factor;
            final Color boxColor;
            final Color outlineColor;
            this.positions.forEach(pos -> {
                factor = (this.duration.getValue() - pos.getRenderTime()) / this.duration.getValue();
                RenderUtil.drawSexyBoxPhobosIsRetardedFuckYouESP(new AxisAlignedBB(pos.getPos()),  boxColor,  outlineColor,  this.lineWidth.getValue(),  this.outline.getValue(),  this.box.getValue(),  this.colorSync.getValue(),  ((boolean)this.fadeFactor.getValue()) ? factor : 1.0f,  ((boolean)this.scaleFactor.getValue()) ? factor : 1.0f,  ((boolean)this.slabFactor.getValue()) ? factor : 1.0f);
                pos.setRenderTime(pos.getRenderTime() + 50.0f);
                return;
            });
            this.positions.removeIf(pos -> pos.getRenderTime() >= this.duration.getValue() || AutoCrystal.mc.world.isAirBlock(pos.getPos()) || !AutoCrystal.mc.world.isAirBlock(pos.getPos().offset(EnumFacing.UP)));
            if (this.positions.size() > this.max.getValue()) {
                this.positions.remove(0);
            }
        }
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && this.text.getValue() && this.renderMode.getValue() != RenderMode.GLIDE) {
            RenderUtil.drawText(new AxisAlignedBB(this.renderPos).offset(0.0,  (this.renderMode.getValue() != RenderMode.FADE) ? (1.0f - this.slabHeight.getValue() / 2.0f - 0.4) : 0.1,  0.0),  ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f",  this.renderDamage)) + "");
        }
    }
    
    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(AutoCrystal.mc.currentScreen instanceof PhobosGui) && this.switchBind.getValue().getKey() == Keyboard.getEventKey()) {
            if (this.switchBack.getValue() && this.offhandSwitch.getValue() && this.offHand) {
                final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
                if (module.isOff()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
                }
                else if (module.type.getValue() == Offhand.Type.NEW) {
                    module.setSwapToTotem(true);
                    module.doOffhand();
                }
                else {
                    module.setMode(Offhand.Mode2.TOTEMS);
                    module.doSwitch();
                }
                return;
            }
            this.switching = !this.switching;
        }
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.thread != null) {
                this.shouldInterrupt.set(true);
            }
        }
    }
    
    private void postProcessing() {
        if (this.threadMode.getValue() != ThreadMode.NONE || this.eventMode.getValue() != 2 || this.rotate.getValue() == Rotate.OFF || !this.rotateFirst.getValue()) {
            return;
        }
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.postProcessBreak();
                this.postProcessPlace();
                break;
            }
            case PLACEBREAK: {
                this.postProcessPlace();
                this.postProcessBreak();
                break;
            }
        }
    }
    
    private void postProcessBreak() {
        while (!this.packetUseEntities.isEmpty()) {
            final CPacketUseEntity packet = this.packetUseEntities.poll();
            AutoCrystal.mc.player.connection.sendPacket((Packet)packet);
            if (this.breakSwing.getValue()) {
                AutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.breakTimer.reset();
        }
    }
    
    private void postProcessPlace() {
        if (this.placeInfo != null) {
            this.placeInfo.runPlace();
            this.placeTimer.reset();
            this.placeInfo = null;
        }
    }
    
    private void processMultiThreading() {
        if (this.isOff()) {
            return;
        }
        if (this.threadMode.getValue() == ThreadMode.WHILE) {
            this.handleWhile();
        }
        else if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.handlePool(false);
        }
    }
    
    private void handlePool(final boolean justDoIt) {
        if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || (this.syncroTimer.passedMs(this.syncThreads.getValue()) && this.syncThreadBool.getValue())) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            this.executor = this.getExecutor();
            this.syncroTimer.reset();
        }
    }
    
    private void handleWhile() {
        if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || (this.syncroTimer.passedMs(this.syncThreads.getValue()) && this.syncThreadBool.getValue())) {
            if (this.thread == null) {
                this.thread = new Thread(RAutoCrystal.getInstance(this));
            }
            else if (this.syncroTimer.passedMs(this.syncThreads.getValue()) && !this.shouldInterrupt.get() && this.syncThreadBool.getValue()) {
                this.shouldInterrupt.set(true);
                this.syncroTimer.reset();
                return;
            }
            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                this.thread = new Thread(RAutoCrystal.getInstance(this));
            }
            if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                try {
                    this.thread.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.syncroTimer.reset();
            }
        }
    }
    
    private ScheduledExecutorService getExecutor() {
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoCrystal.getInstance(this),  0L,  this.threadDelay.getValue(),  TimeUnit.MILLISECONDS);
        return service;
    }
    
    public void doAutoCrystal() {
        if (this.check()) {
            switch (this.logic.getValue()) {
                case PLACEBREAK: {
                    this.placeCrystal();
                    this.breakCrystal();
                    break;
                }
                case BREAKPLACE: {
                    this.breakCrystal();
                    this.placeCrystal();
                    break;
                }
            }
            this.manualBreaker();
        }
    }
    
    private boolean check() {
        if (fullNullCheck()) {
            return false;
        }
        if (this.syncTimer.passedMs(this.damageSyncTime.getValue())) {
            this.currentSyncTarget = null;
            this.syncedCrystalPos = null;
            this.syncedPlayerPos = null;
        }
        else if (this.syncySync.getValue() && this.syncedCrystalPos != null) {
            this.posConfirmed = true;
        }
        this.foundDoublePop = false;
        if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.mainHand = (AutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        if (this.autoSwitch.getValue() == AutoSwitch.SILENT && InventoryUtil.getItemHotbar(Items.END_CRYSTAL) != -1) {
            this.mainHand = true;
            this.shouldSilent = true;
        }
        else {
            this.shouldSilent = false;
        }
        this.offHand = (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != AutoCrystal.mc.player.inventory.currentItem || AutoTrap.isPlacing || Surround.isPlacing) {
            this.lastSlot = AutoCrystal.mc.player.inventory.currentItem;
            this.switchTimer.reset();
        }
        if (!this.offHand && !this.mainHand) {
            this.placeInfo = null;
            this.packetUseEntities.clear();
        }
        if (this.offHand || this.mainHand) {
            this.switching = false;
        }
        if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness((EntityPlayer)AutoCrystal.mc.player) || !this.switchTimer.passedMs(this.switchCooldown.getValue())) {
            this.renderPos = null;
            AutoCrystal.target = null;
            return this.rotating = false;
        }
        if (this.mineSwitch.getValue() && Mouse.isButtonDown(0) && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && Mouse.isButtonDown(1) && AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            this.switchItem();
        }
        this.mapCrystals();
        if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(this.confirm.getValue())) {
            this.syncTimer.setMs(this.damageSyncTime.getValue() + 1);
        }
        return true;
    }
    
    private void mapCrystals() {
        this.efficientTarget = null;
        if (this.packets.getValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue<Entity>();
            this.crystalMap = new HashMap<Entity,  Float>();
        }
        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5f;
        for (final Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (!entity.isDead && entity instanceof EntityEnderCrystal) {
                if (!this.isValid(entity)) {
                    continue;
                }
                if (this.syncedFeetPlace.getValue() && entity.getPosition().down().equals((Object)this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
                    ++this.minDmgCount;
                    ++this.crystalCount;
                    if (this.syncCount.getValue()) {
                        this.minDmgCount = this.wasteAmount.getValue() + 1;
                        this.crystalCount = this.wasteAmount.getValue() + 1;
                    }
                    if (!this.hyperSync.getValue()) {
                        continue;
                    }
                    maxCrystal = null;
                    break;
                }
                else {
                    boolean count = false;
                    boolean countMin = false;
                    float selfDamage = -1.0f;
                    if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        selfDamage = DamageUtil.calculateDamage(entity,  (Entity)AutoCrystal.mc.player);
                    }
                    if (selfDamage + 0.5 < EntityUtil.getHealth((Entity)AutoCrystal.mc.player) && selfDamage <= this.maxSelfBreak.getValue()) {
                        final Entity beforeCrystal = maxCrystal;
                        final float beforeDamage = maxDamage;
                        for (final EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                            if (player.getDistanceSq(entity) > MathUtil.square(this.range.getValue())) {
                                continue;
                            }
                            if (EntityUtil.isValid((Entity)player,  this.range.getValue() + this.breakRange.getValue())) {
                                if (this.antiNaked.getValue() && DamageUtil.isNaked(player)) {
                                    continue;
                                }
                                final float damage;
                                if ((damage = DamageUtil.calculateDamage(entity,  (Entity)player)) <= selfDamage && (damage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth((Entity)player)) {
                                    continue;
                                }
                                if (damage > maxDamage) {
                                    maxDamage = damage;
                                    maxCrystal = entity;
                                }
                                if (this.packets.getValue() == 1) {
                                    if (damage >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                                        count = true;
                                    }
                                    countMin = true;
                                }
                                else {
                                    if (this.crystalMap.get(entity) != null && this.crystalMap.get(entity) >= damage) {
                                        continue;
                                    }
                                    this.crystalMap.put(entity,  damage);
                                }
                            }
                            else {
                                if ((this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.ALL) || !Phobos.friendManager.isFriend(player.getName())) {
                                    continue;
                                }
                                if (DamageUtil.calculateDamage(entity,  (Entity)player) <= EntityUtil.getHealth((Entity)player) + 0.5) {
                                    continue;
                                }
                                maxCrystal = beforeCrystal;
                                maxDamage = beforeDamage;
                                this.crystalMap.remove(entity);
                                if (!this.noCount.getValue()) {
                                    break;
                                }
                                count = false;
                                countMin = false;
                                break;
                            }
                        }
                    }
                    if (!countMin) {
                        continue;
                    }
                    ++this.minDmgCount;
                    if (!count) {
                        continue;
                    }
                    ++this.crystalCount;
                }
            }
        }
        if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
            this.lastDamage = maxDamage;
        }
        if (this.enormousSync.getValue() && this.syncedFeetPlace.getValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
            if (this.syncCount.getValue()) {
                this.minDmgCount = this.wasteAmount.getValue() + 1;
                this.crystalCount = this.wasteAmount.getValue() + 1;
            }
            return;
        }
        if (this.webAttack.getValue() && this.webPos != null) {
            if (AutoCrystal.mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square(this.breakRange.getValue())) {
                this.webPos = null;
            }
            else {
                for (final Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB((Class)Entity.class,  new AxisAlignedBB(this.webPos.up()))) {
                    if (!(entity instanceof EntityEnderCrystal)) {
                        continue;
                    }
                    this.attackList.add(entity);
                    this.efficientTarget = entity;
                    this.webPos = null;
                    this.lastDamage = 0.5;
                    return;
                }
            }
        }
        if (this.shouldSlowBreak(true) && maxDamage < this.minDamage.getValue() && (AutoCrystal.target == null || EntityUtil.getHealth((Entity)AutoCrystal.target) > this.facePlace.getValue() || (!this.breakTimer.passedMs(this.facePlaceSpeed.getValue()) && this.slowFaceBreak.getValue() && Mouse.isButtonDown(0) && this.holdFacePlace.getValue() && this.holdFaceBreak.getValue()))) {
            this.efficientTarget = null;
            return;
        }
        if (this.packets.getValue() == 1) {
            this.efficientTarget = maxCrystal;
        }
        else {
            this.crystalMap = MathUtil.sortByValue(this.crystalMap,  true);
            for (final Map.Entry<Entity,  Float> entry : this.crystalMap.entrySet()) {
                final Entity crystal = entry.getKey();
                final float damage2 = entry.getValue();
                if (damage2 >= this.minDamage.getValue() || !this.wasteMinDmgCount.getValue()) {
                    ++this.crystalCount;
                }
                this.attackList.add(crystal);
                ++this.minDmgCount;
            }
        }
    }
    
    private boolean shouldSlowBreak(final boolean withManual) {
        return (withManual && this.manual.getValue() && this.manualMinDmg.getValue() && Mouse.isButtonDown(1) && (!Mouse.isButtonDown(0) || !this.holdFacePlace.getValue())) || (this.holdFacePlace.getValue() && this.holdFaceBreak.getValue() && Mouse.isButtonDown(0) && !this.breakTimer.passedMs(this.facePlaceSpeed.getValue())) || (this.slowFaceBreak.getValue() && !this.breakTimer.passedMs(this.facePlaceSpeed.getValue()));
    }
    
    private void placeCrystal() {
        int crystalLimit = this.wasteAmount.getValue();
        if (this.placeTimer.passedMs(this.placeDelay.getValue()) && this.place.getValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
            if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!this.antiSurround.getValue() || this.lastPos == null || !this.lastPos.equals((Object)this.placePos))) {
                return;
            }
            this.calculateDamage(this.getTarget(this.targetMode.getValue() == Target.UNSAFE));
            if (AutoCrystal.target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > this.minDamage.getValue() || (this.lethalSwitch.getValue() && EntityUtil.getHealth((Entity)AutoCrystal.target) <= this.facePlace.getValue())) && !this.switchItem()) {
                    return;
                }
                if (this.currentDamage < this.minDamage.getValue() && this.limitFacePlace.getValue()) {
                    crystalLimit = 1;
                }
                if (this.currentDamage >= this.minMinDmg.getValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (this.antiSurround.getValue() && this.lastPos != null && this.lastPos.equals((Object)this.placePos))) && (this.currentDamage > this.minDamage.getValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0 && (DamageUtil.isArmorLow(AutoCrystal.target,  this.minArmor.getValue()) || EntityUtil.getHealth((Entity)AutoCrystal.target) <= this.facePlace.getValue() || this.currentDamage > this.minDamage.getValue() || this.shouldHoldFacePlace())) {
                    final float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (this.dropOff.getValue() - 5.0f) : 0.0f;
                    boolean syncflag = false;
                    if (this.syncedFeetPlace.getValue() && this.placePos.equals((Object)this.lastPos) && this.isEligableForFeetSync(AutoCrystal.target,  this.placePos) && !this.syncTimer.passedMs(this.damageSyncTime.getValue()) && AutoCrystal.target.equals((Object)this.currentSyncTarget) && AutoCrystal.target.getPosition().equals((Object)this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
                        this.syncedCrystalPos = this.placePos;
                        this.lastDamage = this.currentDamage;
                        if (this.fullSync.getValue()) {
                            this.lastDamage = 100.0;
                        }
                        syncflag = true;
                    }
                    if (syncflag || this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue()) || this.damageSync.getValue() == DamageSync.NONE) {
                        if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }
                        if (!this.onlyplaced.getValue()) {
                            this.renderPos = this.placePos;
                        }
                        this.renderDamage = this.currentDamage;
                        if (this.switchItem()) {
                            this.currentSyncTarget = AutoCrystal.target;
                            this.syncedPlayerPos = AutoCrystal.target.getPosition();
                            if (this.foundDoublePop) {
                                this.totemPops.put(AutoCrystal.target,  new TimerUtil().reset());
                            }
                            this.rotateToPos(this.placePos);
                            if (this.addTolowDmg || (this.actualSlowBreak.getValue() && this.currentDamage < this.minDamage.getValue())) {
                                AutoCrystal.lowDmgPos.add(this.placePos);
                            }
                            AutoCrystal.placedPos.add(this.placePos);
                            if (!this.justRender.getValue()) {
                                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && this.rotateFirst.getValue() && this.rotate.getValue() != Rotate.OFF) {
                                    this.placeInfo = new PlaceInfo(this.placePos,  this.offHand,  this.placeSwing.getValue(),  this.exactHand.getValue(),  this.shouldSilent);
                                }
                                else {
                                    BlockUtil.placeCrystalOnBlock(this.placePos,  this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  this.placeSwing.getValue(),  this.exactHand.getValue(),  this.shouldSilent);
                                }
                            }
                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs(this.damageSyncTime.getValue())) {
                                this.syncedCrystalPos = null;
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            }
            else {
                this.renderPos = null;
            }
        }
    }
    
    private boolean shouldHoldFacePlace() {
        this.addTolowDmg = false;
        return this.holdFacePlace.getValue() && Mouse.isButtonDown(0) && (this.addTolowDmg = true);
    }
    
    private boolean switchItem() {
        if (this.offHand || this.mainHand) {
            return true;
        }
        switch (this.autoSwitch.getValue()) {
            case NONE: {
                return false;
            }
            case TOGGLE: {
                if (!this.switching) {
                    return false;
                }
            }
            case ALWAYS: {
                if (!this.doSwitch()) {
                    break;
                }
                return true;
            }
        }
        return false;
    }
    
    private boolean doSwitch() {
        if (!this.offhandSwitch.getValue()) {
            if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                this.mainHand = false;
            }
            else {
                InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class,  false);
                this.mainHand = true;
            }
            this.switching = false;
            return true;
        }
        final Offhand module = Phobos.moduleManager.getModuleByClass(Offhand.class);
        if (module.isOff()) {
            Command.sendMessage("<" + this.getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
            return this.switching = false;
        }
        if (module.type.getValue() == Offhand.Type.NEW) {
            module.setSwapToTotem(false);
            module.setMode(Offhand.Mode.CRYSTALS);
            module.doOffhand();
        }
        else {
            module.setMode(Offhand.Mode2.CRYSTALS);
            module.doSwitch();
        }
        this.switching = false;
        return true;
    }
    
    private void calculateDamage(final EntityPlayer targettedPlayer) {
        if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !this.fullCalc.getValue()) {
            return;
        }
        float maxDamage = 0.5f;
        EntityPlayer currentTarget = null;
        BlockPos currentPos = null;
        float maxSelfDamage = 0.0f;
        this.foundDoublePop = false;
        BlockPos setToAir = null;
        IBlockState state = null;
        final BlockPos playerPos;
        if (this.webAttack.getValue() && targettedPlayer != null && AutoCrystal.mc.world.getBlockState(playerPos = new BlockPos(targettedPlayer.getPositionVector())).getBlock() == Blocks.WEB) {
            setToAir = playerPos;
            state = AutoCrystal.mc.world.getBlockState(playerPos);
            AutoCrystal.mc.world.setBlockToAir(playerPos);
        }
        for (final BlockPos pos : BlockUtil.possiblePlacePositions(this.placeRange.getValue(),  this.antiSurround.getValue(),  this.oneDot15.getValue(),  this.cc.getValue())) {
            if (!BlockUtil.rayTracePlaceCheck(pos,  (this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && AutoCrystal.mc.player.getDistanceSq(pos) > MathUtil.square(this.placetrace.getValue()),  1.0f)) {
                continue;
            }
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(pos,  (Entity)AutoCrystal.mc.player);
            }
            if (selfDamage + 0.5 >= EntityUtil.getHealth((Entity)AutoCrystal.mc.player)) {
                continue;
            }
            if (selfDamage > this.maxSelfPlace.getValue()) {
                continue;
            }
            if (targettedPlayer != null) {
                final float playerDamage = DamageUtil.calculateDamage(pos,  (Entity)targettedPlayer);
                if (this.calcEvenIfNoDamage.getValue() && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)) {
                    boolean friendPop = false;
                    for (final EntityPlayer friend : AutoCrystal.mc.world.playerEntities) {
                        if (friend != null && !AutoCrystal.mc.player.equals((Object)friend) && friend.getDistanceSq(pos) <= MathUtil.square(this.range.getValue() + this.placeRange.getValue()) && Phobos.friendManager.isFriend(friend)) {
                            if (DamageUtil.calculateDamage(pos,  (Entity)friend) <= EntityUtil.getHealth((Entity)friend) + 0.5) {
                                continue;
                            }
                            friendPop = true;
                            break;
                        }
                    }
                    if (friendPop) {
                        continue;
                    }
                }
                if (this.isDoublePoppable(targettedPlayer,  playerDamage) && (currentPos == null || targettedPlayer.getDistanceSq(pos) < targettedPlayer.getDistanceSq(currentPos))) {
                    currentTarget = targettedPlayer;
                    maxDamage = playerDamage;
                    currentPos = pos;
                    this.foundDoublePop = true;
                }
                else {
                    if (this.foundDoublePop || (playerDamage <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage))) {
                        continue;
                    }
                    if (playerDamage <= selfDamage && (playerDamage <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage <= EntityUtil.getHealth((Entity)targettedPlayer)) {
                        continue;
                    }
                    maxDamage = playerDamage;
                    currentTarget = targettedPlayer;
                    currentPos = pos;
                    maxSelfDamage = selfDamage;
                }
            }
            else {
                final float maxDamageBefore = maxDamage;
                final EntityPlayer currentTargetBefore = currentTarget;
                final BlockPos currentPosBefore = currentPos;
                final float maxSelfDamageBefore = maxSelfDamage;
                for (final EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                    if (EntityUtil.isValid((Entity)player,  this.placeRange.getValue() + this.range.getValue())) {
                        if (this.antiNaked.getValue() && DamageUtil.isNaked(player)) {
                            continue;
                        }
                        final float playerDamage2 = DamageUtil.calculateDamage(pos,  (Entity)player);
                        if (this.doublePopOnDamage.getValue() && this.isDoublePoppable(player,  playerDamage2) && (currentPos == null || player.getDistanceSq(pos) < player.getDistanceSq(currentPos))) {
                            currentTarget = player;
                            maxDamage = playerDamage2;
                            currentPos = pos;
                            maxSelfDamage = selfDamage;
                            this.foundDoublePop = true;
                            if (this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.PLACE) {
                                continue;
                            }
                            break;
                        }
                        else {
                            if (this.foundDoublePop || (playerDamage2 <= maxDamage && (!this.extraSelfCalc.getValue() || playerDamage2 < maxDamage || selfDamage >= maxSelfDamage))) {
                                continue;
                            }
                            if (playerDamage2 <= selfDamage && (playerDamage2 <= this.minDamage.getValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage2 <= EntityUtil.getHealth((Entity)player)) {
                                continue;
                            }
                            maxDamage = playerDamage2;
                            currentTarget = player;
                            currentPos = pos;
                            maxSelfDamage = selfDamage;
                        }
                    }
                    else {
                        if ((this.antiFriendPop.getValue() != AntiFriendPop.ALL && this.antiFriendPop.getValue() != AntiFriendPop.PLACE) || player == null || player.getDistanceSq(pos) > MathUtil.square(this.range.getValue() + this.placeRange.getValue()) || !Phobos.friendManager.isFriend(player)) {
                            continue;
                        }
                        if (DamageUtil.calculateDamage(pos,  (Entity)player) <= EntityUtil.getHealth((Entity)player) + 0.5) {
                            continue;
                        }
                        maxDamage = maxDamageBefore;
                        currentTarget = currentTargetBefore;
                        currentPos = currentPosBefore;
                        maxSelfDamage = maxSelfDamageBefore;
                        break;
                    }
                }
            }
        }
        if (setToAir != null) {
            AutoCrystal.mc.world.setBlockState(setToAir,  state);
            this.webPos = currentPos;
        }
        AutoCrystal.target = currentTarget;
        this.currentDamage = maxDamage;
        this.placePos = currentPos;
    }
    
    private EntityPlayer getTarget(final boolean unsafe) {
        if (this.targetMode.getValue() == Target.DAMAGE) {
            return null;
        }
        EntityPlayer currentTarget = null;
        for (final EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
            if (!EntityUtil.isntValid((Entity)player,  this.placeRange.getValue() + this.range.getValue()) && (!this.antiNaked.getValue() || !DamageUtil.isNaked(player))) {
                if (unsafe && EntityUtil.isSafe((Entity)player)) {
                    continue;
                }
                if (this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player,  this.minArmor.getValue())) {
                    currentTarget = player;
                    break;
                }
                if (currentTarget == null) {
                    currentTarget = player;
                }
                else {
                    if (AutoCrystal.mc.player.getDistanceSq((Entity)player) >= AutoCrystal.mc.player.getDistanceSq((Entity)currentTarget)) {
                        continue;
                    }
                    currentTarget = player;
                }
            }
        }
        if (unsafe && currentTarget == null) {
            return this.getTarget(false);
        }
        if (this.predictPos.getValue() && currentTarget != null) {
            currentTarget.getUniqueID();
            final GameProfile profile = new GameProfile(currentTarget.getUniqueID(),  currentTarget.getName());
            final EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP((World)AutoCrystal.mc.world,  profile);
            final Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition(currentTarget,  this.predictTicks.getValue());
            newTarget.copyLocationAndAnglesFrom((Entity)currentTarget);
            newTarget.posX = extrapolatePosition.x;
            newTarget.posY = extrapolatePosition.y;
            newTarget.posZ = extrapolatePosition.z;
            newTarget.setHealth(EntityUtil.getHealth((Entity)currentTarget));
            newTarget.inventory.copyInventory(currentTarget.inventory);
            currentTarget = (EntityPlayer)newTarget;
        }
        return currentTarget;
    }
    
    private void breakCrystal() {
        if (this.explode.getValue() && this.breakTimer.passedMs(this.breakDelay.getValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (this.packets.getValue() == 1 && this.efficientTarget != null) {
                if (this.justRender.getValue()) {
                    this.doFakeSwing();
                    return;
                }
                if (this.syncedFeetPlace.getValue() && this.gigaSync.getValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                this.rotateTo(this.efficientTarget);
                this.attackEntity(this.efficientTarget);
                this.breakTimer.reset();
            }
            else if (!this.attackList.isEmpty()) {
                if (this.justRender.getValue()) {
                    this.doFakeSwing();
                    return;
                }
                if (this.syncedFeetPlace.getValue() && this.gigaSync.getValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                for (int i = 0; i < this.packets.getValue(); ++i) {
                    final Entity entity = this.attackList.poll();
                    if (entity != null) {
                        this.rotateTo(entity);
                        this.attackEntity(entity);
                    }
                }
                this.breakTimer.reset();
            }
        }
    }
    
    private void attackEntity(final Entity entity) {
        if (entity != null) {
            if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && this.rotateFirst.getValue() && this.rotate.getValue() != Rotate.OFF) {
                this.packetUseEntities.add(new CPacketUseEntity(entity));
            }
            else {
                EntityUtil.attackEntity(entity,  this.sync.getValue(),  this.breakSwing.getValue());
                EntityUtil.OffhandAttack(entity,  this.attackOppositeHand.getValue(),  this.attackOppositeHand.getValue());
                AutoCrystal.brokenPos.add(new BlockPos(entity.getPositionVector()).down());
            }
        }
    }
    
    private void doFakeSwing() {
        if (this.fakeSwing.getValue()) {
            EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND,  (EntityLivingBase)AutoCrystal.mc.player);
        }
    }
    
    private void manualBreaker() {
        if (this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() != 2 && this.rotating) {
            if (this.didRotation) {
                AutoCrystal.mc.player.rotationPitch += (float)4.0E-4;
                this.didRotation = false;
            }
            else {
                AutoCrystal.mc.player.rotationPitch -= (float)4.0E-4;
                this.didRotation = true;
            }
        }
        final RayTraceResult result;
        if ((this.offHand || this.mainHand) && this.manual.getValue() && this.manualTimer.passedMs(this.manualBreak.getValue()) && Mouse.isButtonDown(1) && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = AutoCrystal.mc.objectMouseOver) != null) {
            switch (result.typeOfHit) {
                case ENTITY: {
                    final Entity entity = result.entityHit;
                    if (!(entity instanceof EntityEnderCrystal)) {
                        break;
                    }
                    EntityUtil.attackEntity(entity,  this.sync.getValue(),  this.breakSwing.getValue());
                    EntityUtil.OffhandAttack(entity,  this.attackOppositeHand.getValue(),  this.attackOppositeHand.getValue());
                    this.manualTimer.reset();
                    break;
                }
                case BLOCK: {
                    final BlockPos mousePos = AutoCrystal.mc.objectMouseOver.getBlockPos().up();
                    for (final Entity target : AutoCrystal.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null,  new AxisAlignedBB(mousePos))) {
                        if (!(target instanceof EntityEnderCrystal)) {
                            continue;
                        }
                        EntityUtil.attackEntity(target,  this.sync.getValue(),  this.breakSwing.getValue());
                        EntityUtil.OffhandAttack(target,  this.attackOppositeHand.getValue(),  this.attackOppositeHand.getValue());
                        this.manualTimer.reset();
                    }
                    break;
                }
            }
        }
    }
    
    private void rotateTo(final Entity entity) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case BREAK:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(AutoCrystal.mc.getRenderPartialTicks()),  entity.getPositionVector());
                if (this.eventMode.getValue() != 2 || this.threadMode.getValue() != ThreadMode.NONE) {
                    this.yaw = angle[0];
                    this.pitch = angle[1];
                    this.rotating = true;
                    break;
                }
                if (this.YawStep.getValue()) {
                    float f = Phobos.rotationManager.getYaw();
                    while (f < angle[1]) {
                        if (AutoCrystal.mc.player.ticksExisted % this.YawStepTicks.getValue() != 0) {
                            continue;
                        }
                        Phobos.rotationManager.setPlayerRotations(f += this.YawStepVal.getValue(),  angle[1]);
                        if (!this.YawStepDebugMessages.getValue()) {
                            continue;
                        }
                        Command.sendMessage("Yaw " + Phobos.rotationManager.getYaw());
                    }
                    break;
                }
                Phobos.rotationManager.setPlayerRotations(angle[0],  angle[1]);
                break;
            }
        }
    }
    
    private void rotateToPos(final BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case PLACE:
            case ALL: {
                final float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(AutoCrystal.mc.getRenderPartialTicks()),  new Vec3d((double)(pos.getX() + 0.5f),  (double)(pos.getY() - 0.5f),  (double)(pos.getZ() + 0.5f)));
                if (this.eventMode.getValue() != 2 || this.threadMode.getValue() != ThreadMode.NONE) {
                    this.yaw = angle[0];
                    this.pitch = angle[1];
                    this.rotating = true;
                    break;
                }
                if (this.YawStep.getValue()) {
                    float f = Phobos.rotationManager.getYaw();
                    while (f < angle[1]) {
                        if (AutoCrystal.mc.player.ticksExisted % this.YawStepTicks.getValue() != 0) {
                            continue;
                        }
                        Phobos.rotationManager.setPlayerRotations(f += this.YawStepVal.getValue(),  angle[1]);
                        this.yawStepTimer.reset();
                        if (!this.YawStepDebugMessages.getValue()) {
                            continue;
                        }
                        Command.sendMessage("Yaw" + Phobos.rotationManager.getYaw());
                    }
                    break;
                }
                Phobos.rotationManager.setPlayerRotations(angle[0],  angle[1]);
                break;
            }
        }
    }
    
    private boolean isDoublePoppable(final EntityPlayer player,  final float damage) {
        final float health;
        if (this.doublePop.getValue() && (health = EntityUtil.getHealth((Entity)player)) <= this.popHealth.getValue() && damage > health + 0.5 && damage <= this.popDamage.getValue()) {
            final TimerUtil timer = this.totemPops.get(player);
            return timer == null || timer.passedMs(this.popTime.getValue());
        }
        return false;
    }
    
    private boolean isValid(final Entity entity) {
        return entity != null && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breakRange.getValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || AutoCrystal.mc.player.canEntityBeSeen(entity) || (!AutoCrystal.mc.player.canEntityBeSeen(entity) && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breaktrace.getValue())));
    }
    
    private boolean isEligableForFeetSync(final EntityPlayer player,  final BlockPos pos) {
        if (this.holySync.getValue()) {
            final BlockPos playerPos = new BlockPos(player.getPositionVector());
            for (final EnumFacing facing : EnumFacing.values()) {
                if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && pos.equals((Object)playerPos.down().offset(facing))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    static {
        AutoCrystal.lowDmgPos = (Set<BlockPos>)new ConcurrentSet();
        AutoCrystal.placedPos = new HashSet<BlockPos>();
        AutoCrystal.brokenPos = new HashSet<BlockPos>();
    }
    
    public enum PredictTimer
    {
        NONE,  
        BREAK,  
        PREDICT;
    }
    
    public enum AntiFriendPop
    {
        NONE,  
        PLACE,  
        BREAK,  
        ALL;
    }
    
    public enum ThreadMode
    {
        NONE,  
        POOL,  
        SOUND,  
        WHILE;
    }
    
    public enum AutoSwitch
    {
        NONE,  
        TOGGLE,  
        ALWAYS,  
        SILENT;
    }
    
    public enum Raytrace
    {
        NONE,  
        PLACE,  
        BREAK,  
        FULL;
    }
    
    public enum Switch
    {
        ALWAYS,  
        BREAKSLOT,  
        CALC;
    }
    
    public enum Logic
    {
        BREAKPLACE,  
        PLACEBREAK;
    }
    
    public enum Target
    {
        CLOSEST,  
        UNSAFE,  
        DAMAGE;
    }
    
    public enum Rotate
    {
        OFF,  
        PLACE,  
        BREAK,  
        ALL;
    }
    
    public enum DamageSync
    {
        NONE,  
        PLACE,  
        BREAK;
    }
    
    public enum Settings
    {
        PLACE,  
        BREAK,  
        RENDER,  
        MISC,  
        DEV;
    }
    
    public enum RenderMode
    {
        STATIC,  
        FADE,  
        GLIDE;
    }
    
    public static class PlaceInfo
    {
        private final BlockPos pos;
        private final boolean offhand;
        private final boolean placeSwing;
        private final boolean exactHand;
        private final boolean silent;
        
        public PlaceInfo(final BlockPos pos,  final boolean offhand,  final boolean placeSwing,  final boolean exactHand,  final boolean silent) {
            this.pos = pos;
            this.offhand = offhand;
            this.placeSwing = placeSwing;
            this.exactHand = exactHand;
            this.silent = silent;
        }
        
        public void runPlace() {
            BlockUtil.placeCrystalOnBlock(this.pos,  this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND,  this.placeSwing,  this.exactHand,  this.silent);
        }
    }
    
    private static class RAutoCrystal implements Runnable
    {
        private static RAutoCrystal instance;
        private AutoCrystal autoCrystal;
        
        public static RAutoCrystal getInstance(final AutoCrystal autoCrystal) {
            if (RAutoCrystal.instance == null) {
                RAutoCrystal.instance = new RAutoCrystal();
                RAutoCrystal.instance.autoCrystal = autoCrystal;
            }
            return RAutoCrystal.instance;
        }
        
        @Override
        public void run() {
            if (this.autoCrystal.threadMode.getValue() == ThreadMode.WHILE) {
                while (this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == ThreadMode.WHILE) {
                    while (Phobos.eventManager.ticksOngoing()) {}
                    if (this.autoCrystal.shouldInterrupt.get()) {
                        this.autoCrystal.shouldInterrupt.set(false);
                        this.autoCrystal.syncroTimer.reset();
                        this.autoCrystal.thread.interrupt();
                        break;
                    }
                    this.autoCrystal.threadOngoing.set(true);
                    Phobos.safetyManager.doSafetyCheck();
                    this.autoCrystal.doAutoCrystal();
                    this.autoCrystal.threadOngoing.set(false);
                    try {
                        Thread.sleep(this.autoCrystal.threadDelay.getValue());
                    }
                    catch (InterruptedException e) {
                        this.autoCrystal.thread.interrupt();
                        e.printStackTrace();
                    }
                }
            }
            else if (this.autoCrystal.threadMode.getValue() != ThreadMode.NONE && this.autoCrystal.isOn()) {
                while (Phobos.eventManager.ticksOngoing()) {}
                this.autoCrystal.threadOngoing.set(true);
                Phobos.safetyManager.doSafetyCheck();
                this.autoCrystal.doAutoCrystal();
                this.autoCrystal.threadOngoing.set(false);
            }
        }
    }
    
    private class RenderPos
    {
        private BlockPos renderPos;
        private float renderTime;
        
        public RenderPos(final BlockPos pos,  final float time) {
            this.renderPos = pos;
            this.renderTime = time;
        }
        
        public BlockPos getPos() {
            return this.renderPos;
        }
        
        public void setPos(final BlockPos pos) {
            this.renderPos = pos;
        }
        
        public float getRenderTime() {
            return this.renderTime;
        }
        
        public void setRenderTime(final float time) {
            this.renderTime = time;
        }
    }
}
© 2021 GitHub, Inc.
Terms
Privacy
Security
Status
Docs
Contact GitHub
Pricing
API
Training
Blog
About
Loading complete
