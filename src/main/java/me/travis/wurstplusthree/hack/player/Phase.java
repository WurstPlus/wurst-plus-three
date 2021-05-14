package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

/**
 * @author Madmegsox1
 * @since 11/05/2021
 */

@Hack.Registration(name = "Phase", description = "Phases through blocks", category = Hack.Category.PLAYER, isListening = false)
public class Phase extends Hack {
    long last = 0;
    KeyBinding left, right, down, up;
    public Phase() {
        left = new KeyBinding("Left", Keyboard.KEY_LEFT, "combat");
        right = new KeyBinding("Right", Keyboard.KEY_RIGHT, "combat");
        down = new KeyBinding("Down", Keyboard.KEY_DOWN, "combat");
        up = new KeyBinding("Up", Keyboard.KEY_UP, "combat");
        ClientRegistry.registerKeyBinding(left);
        ClientRegistry.registerKeyBinding(right);
        ClientRegistry.registerKeyBinding(down);
        ClientRegistry.registerKeyBinding(up);
    }
    BooleanSetting twodelay = new BooleanSetting("Delay", true, this);
    IntSetting tickDelay = new IntSetting("TickDelay", 2, 0, 40, this);
    DoubleSetting speed = new DoubleSetting("Speed", 6.25, 0.0, 6.25, this);
    BooleanSetting advd = new BooleanSetting("AVD", false, this);
    BooleanSetting EnhancedRots = new BooleanSetting("EnhancedControl", false, this);
    IntSetting EnhancedRotsAmount = new IntSetting("EnhancedCtrlSpeed", 2, 0, 20, this);
    BooleanSetting invert = new BooleanSetting("InvertedYaw", false, this);
    BooleanSetting SendRotPackets = new BooleanSetting("SendRotPackets", true, this);
    BooleanSetting twobeepvp = new BooleanSetting("Bypass", true, this);
    BooleanSetting PUP = new BooleanSetting("PUP", true, this);
    EnumSetting cmode = new EnumSetting("Controlmode", "Rel", Arrays.asList("Rel", "Abs"), this);

    @SubscribeEvent
    public void packetEvent(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook pak = (SPacketPlayerPosLook) event.getPacket();
            pak.yaw = mc.player.rotationYaw;
            pak.pitch = mc.player.rotationPitch;
        }
        if(event.getPacket() instanceof SPacketPlayerPosLook){
            SPacketPlayerPosLook pak = (SPacketPlayerPosLook) event.getPacket();

            double dx = Math.abs(pak.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X) ? pak.x : mc.player.posX-pak.x);
            double dy = Math.abs(pak.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y) ? pak.y : mc.player.posY-pak.y);
            double dz = Math.abs(pak.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z) ? pak.z : mc.player.posZ-pak.z);

            if (dx<1E-3) dx=0;
            if (dz<1E-3) dz=0;

            //if (!(dx==0 && dy==0 && dz==0) && debug.getValue()) mc.player.sendMessage(new TextComponentString("position pak, dx="+dx+" dy="+dy+" dz="+dz));

            if (pak.yaw!=mc.player.rotationYaw || pak.pitch!=mc.player.rotationPitch) {
                if
                (SendRotPackets.getValue()) mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw,mc.player.rotationPitch,mc.player.onGround));
                pak.yaw = mc.player.rotationYaw;
                pak.pitch = mc.player.rotationPitch;
            }
        }
    }

    @Override
    public void onUpdate(){
        try {
            mc.player.noClip = true;
            if (tickDelay.getValue() > 0)
                if (mc.player.ticksExisted % tickDelay.getValue() != 0 && twodelay.getValue()) return;

            int eca = EnhancedRotsAmount.getValue();

            if (EnhancedRots.getValue() && up.isKeyDown()) mc.player.rotationPitch -= eca;
            if (EnhancedRots.getValue() && down.isKeyDown()) mc.player.rotationPitch += eca;

            if (EnhancedRots.getValue() && left.isKeyDown()) mc.player.rotationYaw -= eca;
            if (EnhancedRots.getValue() && right.isKeyDown()) mc.player.rotationYaw += eca;

            double yaw = (mc.player.rotationYaw + 90) * (invert.getValue() ? -1 : 1);

            double xDir, zDir;

            if (cmode.getValue().equals("Rel")) {
                double dO_numer = 0;
                double dO_denom = 0;

                if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                    dO_numer -= 90;
                    dO_denom++;
                }
                if (mc.gameSettings.keyBindRight.isKeyDown()) {
                    dO_numer += 90;
                    dO_denom++;
                }
                if (mc.gameSettings.keyBindBack.isKeyDown()) {
                    dO_numer += 180;
                    dO_denom++;
                }
                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    dO_denom++;

                }

                if (dO_denom > 0) yaw += (dO_numer / dO_denom) % 361; // calculate average yaw
                // lets not divide by zero thats bad :)

                if (yaw < 0) yaw = 360 - yaw;
                if (yaw > 360) yaw = yaw % 361;

                xDir = Math.cos(Math.toRadians(yaw));
                zDir = Math.sin(Math.toRadians(yaw));
            } else {
                // for absolute control/hell mode :)
                // W = +x
                // S = -x
                // A = -z
                // D = +z

                xDir = 0;
                zDir = 0;

                xDir += mc.gameSettings.keyBindForward.isKeyDown() ? 1 : 0;
                xDir -= mc.gameSettings.keyBindBack.isKeyDown() ? 1 : 0;

                zDir += mc.gameSettings.keyBindLeft.isKeyDown() ? 1 : 0;
                zDir -= mc.gameSettings.keyBindRight.isKeyDown() ? 1 : 0;
            }

            if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
                mc.player.motionX = xDir * (speed.getValue() / 100);
                mc.player.motionZ = zDir * (speed.getValue() / 100);
            }
            mc.player.motionY = 0;

            boolean yes = false;
            if (advd.getValue()) {
                if (last + 50 >= System.currentTimeMillis()) {
                    yes = false;
                } else {
                    last = System.currentTimeMillis();
                    yes = true;
                }
            }

            mc.player.noClip = true;
            if (yes)
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, mc.player.posY + (mc.player.posY <
                        (twobeepvp.getValue() ? 1.1 : -0.98) ? (speed.getValue() / 100) : 0) + (mc.gameSettings.keyBindJump.isKeyDown() ? (speed.getValue() / 100) : 0) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (speed.getValue() / 100) : 0), mc.player.posZ + mc.player.motionZ, false)); // mc.player.rotationYaw, mc.player.rotationPitch, false));

            if (PUP.getValue()) {
                mc.player.noClip = true;
                mc.player.setLocationAndAngles(mc.player.posX + mc.player.motionX, mc.player.posY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch);
            }

            mc.player.noClip = true;
            if (yes)
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, mc.player.posY - 42069, mc.player.posZ + mc.player.motionZ, true));   //, mc.player.rotationYaw , mc.player.rotationPitch, true));

        /*double dx=0,dy=0,dz=0;
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            dy=-0.0625D;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            dy= 0.0625D;
        }*/

        /*mc.player.setLocationAndAngles(mc.player.posX+dx, mc.player.posY+dy, mc.player.posZ+dz, mc.player.rotationYaw, mc.player.rotationPitch);
        mc.getConnection().sendPacket(new CPacketPlayer.Position(
                mc.player.posX, mc.player.posY, mc.player.posZ, false));*/
        } catch (Exception e) {
            disable();
        }
        return;
    }

    @Override
    public void onDisable(){
        if (mc.player!=null) mc.player.noClip=false;
    }
}
