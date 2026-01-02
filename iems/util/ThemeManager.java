package iems.util;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    public static void apply(JFrame frame, String theme, boolean highContrast) {
        Color bg = highContrast ? Color.BLACK : "dark".equals(theme) ? new Color(40, 40, 40) : Color.WHITE;
        Color fg = highContrast ? Color.WHITE : "dark".equals(theme) ? Color.WHITE : Color.BLACK;

        UIManager.put("Panel.background", bg);
        UIManager.put("Panel.foreground", fg);
        UIManager.put("Label.foreground", fg);
        UIManager.put("Button.foreground", fg);
        UIManager.put("TextField.background",
                highContrast ? Color.DARK_GRAY : "dark".equals(theme) ? new Color(55, 55, 55) : Color.WHITE);
        UIManager.put("TextField.foreground", fg);

        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }
}