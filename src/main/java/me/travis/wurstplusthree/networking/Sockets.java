package me.travis.wurstplusthree.networking;

import java.io.*;
import java.net.Socket;

/**
 * @author Madmegsox1
 * @since 18/05/2021
 */

public class Sockets {
    static final String IP = "0.0.0.0";
    static final int PORT = 4200;

    public static Socket createConnection() throws IOException {
        return new Socket(IP, PORT);
    }

    public static String[] getData(Socket socket) throws IOException {
        InputStream stream = socket.getInputStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        return r.readLine().split(":");
    }

    public static void sendData(Socket socket, String data) throws IOException {
        OutputStream stream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(stream, true);
        writer.println(data);
    }
}
