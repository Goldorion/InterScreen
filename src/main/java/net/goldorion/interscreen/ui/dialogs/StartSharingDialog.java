package net.goldorion.interscreen.ui.dialogs;

import net.goldorion.interscreen.network.Client;
import net.goldorion.interscreen.network.Server;
import net.goldorion.interscreen.ui.InterScreenApp;
import net.goldorion.interscreen.utils.PanelUtils;

import javax.swing.*;
import java.awt.*;

public class StartSharingDialog extends JDialog {

    public StartSharingDialog(InterScreenApp parent) {
        super(parent, "Start local screen sharing", true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel properties = new JPanel(new GridLayout(2, 2, 5, 5));
        properties.setOpaque(false);

        JSpinner portNumber = new JSpinner(new SpinnerNumberModel(12173, 1, 65535, 1));
        properties.add(new JLabel("Port number:"));
        properties.add(portNumber);

        JTextField ipAddress = new JTextField(15);
        properties.add(new JLabel("<html>Local IP address: <br><small>The device's local IP"));
        properties.add(ipAddress);

        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            parent.setServer(new Server((int) portNumber.getValue()));
            parent.setClient(new Client(parent, (int) portNumber.getValue(), ipAddress.getText(), true));
            dispose();
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        JPanel panel = PanelUtils.centerAndSouth(properties, PanelUtils.join(start, cancel));

        setContentPane(PanelUtils.squeezeInCenter(panel));
        pack();
        setLocationRelativeTo(this);
        setVisible(true);
    }
}
