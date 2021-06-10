package me.travis.wurstplusthree.networking.handler;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.networking.Packet;
import me.travis.wurstplusthree.networking.packets.ping.PingGetGlobal;

/**
 * @author Madmegsox1
 * @since 30/05/2021
 */

public class ChatHandling extends Thread {
    ChatMode mode;
    String name;
    boolean running;
    long lastPing;

    public ChatHandling(){
        mode = ChatMode.SERVER;
        name = "";
        running = false;
        lastPing = System.currentTimeMillis();
    }

    @Override
    public void run(){
        lastPing = System.currentTimeMillis();
        chatLoop();
    }

    public void chatLoop(){
        while (running) {
            try {
                if (mode.equals(ChatMode.GLOBAL)) {
                    if (System.currentTimeMillis() - lastPing == 1000) {
                        Packet getChatPacket = new PingGetGlobal();
                        String[] data = getChatPacket.run(WurstplusThree.CLIENT_HANDLING.token);
                        if(data[0].equals("server") && data[1].equals("pinggetglobal")){
                            String[] messages = data[3].split(";");
                            //Handel display messages
                        }
                        lastPing = System.currentTimeMillis();
                    }
                }else if(mode.equals(ChatMode.DIRECT)){
                    if (System.currentTimeMillis() - lastPing == 1000) {

                    }
                }
            }catch (Exception e){
                WurstplusThree.LOGGER.error("Exception in ChatLoop for IRC " + e);
            }
        }
    }

    public void setToDirect(String name){
        this.mode = ChatMode.DIRECT;
        this.name = name;
    }

    public void setToGlobal(){
        mode = ChatMode.GLOBAL;
    }

    public void setToServer(){
        mode = ChatMode.SERVER;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    enum ChatMode {
        GLOBAL,
        DIRECT,
        SERVER
    }
}