package me.travis.wurstplusthree.networking.packets.client;

import me.travis.wurstplusthree.networking.Packet;
import me.travis.wurstplusthree.networking.Sockets;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 20/05/2021
 */

public class GetClientFromNamePacket extends Packet {
    @Override
    public String[] run(String... arguments) throws IOException {
        Socket s = Sockets.createConnection();
        Sockets.sendData(s, "client:getclientname:"+arguments[0]);
        String[] data = Sockets.getData(s);
        s.close();
        return data;
    }
}
