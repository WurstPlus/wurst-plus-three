package me.travis.wurstplusthree.command.commands;

import me.travis.wurstplusthree.command.Command;
import me.travis.wurstplusthree.util.ClientMessage;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Madmegsox1
 * @since 01/06/2021
 * - shit code from my old client
 */

public class ClipBind extends Command {
    static List<Integer> keys = new ArrayList<>();

    public ClipBind() {
        super("clipbind", "bindclip");
    }

    @Override
    public void execute(String[] message) {
        keys.clear();
        int key = 0;
        String keylist = "";
        for (String s : message) { // cba changing this to a switch statement lol
            if (s.toLowerCase().equals("shift")) {
                key = KeyEvent.VK_SHIFT;
            } else if (s.toLowerCase().equals("alt")) {
                key = KeyEvent.VK_ALT;
            } else if (s.toLowerCase().equals("ctrl")) {
                key = KeyEvent.VK_CONTROL;
            } else if (s.toLowerCase().equals("f1")) {
                key = KeyEvent.VK_F1;
            } else if (s.toLowerCase().equals("f2")) {
                key = KeyEvent.VK_F2;
            } else if (s.toLowerCase().equals("f3")) {
                key = KeyEvent.VK_F3;
            } else if (s.toLowerCase().equals("f4")) {
                key = KeyEvent.VK_F4;
            } else if (s.toLowerCase().equals("f5")) {
                key = KeyEvent.VK_F5;
            } else if (s.toLowerCase().equals("f6")) {
                key = KeyEvent.VK_F6;
            } else if (s.toLowerCase().equals("f7")) {
                key = KeyEvent.VK_F7;
            } else if (s.toLowerCase().equals("f8")) {
                key = KeyEvent.VK_F8;
            } else if (s.toLowerCase().equals("f9")) {
                key = KeyEvent.VK_F9;
            } else if (s.toLowerCase().equals("f10")) {
                key = KeyEvent.VK_F10;
            } else if (s.toLowerCase().equals("f11")) {
                key = KeyEvent.VK_F11;
            } else if (s.toLowerCase().equals("f12")) {
                key = KeyEvent.VK_F12;
            } else if (s.toLowerCase().equals("q")) {
                key = KeyEvent.VK_Q;
            } else if (s.toLowerCase().equals("w")) {
                key = KeyEvent.VK_W;
            } else if (s.toLowerCase().equals("e")) {
                key = KeyEvent.VK_E;
            } else if (s.toLowerCase().equals("r")) {
                key = KeyEvent.VK_R;
            } else if (s.toLowerCase().equals("t")) {
                key = KeyEvent.VK_T;
            } else if (s.toLowerCase().equals("y")) {
                key = KeyEvent.VK_Y;
            } else if (s.toLowerCase().equals("u")) {
                key = KeyEvent.VK_U;
            } else if (s.toLowerCase().equals("i")) {
                key = KeyEvent.VK_I;
            } else if (s.toLowerCase().equals("o")) {
                key = KeyEvent.VK_O;
            } else if (s.toLowerCase().equals("p")) {
                key = KeyEvent.VK_P;
            } else if (s.toLowerCase().equals("a")) {
                key = KeyEvent.VK_A;
            } else if (s.toLowerCase().equals("s")) {
                key = KeyEvent.VK_S;
            } else if (s.toLowerCase().equals("d")) {
                key = KeyEvent.VK_D;
            } else if (s.toLowerCase().equals("f")) {
                key = KeyEvent.VK_F;
            } else if (s.toLowerCase().equals("g")) {
                key = KeyEvent.VK_G;
            } else if (s.toLowerCase().equals("h")) {
                key = KeyEvent.VK_H;
            } else if (s.toLowerCase().equals("j")) {
                key = KeyEvent.VK_J;
            } else if (s.toLowerCase().equals("k")) {
                key = KeyEvent.VK_K;
            } else if (s.toLowerCase().equals("l")) {
                key = KeyEvent.VK_L;
            } else if (s.toLowerCase().equals("z")) {
                key = KeyEvent.VK_Z;
            } else if (s.toLowerCase().equals("x")) {
                key = KeyEvent.VK_X;
            } else if (s.toLowerCase().equals("c")) {
                key = KeyEvent.VK_C;
            } else if (s.toLowerCase().equals("v")) {
                key = KeyEvent.VK_V;
            } else if (s.toLowerCase().equals("b")) {
                key = KeyEvent.VK_B;
            } else if (s.toLowerCase().equals("n")) {
                key = KeyEvent.VK_N;
            } else if (s.toLowerCase().equals("m")) {
                key = KeyEvent.VK_M;
            } else if (s.toLowerCase().equals(",")) {
                key = KeyEvent.VK_COMMA;
            } else if (s.toLowerCase().equals(".")) {
                key = KeyEvent.VK_PERIOD;
            } else if (s.toLowerCase().equals(";")) {
                key = KeyEvent.VK_SEMICOLON;
            } else if (s.toLowerCase().equals("1")) {
                key = KeyEvent.VK_1;
            } else if (s.toLowerCase().equals("2")) {
                key = KeyEvent.VK_2;
            } else if (s.toLowerCase().equals("3")) {
                key = KeyEvent.VK_3;
            } else if (s.toLowerCase().equals("4")) {
                key = KeyEvent.VK_4;
            } else if (s.toLowerCase().equals("5")) {
                key = KeyEvent.VK_5;
            } else if (s.toLowerCase().equals("6")) {
                key = KeyEvent.VK_6;
            } else if (s.toLowerCase().equals("7")) {
                key = KeyEvent.VK_7;
            } else if (s.toLowerCase().equals("8")) {
                key = KeyEvent.VK_8;
            } else if (s.toLowerCase().equals("9")) {
                key = KeyEvent.VK_9;
            } else if (s.toLowerCase().equals("0")) {
                key = KeyEvent.VK_0;
            } else if (s.toLowerCase().equals("=")) {
                key = KeyEvent.VK_EQUALS;
            } else if (s.toLowerCase().equals("-")) {
                key = KeyEvent.VK_MINUS;
            } else {
                ClientMessage.sendErrorMessage("Error key not supported");
                return;
            }
            keys.add(key);
            keylist += s + " ";
        }
        ClientMessage.sendMessage("Bind set to " + keylist);
    }

    public static List<Integer> getKeys(){
        return keys;
    }

    public static void setKeys(List<Integer> keyArray){
        keys.clear();
        keys = keyArray;
    }
}
