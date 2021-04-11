package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager implements Globals {

    private final List<WurstplusPlayer> enemies;

    public EnemyManager() {
        this.enemies = new ArrayList<>();
    }

    public void addEnemy(String name) {
        this.enemies.add(new WurstplusPlayer(name));
    }

    public void addEnemy(WurstplusPlayer player) {
        this.enemies.add(player);
    }

    public void removeEnemy(String name) {
        this.enemies.removeIf(player -> player.getName().equalsIgnoreCase(name));
    }

    public void removeEnemy(WurstplusPlayer player) {
        this.enemies.remove(player);
    }

    public boolean isEnemy(String name) {
        for (WurstplusPlayer player : this.enemies) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
