package de.florian.MusicPlayer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AudioPlayerUI extends JFrame implements ActionListener {
    private AudioPlayer player = new AudioPlayer();
    private Thread playbackThread;
    private PlayingTimer timer;

    private boolean isPlaying = false;
    private boolean isPause = false;

    private String audioFilePath;

    private JLabel labelFileName = new JLabel("Datei: ");
    private JLabel labelTimeCounter = new JLabel("00:00:00");
    private JLabel labelDuration = new JLabel("00:00:00");

    private JButton buttonPlay = new JButton("Play");
    private JButton buttonPause = new JButton("Pause");

    private JSlider sliderTime = new JSlider();


    public AudioPlayerUI() {
        super("SoundEffects - Player");
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource("img/SoundEffects.png")).getImage()); // Icon des Fensters festlegen
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
        buttonPlay.setEnabled(false);

        buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
        buttonPause.setEnabled(false);

        labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
        labelDuration.setFont(new Font("Sans", Font.BOLD, 12));

        sliderTime.setPreferredSize(new Dimension(400, 20));
        sliderTime.setEnabled(false);
        sliderTime.setValue(0);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        add(labelFileName, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        add(labelTimeCounter, constraints);

        constraints.gridx = 1;
        add(sliderTime, constraints);

        constraints.gridx = 2;
        add(labelDuration, constraints);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelButtons.add(buttonPlay);
        panelButtons.add(buttonPause);

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(panelButtons, constraints);

        buttonPlay.addActionListener(this);
        buttonPause.addActionListener(this);

        pack();
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                stopPlaying();
                setVisible(false);
            }
        });
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof JButton) {
            JButton button = (JButton) source;
			/*if (button == buttonOpen) {
				openFile();
			} else*/
            if (button == buttonPlay) {
                if (!isPlaying) {
                    playBack();
                } else {
                    stopPlaying();
                }
            } else if (button == buttonPause) {
                if (!isPause) {
                    pausePlaying();
                } else {
                    resumePlaying();
                }
            }
        }
    }


    public void openFile(String path) {
        audioFilePath = path;
        if (isPlaying || isPause) {
            stopPlaying();
            while (player.getAudioClip().isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
        playBack();
    }

    private void playBack() {
        timer = new PlayingTimer(labelTimeCounter, sliderTime);
        timer.start();
        isPlaying = true;
        playbackThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    buttonPlay.setText("Stop");
                    buttonPlay.setEnabled(true);

                    buttonPause.setText("Pause");
                    buttonPause.setEnabled(true);

                    player.load(audioFilePath);
                    timer.setAudioClip(player.getAudioClip());
                    labelFileName.setText("Datei: " + audioFilePath);
                    sliderTime.setMaximum((int) player.getClipSecondLength());

                    labelDuration.setText(player.getClipLengthString());
                    player.play();

                    resetControls();
                } catch (UnsupportedAudioFileException ex) {
                    JOptionPane.showMessageDialog(AudioPlayerUI.this, "Format wird nicht unterstützt!", "Error", JOptionPane.ERROR_MESSAGE);
                    resetControls();
                    ex.printStackTrace();
                } catch (LineUnavailableException ex) {
                    JOptionPane.showMessageDialog(AudioPlayerUI.this, "Datei kann nicht abgespielt werden!", "Error", JOptionPane.ERROR_MESSAGE);
                    resetControls();
                    ex.printStackTrace();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(AudioPlayerUI.this, "I/O Fehler!", "Error", JOptionPane.ERROR_MESSAGE);
                    resetControls();
                    ex.printStackTrace();
                }

            }
        });

        playbackThread.start();
    }

    private void stopPlaying() {
        isPause = false;
        buttonPause.setText("Pause");
        buttonPause.setEnabled(false);
        timer.reset();
        timer.interrupt();
        player.stop();
        playbackThread.interrupt();
    }

    private void pausePlaying() {
        buttonPause.setText("Play");
        isPause = true;
        player.pause();
        timer.pauseTimer();
        playbackThread.interrupt();
    }

    private void resumePlaying() {
        buttonPause.setText("Pause");
        isPause = false;
        player.resume();
        timer.resumeTimer();
        playbackThread.interrupt();
    }

    private void resetControls() {
        timer.reset();
        timer.interrupt();

        buttonPlay.setText("Play");
        buttonPause.setEnabled(false);
        isPlaying = false;
    }
}