package me.travis.wurstplusthree.networking.chat.packets.ping;

import me.travis.wurstplusthree.networking.chat.Packet;
import me.travis.wurstplusthree.networking.chat.Sockets;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 20/05/2021
 */

public class PingUpPacket extends Packet {
    public String[] run(String key) throws IOException {
        String client = mc.player.getName() + ":" + mc.player.getUniqueID();
        Socket s = Sockets.createConnection();
        Sockets.sendData(s, "client:pingup:"+client+":"+key);
        String[] data = Sockets.getData(s);
        s.close();
        return data;
    }
}
