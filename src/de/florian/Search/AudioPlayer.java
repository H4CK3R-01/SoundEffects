package de.florian.Search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class AudioPlayer extends Thread {
    private File sound;
    private String name;

    private JFrame player;
    private JButton play, pause;
    private JSlider lautstaerke;
    private JLabel soundName;

    public AudioPlayer(File file) {
        this.sound = file;

        this.start();
    }

    public void run() {
        player = new JFrame("Sound abspielen");
        player.setSize(new Dimension(450, 500));
        player.setLocationRelativeTo(null);
        player.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                player = null;
            }
        });

        soundName = new JLabel(name);
        play = new JButton("Play");
        pause = new JButton("Pause");
        lautstaerke = new JSlider();


        player.add(soundName);
        player.add(play);
        player.add(pause);
        player.add(lautstaerke);


        player.setVisible(true);
    }
}
