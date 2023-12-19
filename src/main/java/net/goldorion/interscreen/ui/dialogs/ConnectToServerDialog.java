package net.goldorion.interscreen.ui.dialogs;

import net.goldorion.interscreen.network.Client;
import net.goldorion.interscreen.ui.InterScreenApp;
import net.goldorion.interscreen.utils.PanelUtils;

import javax.swing.*;
import java.awt.*;

public class ConnectToServerDialog extends JDialog {

    public ConnectToServerDialog(InterScreenApp parent) {
        super(parent, "Connect to local sharing screen sharing", true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel properties = new JPanel(new GridLayout(2, 2, 5, 5));
        properties.setOpaque(false);

        JSpinner portNumber = new JSpinner(new SpinnerNumberModel(12173, 1, 65535, 1));
        properties.add(new JLabel("Port number:"));
        properties.add(portNumber);

        JTextField ipAddress = new JTextField(15);
        properties.add(new JLabel("<html>Local IP address: <br><small>The local IP of the owner's device"));
        properties.add(ipAddress);

        JButton connect = new JButton("Connect");
        connect.addActionListener(e -> {
            parent.setClient(new Client(parent, (int) portNumber.getValue(), ipAddress.getText(), true));
            dispose();
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        JPanel panel = PanelUtils.centerAndSouth(properties, PanelUtils.join(connect, cancel));

        setContentPane(PanelUtils.squeezeInCenter(panel));
        pack();
        setLocationRelativeTo(this);
        setVisible(true);
    }
}
