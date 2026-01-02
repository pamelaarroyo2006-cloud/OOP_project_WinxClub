package iems.ui;

import iems.model.User;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(User user, DashboardFrame frame) {
        setLayout(new BorderLayout());
        setBackground(UIStyle.LIGHT_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.CARD_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = UIStyle.title("Settings");
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel card = UIStyle.card();
        JLabel content = new JLabel("Settings Panel - " + user.getFullName(), SwingConstants.CENTER);
        content.setFont(UIStyle.BODY);
        card.add(content, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }
}