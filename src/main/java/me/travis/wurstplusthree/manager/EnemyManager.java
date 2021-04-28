package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager implements Globals {

    private List<WurstplusPlayer> enemies;

    public EnemyManager() {
        this.enemies = new ArrayList<>();
    }

    public void addEnemy(String name) {
        if (!this.isEnemy(name)) {
            this.enemies.add(new WurstplusPlayer(name));
        }
    }

    public void removeEnemy(String name) {
        this.enemies.removeIf(player -> player.getName().equalsIgnoreCase(name));
    }

    public boolean isEnemy(String name) {
        for (WurstplusPlayer player : this.enemies) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEnemies() {
        return !this.enemies.isEmpty();
    }

    public List<WurstplusPlayer> getEnemies() {
        return this.enemies;
    }

    public void clear() {
        this.enemies.clear();
    }

    public void setEnemies(List<WurstplusPlayer> list) {
        this.enemies = list;
    }

}
