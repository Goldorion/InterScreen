package net.goldorion.interscreen.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PanelUtils {

    public static JPanel centerAndEast(JComponent center, JComponent east) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("Center", center);
        panel.add("East", east);

        return panel;
    }

    public static JPanel centerAndSouth(JComponent center, JComponent south) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("Center", center);
        panel.add("South", south);

        return panel;
    }

    public static JPanel join(Component... components) {
        return join(FlowLayout.CENTER, components);
    }

    public static JPanel join(int align, Component... components) {
        JPanel panel = new JPanel(new FlowLayout(align));
        panel.setOpaque(false);
        Arrays.stream(components).forEach(panel::add);
        return panel;
    }

    public static JPanel squeezeInCenter(Component component) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.add(component, new GridBagConstraints());
        return p;
    }
}
