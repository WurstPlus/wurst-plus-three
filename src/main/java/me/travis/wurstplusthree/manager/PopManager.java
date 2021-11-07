package me.travis.wurstplusthree.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.util.Globals;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PopManager implements Globals {

    private Map<EntityPlayer, Integer> popList = new ConcurrentHashMap<>();
    public final List<String> toAnnouce = new ArrayList<>();

    public void onTotemPop(EntityPlayer player) {
        this.popTotem(player);
        if (!player.equals(mc.player) && player.isEntityAlive()) {
            this.toAnnouce.add(this.getPopString(player, this.getTotemPops(player)));
        }
    }

    public void onDeath(EntityPlayer player) {
        if (this.getTotemPops(player) != 0 && !player.equals(mc.player)) {
            this.toAnnouce.add(this.getDeathString(player, this.getTotemPops(player)));
        }
        this.resetPops(player);
    }

    public void onLogout() {
        this.clearList();
    }

    public void clearList() {
        this.popList = new ConcurrentHashMap<>();
    }

    public void resetPops(EntityPlayer player) {
        this.setTotemPops(player, 0);
    }

    public void popTotem(EntityPlayer player) {
        this.popList.merge(player, 1, Integer::sum);
    }

    public void setTotemPops(EntityPlayer player, int amount) {
        this.popList.put(player, amount);
    }

    public int getTotemPops(EntityPlayer player) {
        return this.popList.get(player) == null ? 0 : this.popList.get(player);
    }

    private String getDeathString(EntityPlayer player, int pops) {
        if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) {
            return "DUDE! you just let " + ChatFormatting.AQUA + player.getName() + ChatFormatting.RESET + " DIE after popping "
                    + ChatFormatting.GREEN + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        } else {
            return "LMAO " + ChatFormatting.RED + player.getName() + ChatFormatting.RESET + " just fucking DIED after popping "
                    + ChatFormatting.GREEN + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        }
    }

    private String getPopString(EntityPlayer player, int pops) {
        if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) {
            return "ur pal " + ChatFormatting.AQUA + player.getName() + ChatFormatting.RESET + " has now popped "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems") + " go help them";
        } else {
            return "shitter known as " + ChatFormatting.RED + player.getName() + ChatFormatting.RESET + " has now popped "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        }
    }

}
