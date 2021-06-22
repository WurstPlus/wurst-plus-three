package me.travis.wurstplusthree.hack.hacks.player;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer;

@Hack.Registration(name = "Step", description = "steps up things", category = Hack.Category.PLAYER)
public class Step extends Hack {

    BooleanSetting vanilla = new BooleanSetting("Vanilla", false, this);

    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        if (mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.collidedVertically && mc.player.fallDistance == 0.0f && !mc.gameSettings.keyBindJump.pressed && !mc.player.isOnLadder() && !WurstplusThree.HACKS.ishackEnabled("Speed")) {
            if (!vanilla.getValue()) {
                ncpStep();
            }
        }
    }

    @Override
    public void onEnable() {
        if (vanilla.getValue()) {
            mc.player.stepHeight = 2f;
        }
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    private void ncpStep() {
        double y;
        double posZ;
        double posX;
        posX = mc.player.posX;
        posZ = mc.player.posZ;
        y = mc.player.posY;
        for (double off : new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869}) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
        }
    }

}
