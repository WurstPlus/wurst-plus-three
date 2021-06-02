package me.travis.wurstplusthree.util.logview;

/**
 * @author Madmegsox1
 * @since 02/06/2021
 */

public class Threads extends Thread {

    @Override
    public void run(){
        Gui gui = new Gui();
        RefreshLog refreshLog = new RefreshLog(gui);
        refreshLog.start();
        gui.setThead(refreshLog);
    }
}
