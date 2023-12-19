package net.goldorion.interscreen.ui;

import net.goldorion.interscreen.network.Client;
import net.goldorion.interscreen.network.Server;
import net.goldorion.interscreen.network.actions.IAction;
import net.goldorion.interscreen.network.actions.KeyBoardAction;
import net.goldorion.interscreen.network.actions.MouseAction;
import net.goldorion.interscreen.ui.dialogs.ConnectToServerDialog;
import net.goldorion.interscreen.ui.dialogs.StartSharingDialog;
import net.goldorion.interscreen.utils.PanelUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class InterScreenApp extends JFrame {

    private Server server;
    private Client client;

    private final JScreenPanel screenPanel;

    public InterScreenApp() {
        setTitle("InterScreen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        screenPanel = new JScreenPanel();
        addListenersToScreenPanel();
        panel.add("Center", screenPanel);

        JButton serverSharing = new JButton("Start sharing");
        JButton connectToServer = new JButton("Connect to sharing");
        serverSharing.addActionListener(e -> {
            if (serverSharing.getText().equalsIgnoreCase("Start sharing")) {
                new StartSharingDialog(this);
                connectToServer.setEnabled(false);
                serverSharing.setText("Stop sharing");
            } else if (serverSharing.getText().equalsIgnoreCase("Stop sharing")) {
                server.stopServer();
                connectToServer.setEnabled(true);
                serverSharing.setText("Start sharing");
            }
        });
        connectToServer.addActionListener(e -> {
            if (connectToServer.getText().equalsIgnoreCase("Connect to sharing")) {
                new ConnectToServerDialog(this);
                serverSharing.setEnabled(false);
                connectToServer.setText("Leave sharing");
            } else if (connectToServer.getText().equalsIgnoreCase("Leave sharing")) {
                client.disconnect();
                serverSharing.setEnabled(true);
                connectToServer.setText("Connect sharing");
            }
        });
        panel.add("South", PanelUtils.join(serverSharing, connectToServer));

        setContentPane(panel);
        setVisible(true);
    }

    public void receiveNewImage(BufferedImage image) {
        screenPanel.setImage(image);
    }

    private void sendAction(IAction action) {
        if (client != null && !client.isOwner() && client.isConnected())
            client.sendActionToServer(action);
    }

    private void addListenersToScreenPanel() {
        screenPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sendAction(new MouseAction(e.getPoint(), e.getButton(), e.getModifiersEx(), 0, true, false));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                sendAction(new MouseAction(e.getPoint(), e.getButton(), e.getModifiersEx(), 0, false, true));
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                sendAction(new MouseAction(e.getPoint(), e.getButton(), e.getModifiersEx(), e.getScrollAmount(), false, false));
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                sendAction(new MouseAction(e.getPoint(), e.getButton(), e.getModifiersEx(), 0, false, false));
            }
        });
        screenPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                sendAction(new KeyBoardAction(e.getKeyCode(), true, false));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                sendAction(new KeyBoardAction(e.getKeyCode(), false, true));
            }
        });
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
