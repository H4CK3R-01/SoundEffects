package de.florian.Search;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;

public class UI extends Thread {
    private final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private final String DB_URL = "jdbc:mariadb://localhost/SoundEffects";
    private final String USER = "pi";
    private final String PASS = "Admin!123";

    static JFrame mainFrame;
    static PlaceholderTextField searchTextField;
    static JScrollPane tableScrollPane;
    static JTable table;

    private String[][] data;
    private String[] columnNames = {"Beschreibung", "Dauer", "Dateiname"};


    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public UI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    public void run() {
        mainFrame = new JFrame("SoundEffects - Search");
        searchTextField = new PlaceholderTextField("");
        searchTextField.setPlaceholder("Suche ...");

        searchTextField.addActionListener(actionListener);
        mainFrame.add(searchTextField, BorderLayout.NORTH);

        table = new JTable(data, columnNames) {
            public boolean isCellEditable(int x, int y) {
                return false;
            }
        };
        table.setRowHeight(30);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("1");
                if (e.getClickCount() == 2) {
                    System.out.println("2");
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();

                    File sound = new File("/media/florian/DATA/BBCSoundEffects/" + table.getValueAt(row, 2).toString());

                    try
                    {
                        Clip clip = AudioSystem.getClip();
                        clip.open(AudioSystem.getAudioInputStream(sound));
                        clip.start();
                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace(System.out);
                    }
                }
            }
        });

        tableScrollPane = new JScrollPane(table);
        mainFrame.add(tableScrollPane, BorderLayout.CENTER);

        mainFrame.setSize(new Dimension(1600, 900));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        data = new String[1][3];
        paintFrame();
    }

    ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                table = null;
                Class.forName(JDBC_DRIVER);
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                statement = connection.createStatement();

                resultSet = statement.executeQuery("select * from Effects WHERE Beschreibung LIKE '%" + searchTextField.getText() + "%';");

                int rowSize = 0;
                try {
                    resultSet.last();
                    rowSize = resultSet.getRow();
                    resultSet.beforeFirst();
                } catch (Exception ex) {
                }

                data = new String[rowSize + 1][3];
                while (resultSet.next()) {
                    data[resultSet.getRow()][0] = resultSet.getString(2);
                    data[resultSet.getRow()][1] = resultSet.getString(3);
                    data[resultSet.getRow()][2] = resultSet.getString(1);
                }
                paintFrame();
            } catch (SQLException se) {
            } catch (Exception ex) {
            } finally {
                try {
                    if (statement != null) {
                        connection.close();
                    }
                } catch (SQLException se) {
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException se) {
                }
            }
        }
    };


    private void paintFrame() {
        mainFrame.setVisible(false);
        try {
            mainFrame.remove(tableScrollPane);
        } catch (Exception e) {
            e.printStackTrace();
        }

        table = new JTable(data, columnNames) {
            public boolean isCellEditable(int x, int y) {
                return false;
            }
        };

        table.setRowHeight(30);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();

                    File sound = new File("/media/florian/DATA/BBCSoundEffects/" + table.getValueAt(row, 2).toString());

                    new MusicPlayer(table.getValueAt(row, 0).toString(), sound);
                    //new AudioPlayer(sound);
                }
            }
        });
        tableScrollPane = new JScrollPane(table);
        mainFrame.add(tableScrollPane, BorderLayout.CENTER);

        //mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }
}
