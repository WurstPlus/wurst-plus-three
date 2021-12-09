package me.travis.wurstplusthree.networking.chat.handler;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.networking.chat.Packet;
import me.travis.wurstplusthree.networking.chat.packets.ping.PingGetGlobal;
import me.travis.wurstplusthree.util.ClientMessage;

import java.util.Arrays;

/**
 * @author Madmegsox1
 * @since 30/05/2021
 */

public class ChatHandling extends Thread {
    public ChatMode mode;
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
                        try {

                            Packet getChatPacket = new PingGetGlobal();
                            String[] data = getChatPacket.run(WurstplusThree.CLIENT_HANDLING.token);
                            if (data[0].equals("server") && data[1].equals("pinggetglobal")) {
                                String[] messages = data[3].split(";");
                                int ID = Integer.getInteger(messages[messages.length - 1].split("\\|")[0]);
                                Arrays.stream(messages).spliterator().forEachRemaining(msg -> {
                                    String[] msgList = msg.split("\\|");
                                    int msgId = Integer.getInteger(msgList[0]);
                                    if (msgId == ID) {
                                        ClientMessage.sendMessage("[" + msgList[1] + "] " + msgList[2]);
                                    }
                                });

                                //Handel display messages
                            }
                            lastPing = System.currentTimeMillis();
                        }catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
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