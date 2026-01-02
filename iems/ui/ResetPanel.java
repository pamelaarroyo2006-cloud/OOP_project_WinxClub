package iems.ui;

import iems.dao.UserDAO;
import iems.model.User;
import iems.util.SecurityUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class ResetPanel extends JPanel {
    public ResetPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyle.LIGHT_BG);

        // Header
        JPanel header = UIStyle.card();
        header.add(UIStyle.title("Reset Your Password"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel formCard = UIStyle.card();
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField tokenField = UIStyle.styledTextField(20);
        JPasswordField newPass = new JPasswordField(20);
        JButton resetBtn = UIStyle.primary("Reset Password");

        g.gridx = 0;
        g.gridy = 0;
        form.add(new JLabel("Reset token"), g);
        g.gridx = 1;
        form.add(tokenField, g);
        g.gridx = 0;
        g.gridy = 1;
        form.add(new JLabel("New password"), g);
        g.gridx = 1;
        form.add(newPass, g);
        g.gridx = 1;
        g.gridy = 2;
        form.add(resetBtn, g);

        formCard.add(form, BorderLayout.CENTER);
        add(formCard, BorderLayout.CENTER);

        // Logic
        resetBtn.addActionListener(e -> {
            String token = tokenField.getText().trim();
            String pw = new String(newPass.getPassword());

            if (token.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Token and new password are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Optional<User> u = new UserDAO().findByResetToken(token);
                if (u.isPresent()) {
                    new UserDAO().updatePassword(
                            u.get().getId(),
                            SecurityUtil.hashPassword(pw));
                    JOptionPane.showMessageDialog(this,
                            "Password updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid token.",
                            "Reset Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}