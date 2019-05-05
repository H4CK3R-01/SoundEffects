package de.florian.Search;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class MusicPlayer {
    private JFrame frame;
    private JLabel songNameLabel = new JLabel();
    private JButton playButton = new JButton("Play");
    private JButton pauseButton = new JButton("Pause");
    private JButton resumeButton = new JButton("Resume");
    private JButton stopButton = new JButton("Stop");
    private FileInputStream fileInputStream;
    private BufferedInputStream bufferedInputStream;
    private File myFile;
    private String filename;
    private long totalLength;
    private long pause;
    private Player player;
    private Thread playThread;
    private Thread resumeThread;

    MusicPlayer(String name, File file) {
        this.filename = name;
        this.myFile = file;

        prepareGUI();
        addActionEvents();
        playThread = new Thread(runnablePlay);
        resumeThread = new Thread(runnableResume);

    }

    public void prepareGUI() {
        frame = new JFrame();
        frame.setTitle("Music Player");
        frame.getContentPane().setLayout(null);
        frame.setSize(450, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                player = null;
            }
        });


        songNameLabel.setText(filename);
        songNameLabel.setBounds(100, 50, 300, 30);
        frame.add(songNameLabel);

        playButton.setBounds(30, 110, 100, 30);
        frame.add(playButton);

        pauseButton.setBounds(120, 110, 100, 30);
        frame.add(pauseButton);

        resumeButton.setBounds(210, 110, 100, 30);
        frame.add(resumeButton);

        stopButton.setBounds(300, 110, 100, 30);
        frame.add(stopButton);

    }

    public void addActionEvents() {
        //registering action listener to buttons
        playButton.addActionListener(this.actionListener);
        pauseButton.addActionListener(this.actionListener);
        resumeButton.addActionListener(this.actionListener);
        stopButton.addActionListener(this.actionListener);
    }

    ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == playButton) {
                playThread.start();
            }
            if (e.getSource() == pauseButton) {
                if (player != null) {
                    try {
                        pause = fileInputStream.available();
                        player.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (e.getSource() == resumeButton) {
                resumeThread.start();
            }
            if (e.getSource() == stopButton) {
                if (player != null) {
                    player.close();
                    songNameLabel.setText("");
                }

            }
        }
    };

    Runnable runnablePlay = new Runnable() {
        @Override
        public void run() {
            try {
                //code for play button
                fileInputStream = new FileInputStream(myFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                totalLength = fileInputStream.available();
                player.play();//starting music
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableResume = new Runnable() {
        @Override
        public void run() {
            try {
                //code for resume button
                fileInputStream = new FileInputStream(myFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                player = new Player(bufferedInputStream);
                fileInputStream.skip(totalLength - pause);
                player.play();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}