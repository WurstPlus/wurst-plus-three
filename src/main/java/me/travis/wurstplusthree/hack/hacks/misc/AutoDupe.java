package me.travis.wurstplusthree.hack.hacks.misc;

import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.HackPriority;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.EnumSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.BlockUtil;
import me.travis.wurstplusthree.util.ClientMessage;
import me.travis.wurstplusthree.util.InventoryUtil;
import me.travis.wurstplusthree.util.PlayerUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

/**
 * @author Madmegsox1
 * @since 10/07/2021
 */
@Hack.Registration(name = "Auto Dupe", category = Hack.Category.MISC, description = "Dupes for u", priority = HackPriority.Low)
public final class AutoDupe extends Hack {
    EnumSetting server = new EnumSetting("Server", "Wurst.Plus", Arrays.asList("Wurst.Plus"), this);
    EnumSetting modes = new EnumSetting("Mode", "Main", Arrays.asList("Main", "Slave"), this, s -> server.is("Wurst.Plus"));
    BooleanSetting sendChantMessage = new BooleanSetting("Chat Message", true, this, s -> server.is("Wurst.Plus"));
    BooleanSetting waitItems = new BooleanSetting("Wait For Items", true, this, s -> server.is("Wurst.Plus"));
    IntSetting delay = new IntSetting("Dupe Delay", 1, 0, 25, this);
    BooleanSetting rotations = new BooleanSetting("Rotations", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);

    private int stage;
    private BlockPos tntPos = null;
    private BlockPos chestPos = null;
    private boolean shouldWait;
    private int waitTicks;

    @Override
    public void onLogout(){
        this.disable();
    }

    @Override
    public void onEnable(){
        stage = 0;
        tntPos = null;
        chestPos = null;
        shouldWait = false;
        waitTicks = 0;
    }

    @Override
    public void onUpdate(){
        if(nullCheck())return;

        if(server.is("Wurst.Plus") && modes.is("Slave")) {
            BlockPos pPos = PlayerUtil.getPlayerPos();
            PlayerUtil.FacingDirection dir = PlayerUtil.getFacing();
            switch (dir) {
                case NORTH:
                    chestPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ() - 1);
                    break;
                case SOUTH:
                    chestPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ() + 1);
                    break;
                case EAST:
                    chestPos = new BlockPos(pPos.getX() + 1, pPos.getY(), pPos.getZ());
                    break;
                case WEST:
                    chestPos = new BlockPos(pPos.getX() - 1, pPos.getY(), pPos.getZ());
                    break;
            }
            if (chestPos == null) {
                ClientMessage.sendMessage("Face a direction!");
                this.toggle();
                return;
            }
            if(mc.world.getBlockState(chestPos).getBlock() == Blocks.CHEST) {
                if(!(mc.currentScreen instanceof GuiChest)) {
                    BlockUtil.openBlock(chestPos);
                }
            }
        }

        if(server.is("Wurst.Plus") && modes.is("Main")){
            BlockPos pPos = PlayerUtil.getPlayerPos();
            if(shouldWait){
                if(waitTicks < this.delay.getValue()){
                    waitTicks++;
                    ClientMessage.sendMessage("Waiting for " + waitTicks);
                    return;
                }
                shouldWait = false;
                stage = 0;
                waitTicks = 0;
                if(sendChantMessage.getValue()) {
                    mc.player.sendChatMessage("Auto duping thanks to Wurst + 3!");
                }
                return;
            }

            if(stage == 5){
                if(mc.world.getBlockState(chestPos).getBlock() != Blocks.CHEST){
                    if(waitItems.getValue()) {
                        for(Entity entity : mc.world.loadedEntityList){
                            if (entity instanceof EntityItem) {
                                if(entity.getDistance(chestPos.getX(), chestPos.getY(), chestPos.getZ()) <= 3){
                                    shouldWait = false;
                                    ClientMessage.sendMessage("Waiting for items to be picked up!");
                                    return;
                                }
                            }
                        }
                    }
                    shouldWait = true;
                    return;
                }
            }

            switch (stage){
                case 0:
                    PlayerUtil.FacingDirection dir = PlayerUtil.getFacing();
                    switch (dir) {
                        case NORTH:
                            chestPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ() - 1);
                            break;
                        case SOUTH:
                            chestPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ() + 1);
                            break;
                        case EAST:
                            chestPos = new BlockPos(pPos.getX() + 1, pPos.getY(), pPos.getZ());
                            break;
                        case WEST:
                            chestPos = new BlockPos(pPos.getX() - 1, pPos.getY(), pPos.getZ());
                            break;
                    }
                    if(chestPos == null){
                        ClientMessage.sendMessage("Face a direction!");
                        this.toggle();
                        return;
                    }

                    int cSlot = InventoryUtil.findHotbarBlock(BlockChest.class);
                    if(cSlot == -1){
                        ClientMessage.sendMessage("You do not have a chest in you hotbar!");
                        this.toggle();
                        return;
                    }
                    BlockUtil.placeBlock(chestPos, cSlot, rotations.getValue(), rotations.getValue(), swing);
                    stage++;
                    return;
                case 1:
                    PlayerUtil.FacingDirection dir1 = PlayerUtil.getFacing();
                    switch (dir1) {
                        case NORTH:
                            tntPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ() - 2);
                            break;
                        case SOUTH:
                            tntPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ() + 2);
                            break;
                        case EAST:
                            tntPos = new BlockPos(pPos.getX() + 2, pPos.getY(), pPos.getZ());
                            break;
                        case WEST:
                            tntPos = new BlockPos(pPos.getX() - 2, pPos.getY(), pPos.getZ());
                            break;
                    }

                    if(tntPos == null){
                        ClientMessage.sendMessage("Face a direction!");
                        this.toggle();
                        return;
                    }
                    int tntSlot = InventoryUtil.findHotbarBlock(BlockTNT.class);
                    if(tntSlot == -1){
                        ClientMessage.sendMessage("You do not have tnt in you hotbar!");
                        this.toggle();
                        return;
                    }
                    BlockUtil.placeBlock(tntPos, tntSlot, rotations.getValue(), rotations.getValue(), swing);
                    stage++;
                    return;
                case 2:
                    if(tntPos == null){
                        ClientMessage.sendMessage("Face a direction!");
                        this.toggle();
                        return;
                    }
                    if(mc.world.getBlockState(tntPos).getBlock() != Blocks.TNT){
                        ClientMessage.sendMessage("There is no tnt placed!");
                        this.toggle();
                        return;
                    }
                    int flitSlot = InventoryUtil.findHotbarBlock(ItemFlintAndSteel.class);
                    if(flitSlot == -1){
                        ClientMessage.sendMessage("There is no flit and steel in your hotbar");
                        this.toggle();
                        return;
                    }

                    int old = mc.playerController.currentPlayerItem;
                    mc.player.inventory.currentItem = flitSlot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(flitSlot));
                    mc.playerController.syncCurrentPlayItem();

                    useFlint(tntPos, EnumHand.MAIN_HAND);

                    mc.player.inventory.currentItem = old;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(old));
                    mc.playerController.syncCurrentPlayItem();

                    stage++;
                    return;
                case 3:
                    mc.player.closeScreen();
                    BlockUtil.openBlock(chestPos);
                    stage++;
                    return;
                case 4:
                    if(mc.currentScreen instanceof GuiChest) {
                        for (int i = 9; i < mc.player.inventory.getSizeInventory() + 13; i++){
                            mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE ,mc.player);
                        }
                    }
                    stage++;
                    return;
            }
        }
    }


    public void useFlint(final BlockPos pos, final EnumHand swingHand){
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + .5, pos.getY() - .5d, pos.getZ() + .5));
        EnumFacing f;
        if (result == null || result.sideHit == null) {
            f = EnumFacing.UP;
        } else {
            f = result.sideHit;
        }
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, f, swingHand, 0.0f, 0.0f, 0.0f));
    }
}
