package me.travis.wurstplusthree.hack.player;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.StepEvent;
import me.travis.wurstplusthree.event.processor.CommitEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer;

@Hack.Registration(name = "Step", description = "steps up things", category = Hack.Category.PLAYER, isListening = false)
public class Step extends Hack {

    BooleanSetting vanilla = new BooleanSetting("Vanilla", false, this);
    IntSetting height = new IntSetting("Height", 2, 1, 2, this);

    @CommitEvent
    public void onStep(StepEvent event) {
        if (nullCheck()) return;
        if (mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.collidedVertically && mc.player.fallDistance == 0.0f && !mc.gameSettings.keyBindJump.pressed && !mc.player.isOnLadder() && !WurstplusThree.HACKS.ishackEnabled("Speed")) {
            event.setHeight(this.height.getValue());
            double rheight = mc.player.getEntityBoundingBox().minY - mc.player.posY;
            if (rheight >= 0.625) {
                if (!this.vanilla.getValue()) {
                    this.ncpStep(rheight);
                }
            }
        } else {
            event.setHeight(0.6f);
        }
    }
    
    private void ncpStep(double height) {
        block12:
        {
            double y;
            double posZ;
            double posX;
            block11:
            {
                posX = mc.player.posX;
                posZ = mc.player.posZ;
                y = mc.player.posY;
                if (!(height < 1.1)) break block11;
                double first = 0.42;
                double second = 0.75;
                if (height != 1.0) {
                    first *= height;
                    second *= height;
                    if (first > 0.425) {
                        first = 0.425;
                    }
                    if (second > 0.78) {
                        second = 0.78;
                    }
                    if (second < 0.49) {
                        second = 0.49;
                    }
                }
                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + first, posZ, false));
                if (!(y + second < y + height)) break block12;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + second, posZ, false));
                break block12;
            }
            if (height < 1.6) {
                for (double off : new double[]{0.42, 0.33, 0.24, 0.083, -0.078}) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y += off, posZ, false));
                }
            } else if (height < 2.1) {
                for (double off : new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869}) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
                }
            } else {
                for (double off : new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907}) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
                }
            }
        }
    }

}
