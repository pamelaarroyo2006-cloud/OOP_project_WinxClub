package iems.util;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class AccessibilityUtil {
    public static void scaleFonts(Component root, int percent) {
        float scale = percent / 100f;
        traverse(root, c -> {
            Font f = c.getFont();
            if (f != null)
                c.setFont(f.deriveFont(Math.max(12f, f.getSize2D() * scale)));
        });
    }

    private static void traverse(Component comp, Consumer<Component> fn) {
        fn.accept(comp);
        if (comp instanceof Container container) {
            for (Component child : container.getComponents())
                traverse(child, fn);
        }
    }

    public static void mnemonic(AbstractButton b, char ch) {
        b.setMnemonic(ch);
    }

    // Small helper for GridBag constraints reuse
    public static GridBagConstraints baseGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        return g;
    }

    public static GridBagConstraints gbc(GridBagConstraints g, int x, int y) {
        GridBagConstraints c = (GridBagConstraints) g.clone();
        c.gridx = x;
        c.gridy = y;
        return c;
    }
}