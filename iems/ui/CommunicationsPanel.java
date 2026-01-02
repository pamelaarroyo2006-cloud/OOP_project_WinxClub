package iems.ui;

import iems.model.User;
import javax.swing.*;
import java.awt.*;

public class CommunicationsPanel extends JPanel {
    public CommunicationsPanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UIStyle.LIGHT_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.CARD_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = UIStyle.title("Communications");
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel card = UIStyle.card();
        JLabel content = new JLabel("Communications Panel - " + user.getFullName(), SwingConstants.CENTER);
        content.setFont(UIStyle.BODY);
        card.add(content, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }
}