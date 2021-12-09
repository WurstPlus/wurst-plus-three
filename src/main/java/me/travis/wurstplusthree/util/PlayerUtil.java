package me.travis.wurstplusthree.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.util.elements.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerUtil implements Globals {

    public static UUID getUUIDFromName(final String name) {
        try {
            final lookUpUUID process = new lookUpUUID(name);
            final Thread thread = new Thread(process);
            thread.start();
            thread.join();
            return process.getUUID();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy - h); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plusY, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static BlockPos getPlayerPos(double pY) {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY + pY), Math.floor(mc.player.posZ));
    }

    public static double[] forward(final double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockEnderChest)
                    return i;
                else if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
    }

    public static int findSandInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockConcretePowder)
                    return i;
            }
        }
        return -1;
    }

    /*
        Fixed a memory leak - A2H
    */
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
      }

    public static boolean isInHole() {

        BlockPos player_block = getPlayerPos();

        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR;
    }

    public static String requestIDs(final String data) {
        try {
            final String query = "https://api.mojang.com/profiles/minecraft";
            final URL url = new URL(query);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            final OutputStream os = conn.getOutputStream();
            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            final InputStream in = new BufferedInputStream(conn.getInputStream());
            final String res = convertStreamToString(in);
            in.close();
            conn.disconnect();
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0 || entity.moveStrafing != 0;
    }

    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        double[] dir = forward(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player != null && mc.player.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static class lookUpUUID implements Runnable {
        private final String name;
        private volatile UUID uuid;

        public lookUpUUID(final String name) {
            this.name = name;
        }

        @Override
        public void run() {
            NetworkPlayerInfo profile;
            try {
                final ArrayList<NetworkPlayerInfo> infoMap = new ArrayList<NetworkPlayerInfo>(Objects.requireNonNull(mc.getConnection()).getPlayerInfoMap());
                profile = infoMap.stream().filter(networkPlayerInfo -> networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
                assert profile != null;
                this.uuid = profile.getGameProfile().getId();
            } catch (Exception e2) {
                profile = null;
            }
            if (profile == null) {
                final String s = PlayerUtil.requestIDs("[\"" + this.name + "\"]");
                if (s == null || s.isEmpty()) {
                } else {
                    final JsonElement element = new JsonParser().parse(s);
                    if (element.getAsJsonArray().size() == 0) {
                    } else {
                        try {
                            final String id = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                            this.uuid = UUIDTypeAdapter.fromString(id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }

    public static Item getBestItem(Block block) {
        String tool = block.getHarvestTool(block.getDefaultState());
        if (tool != null) {
            switch (tool) {
                case "axe":
                    return Items.DIAMOND_AXE;
                case "shovel":
                    return Items.DIAMOND_SHOVEL;
                default:
                    return Items.DIAMOND_PICKAXE;
            }
        } else {
            return Items.DIAMOND_PICKAXE;
        }

    }

    public static ItemStack getItemStackFromItem(Item item) {
        if (mc.player == null) return null;
        for (int slot = 0; slot <= 9; slot++) {
            if (mc.player.inventory.getStackInSlot(slot).getItem() == item)
                return mc.player.inventory.getStackInSlot(slot);
        }
        return null;
    }

    public enum FacingDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST,
    }

    public static FacingDirection getFacing() {
        int yaw = (int) Math.floor(mc.player.getRotationYawHead());
        if (yaw <= 0) {
            yaw += 360;
        }
        yaw = (yaw % 360 + 360) % 360;
        yaw += 45;
        int facing = yaw / 45;

        switch (facing) {
            case 0:
            case 1:
                return FacingDirection.SOUTH;
            case 2:
            case 3:
                return FacingDirection.WEST;
            case 4:
            case 5:
                return FacingDirection.NORTH;
            case 6:
            case 7:
                return FacingDirection.EAST;
        }
        return FacingDirection.SOUTH;
    }

    private static final BlockPos[] surroundOffset =
            {
                    new BlockPos(0, 0, -1), // north
                    new BlockPos(1, 0, 0), // east
                    new BlockPos(0, 0, 1), // south
                    new BlockPos(-1, 0, 0) // west
            };

    public static BlockPos GetPositionVectorBlockPos(Entity entity, @Nullable BlockPos toAdd)
    {
        final Vec3d v = entity.getPositionVector();

        if (toAdd == null)
            return new BlockPos(v.x, v.y, v.z);

        return new BlockPos(v.x, v.y, v.z).add(toAdd);
    }

    public static ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>> GetPlayersReadyToBeCitied()
    {
        ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>> players = new ArrayList<Pair<EntityPlayer, ArrayList<BlockPos>>>();

        for (Entity entity : mc.world.playerEntities.stream().filter(entityPlayer -> !WurstplusThree.FRIEND_MANAGER.isFriend(entityPlayer.getName())).collect(Collectors.toList()))
        {
            ArrayList<BlockPos> positions = new ArrayList<BlockPos>();

            for (int i = 0; i < 4; ++i)
            {
                BlockPos o = GetPositionVectorBlockPos(entity, surroundOffset[i]);

                // ignore if the surrounding block is not obsidian
                if (mc.world.getBlockState(o).getBlock() != Blocks.OBSIDIAN)
                    continue;

                boolean passCheck = false;

                switch (i)
                {
                    case 0:
                        passCheck = CrystalUtil.canPlaceCrystal(o.north(1).down(), false, false);
                        break;
                    case 1:
                        passCheck = CrystalUtil.canPlaceCrystal(o.east(1).down(), false, false);
                        break;
                    case 2:
                        passCheck = CrystalUtil.canPlaceCrystal(o.south(1).down(), false, false);
                        break;
                    case 3:
                        passCheck = CrystalUtil.canPlaceCrystal(o.west(1).down(), false, false);
                        break;
                }

                if (passCheck)
                    positions.add(o);
            }

            if (!positions.isEmpty())
                players.add(new Pair<EntityPlayer, ArrayList<BlockPos>>((EntityPlayer)entity, positions));
        }

        return players;
    }

}
