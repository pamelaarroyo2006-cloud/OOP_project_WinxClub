package iems.ui;

import iems.model.User;
import iems.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private final UserDAO userDAO;

    public LoginPanel(Consumer<User> onLogin, Runnable onRegister) throws SQLException {
        this.userDAO = new UserDAO();
        setLayout(new BorderLayout());
        setOpaque(false);

        // ===== Gradient Background Panel =====
        JPanel gradientBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(58, 123, 213),
                        0, getHeight(), new Color(0, 210, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientBg.setLayout(new GridBagLayout());
        add(gradientBg, BorderLayout.CENTER);

        // ===== Card Panel =====
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 235));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setPreferredSize(new Dimension(420, 460));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;

        JLabel title = new JLabel("Inclusive Education Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, g);

        // ===== Fields =====
        emailField = styledTextField();
        passwordField = styledPasswordField();

        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setOpaque(false);
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton loginBtn = styledButton("Login", new Color(58, 123, 213));
        JButton registerBtn = styledButton("Register", new Color(34, 139, 34));
        JButton forgotBtn = ghostButton("Forgot Password?");

        g.gridwidth = 1;

        g.gridy++;
        card.add(new JLabel("Email"), g);
        g.gridx = 1;
        card.add(emailField, g);

        g.gridx = 0;
        g.gridy++;
        card.add(new JLabel("Password"), g);
        g.gridx = 1;
        card.add(passwordField, g);

        g.gridy++;
        card.add(showPass, g);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 2;
        card.add(loginBtn, g);

        g.gridy++;
        card.add(registerBtn, g);

        g.gridy++;
        card.add(forgotBtn, g);

        gradientBg.add(card);

        // ===== Logic =====
        showPass.addActionListener(e -> passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : '•'));

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both email and password.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                User user = userDAO.authenticate(email, password);
                if (user != null) {
                    onLogin.accept(user);
                    emailField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid email or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> onRegister.run());

        forgotBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Password recovery feature coming soon.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE));
    }

    // ===== UI Helpers =====
    private JTextField styledTextField() {
        JTextField f = new JTextField(20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return f;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField(20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return f;
    }

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            public void paintComponent(Graphics g) {
                // Force the button to paint with our styling
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Draw text
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
            }
        };

        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        b.setContentAreaFilled(false); // We're painting our own background
        b.setOpaque(true); // Make sure it's opaque

        // Add hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });

        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(new Color(58, 123, 213));
        b.setBackground(new Color(0, 0, 0, 0));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        b.setContentAreaFilled(false);
        b.setOpaque(false);

        // Add hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setForeground(new Color(58, 123, 213).darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setForeground(new Color(58, 123, 213));
            }
        });

        return b;
    }
}
