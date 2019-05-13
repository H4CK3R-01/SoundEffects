package de.florian.Search;

import de.florian.MusicPlayer.AudioPlayerUI;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UI extends Thread {
    private final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private String url = "jdbc:mariadb://localhost/SoundEffects";
    private String user = "pi";
    private String pass = "Admin!123";

    private String path = "/media/florian/DATA/BBCSoundEffects/";

    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenuItem changeDirectory, changeDBurl, changeUser, changePass;
    private JFileChooser chooser;
    private PlaceholderTextField searchTextField;
    private JScrollPane tableScrollPane;
    private JTable table;

    private String[][] data;
    private String[] columnNames = {"Beschreibung", "Dauer", "Dateiname"};

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                table = null;
                Class.forName(JDBC_DRIVER);
                connection = DriverManager.getConnection(url, user, pass);
                statement = connection.createStatement();

                resultSet = statement.executeQuery("select * from Effects WHERE Beschreibung LIKE '%" + searchTextField.getText() + "%';");

                int rowSize = 0;
                try {
                    resultSet.last();
                    rowSize = resultSet.getRow();
                    resultSet.beforeFirst();
                } catch (Exception ex) {
                    System.err.println(ex);
                }

                data = new String[rowSize + 1][3];
                while (resultSet.next()) {
                    data[resultSet.getRow()][0] = resultSet.getString(2);
                    data[resultSet.getRow()][1] = resultSet.getString(3);
                    data[resultSet.getRow()][2] = resultSet.getString(1);
                }
                paintFrame();
            } catch (SQLException se) {
                System.err.println(se);
            } catch (Exception ex) {
                System.err.println(ex);
            } finally {
                try {
                    if (statement != null) {
                        connection.close();
                    }
                } catch (SQLException se) {
                    System.err.println(se);
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException se) {
                    System.err.println(se);
                }
            }
        }
    };

    public UI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void run() {
        mainFrame = new JFrame("SoundEffects - Search");

        menuBar = new JMenuBar();
        file = new JMenu("Datei");

        changeDirectory = new JMenuItem("Verzeichnis ändern");
        changeDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Verzeichnis der Soundeffekte auswählen");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getAbsolutePath() + "/";
                    System.out.println(path);
                }
            }
        });

        changeDBurl = new JMenuItem("Datenbank-URL ändern");
        changeDBurl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                url = JOptionPane.showInputDialog("URL der Datenbank ändern (Format: jdbc:Datenbanksystem://Server/Datenbankname) \nStandard ist: jdbc:mariadb://localhost/SoundEffects");
            }
        });

        changeUser = new JMenuItem("Benutzer ändern");
        changeUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                user = JOptionPane.showInputDialog("Benutzer der Datenbank ändern");
                System.out.println(user);
            }
        });

        changePass = new JMenuItem("Passwort ändern");
        changePass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JPasswordField passwordField = new JPasswordField();
                passwordField.setEchoChar('*');
                JOptionPane.showMessageDialog(null, passwordField, "Passwort des Datenbank-Benutzers ändern", JOptionPane.OK_OPTION);
                pass = String.valueOf(passwordField.getPassword());
                System.out.println(pass);
            }
        });

        file.add(changeDirectory);
        file.addSeparator();
        file.add(changeDBurl);
        file.add(changeUser);
        file.add(changePass);

        menuBar.add(file);

        mainFrame.setJMenuBar(menuBar);

        searchTextField = new PlaceholderTextField("");
        searchTextField.setPlaceholder("Suche ...");
        searchTextField.addActionListener(actionListener);
        mainFrame.add(searchTextField, BorderLayout.NORTH);

        table = new JTable(data, columnNames) {
            public boolean isCellEditable(int x, int y) {
                return false;
            }
        };

        tableScrollPane = new JScrollPane(table);
        mainFrame.add(tableScrollPane, BorderLayout.CENTER);

        mainFrame.setSize(new Dimension(1600, 900));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                setColumnWidth();
            }
        });

        data = new String[1][3];
        paintFrame();
    }

    private void paintFrame() {
        mainFrame.setVisible(false);
        try {
            mainFrame.remove(tableScrollPane);
        } catch (Exception e) {
            System.err.println(e);
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

                    AudioPlayerUI player = new AudioPlayerUI();
                    player.setVisible(true);
                    player.openFile(path + table.getValueAt(target.getSelectedRow(), 2).toString());
                }
            }
        });
        setColumnWidth();
        tableScrollPane = new JScrollPane(table);
        mainFrame.add(tableScrollPane, BorderLayout.CENTER);

        //mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    private void setColumnWidth() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn col1 = table.getColumnModel().getColumn(0);
        TableColumn col2 = table.getColumnModel().getColumn(1);
        TableColumn col3 = table.getColumnModel().getColumn(2);
        col1.setPreferredWidth((int) (mainFrame.getWidth() * 0.8));
        col2.setPreferredWidth((int) (mainFrame.getWidth() * 0.1));
        col3.setPreferredWidth(mainFrame.getWidth() - col1.getPreferredWidth() - col2.getPreferredWidth() - 2);
    }
}
