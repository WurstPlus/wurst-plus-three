package me.travis.wurstplusthree.util.logview;

import me.travis.wurstplusthree.WurstplusThree;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author Madmegsox1
 * @since 02/06/2021
 */

public class Gui extends JFrame {

    private static final Path logPath = Paths.get("logs/latest.log");
    public JTextArea display;
    public ArrayList<String> log;
    public RefreshLog thread;

    public Gui() {
        draw();
    }

    public void draw() {
        JFrame frame = new JFrame("Wurst + 3 Log viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        log = getLog();

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Log"));

        display = new JTextArea(45, 65);
        display.setEditable(false);
        for(String l : log){
            display.append(l + "\n");
        }
        JScrollPane scroll = new JScrollPane(display);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scroll);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                thread.stop(false);
            }
        });


        frame.add(panel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setThead(RefreshLog thread){
        this.thread = thread;
    }

    public ArrayList<String> getLog(){
        ArrayList<String> log = new ArrayList<>();
        try {
            log.addAll(Files.readAllLines(logPath));
        }catch (IOException e){
            WurstplusThree.LOGGER.error(e);
        }
        return log;
    }
}

class RefreshLog extends Thread{
    public Gui INSTANCE;
    public boolean running;
    public RefreshLog(Gui instance){
        INSTANCE = instance;
        running = true;
    }

    @Override
    public void run(){
        long time = System.currentTimeMillis();
        while (running){
            if(System.currentTimeMillis() - time == 500){
                time = System.currentTimeMillis();
                for(String l : INSTANCE.getLog()){
                    if(!INSTANCE.log.contains(l)) {
                        INSTANCE.log.add(l);
                        INSTANCE.display.append(l + "\n");
                    }
                }
            }
        }
    }

    public void stop(boolean stop){
        this.running = stop;
    }
}
