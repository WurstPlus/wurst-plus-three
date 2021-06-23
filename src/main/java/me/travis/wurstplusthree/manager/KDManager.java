package me.travis.wurstplusthree.manager;

import me.travis.wurstplusthree.util.Globals;

public class KDManager implements Globals {

    private int kills;
    private int deaths;

    private int currentKills;
    
    private double getKD() {
        return (double) kills / (double) deaths;
    }

    public void addDeath() {
        deaths++;
    }

}
