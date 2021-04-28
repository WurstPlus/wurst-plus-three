package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

import java.util.ArrayList;
import java.util.List;

public class FriendManager implements Globals {

    private List<WurstplusPlayer> friends;

    public FriendManager() {
         this.friends = new ArrayList<>();
    }

    public void addFriend(String name) {
        if (!this.isFriend(name)) {
            this.friends.add(new WurstplusPlayer(name));
        }
    }

    public void removeFriend(String name) {
        this.friends.removeIf(player -> player.getName().equalsIgnoreCase(name));
    }

    public boolean isFriend(String name) {
        for (WurstplusPlayer player : this.friends) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFriends() {
        return !this.friends.isEmpty();
    }

    public List<WurstplusPlayer> getFriends() {
        return this.friends;
    }

    public void toggleFriend(String name) {
        if (this.isFriend(name)) {
            this.removeFriend(name);
        } else {
            this.addFriend(name);
        }
    }

    public void clear() {
        this.friends.clear();
    }

    public void setFriends(List<WurstplusPlayer> list) {
        this.friends = list;
    }

}
