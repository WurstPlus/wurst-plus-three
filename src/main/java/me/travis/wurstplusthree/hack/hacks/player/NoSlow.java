package me.travis.wurstplusthree.hack.hacks.player;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemShield;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author Madmegsox1
 * @since 23/07/2021
 */
@Hack.Registration(name = "NoSlow", description = "Lets you move freely", category = Hack.Category.PLAYER, priority = HackPriority.Low)
public class NoSlow extends Hack {
    BooleanSetting inventoryMove = new BooleanSetting("Inventory Move", true, this);
    BooleanSetting noInput = new BooleanSetting("No Input GUIs", true, this);
    BooleanSetting items = new BooleanSetting("Items", true, this);
    //BooleanSetting ncp = new BooleanSetting("NCP", false, this);

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if(nullCheck())return;
        if (inventoryMove.getValue() && mc.currentScreen != null)
        {
            if (noInput.getValue()) {
                if (mc.currentScreen instanceof GuiChat) {
                    return;
                }
            }

            mc.player.movementInput.moveStrafe = 0.0F;
            mc.player.movementInput.moveForward = 0.0F;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()))
            {
                ++mc.player.movementInput.moveForward;
                mc.player.movementInput.forwardKeyDown = true;
            }
            else
            {
                mc.player.movementInput.forwardKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()))
            {
                --mc.player.movementInput.moveForward;
                mc.player.movementInput.backKeyDown = true;
            }
            else
            {
                mc.player.movementInput.backKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()))
            {
                ++mc.player.movementInput.moveStrafe;
                mc.player.movementInput.leftKeyDown = true;
            }
            else
            {
                mc.player.movementInput.leftKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()))
            {
                --mc.player.movementInput.moveStrafe;
                mc.player.movementInput.rightKeyDown = true;
            }
            else
            {
                mc.player.movementInput.rightKeyDown = false;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
            mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());

        }
    }

    @Override
    public void onTick(){
        if(nullCheck())return;
        if (mc.player.isHandActive())
        {
            if (mc.player.getHeldItem(mc.player.getActiveHand()).getItem() instanceof ItemShield)
            {
                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0 && mc.player.getItemInUseMaxCount() >= 8)
                {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                }
            }
        }
    }

    @SubscribeEvent
    public void onMove(InputUpdateEvent event) {
        if(nullCheck())return;
        if (items.getValue() && mc.player.isHandActive() && !mc.player.isRiding())
        {
            mc.player.movementInput.moveForward /= 0.2F;
            mc.player.movementInput.moveStrafe /= 0.2F;
        }
    }
}
