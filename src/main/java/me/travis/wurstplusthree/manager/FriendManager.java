package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

import java.util.ArrayList;
import java.util.List;

public class FriendManager implements Globals {

    private final List<WurstplusPlayer> friends;

    public FriendManager() {
         this.friends = new ArrayList<>();
    }

    public void addFriend(String name) {
        this.friends.add(new WurstplusPlayer(name));
    }

    public void addFriend(WurstplusPlayer player) {
        this.friends.add(player);
    }

    public void removeFriend(String name) {
        this.friends.removeIf(player -> player.getName().equalsIgnoreCase(name));
    }

    public void removeFriend(WurstplusPlayer player) {
        this.friends.remove(player);
    }

    public boolean isFriend(String name) {
        for (WurstplusPlayer player : this.friends) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
