// Updated MaterialsPanel.java template
package iems.ui;

import iems.model.User;
import javax.swing.*;
import java.awt.*;

public class MaterialsPanel extends JPanel {
    public MaterialsPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel title = new JLabel("Learning Materials");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(30, 41, 59));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Add material cards or table here

        add(content, BorderLayout.CENTER);
    }
}