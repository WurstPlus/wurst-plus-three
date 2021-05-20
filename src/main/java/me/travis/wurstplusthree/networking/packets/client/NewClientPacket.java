package me.travis.wurstplusthree.networking.packets.client;

import me.travis.wurstplusthree.networking.Packet;
import me.travis.wurstplusthree.networking.Sockets;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 18/05/2021
 */

public class NewClientPacket extends Packet {
    @Override
    public String[] run() throws IOException {
        Socket s = Sockets.createConnection();
        Sockets.sendData(s, "client:newclient:"+client);
        return Sockets.getData(s);
    }
}
