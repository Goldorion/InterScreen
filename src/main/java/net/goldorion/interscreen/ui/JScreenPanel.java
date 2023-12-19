package net.goldorion.interscreen.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JScreenPanel extends JPanel {
    private BufferedImage image;

    public JScreenPanel() {
        setFocusable(true);
        setBorder(new LineBorder(Color.BLACK, 2));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null)
            g.drawImage(image, 0, 0, null);

        repaint();
    }
}
