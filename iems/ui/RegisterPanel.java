package iems.ui;

import iems.dao.UserDAO;
import iems.model.Role;
import iems.model.User;
import iems.util.SecurityUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterPanel extends JPanel {
    private final java.util.function.Consumer<User> onRegistered;
    private JTextField nameField;
    private JTextField emailField;
    private JSpinner ageSpinner;
    private JComboBox<Role> roleCombo;
    private JPasswordField passwordField;
    private JCheckBox highContrastCheck;
    private JSlider fontScaleSlider;
    private JComboBox<String> themeCombo;

    private final UserDAO userDAO;

    public RegisterPanel(java.util.function.Consumer<User> onRegistered) throws SQLException {
        this.onRegistered = onRegistered;
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel gradientBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(58, 123, 213), 0, getHeight(),
                        new Color(0, 210, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientBg.setLayout(new GridBagLayout());
        add(gradientBg, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 235));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setPreferredSize(new Dimension(420, 600));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;

        JLabel header = new JLabel("Create Your Account Here");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(header, g);

        nameField = styledTextField();
        emailField = styledTextField();
        ageSpinner = new JSpinner(new SpinnerNumberModel(18, 5, 120, 1));
        roleCombo = new JComboBox<>(Role.values());
        passwordField = styledPasswordField();

        g.gridwidth = 1;

        g.gridy++;
        card.add(new JLabel("Full Name"), g);
        g.gridx = 1;
        card.add(nameField, g);

        g.gridx = 0;
        g.gridy++;
        card.add(new JLabel("Email"), g);
        g.gridx = 1;
        card.add(emailField, g);

        g.gridx = 0;
        g.gridy++;
        card.add(new JLabel("Age"), g);
        g.gridx = 1;
        card.add(ageSpinner, g);

        g.gridx = 0;
        g.gridy++;
        card.add(new JLabel("Role"), g);
        g.gridx = 1;
        card.add(roleCombo, g);

        g.gridx = 0;
        g.gridy++;
        card.add(new JLabel("Password"), g);
        g.gridx = 1;
        card.add(passwordField, g);

        g.gridx = 0;
        g.gridy++;
        g.gridwidth = 2;
        highContrastCheck = new JCheckBox("Enable high contrast mode");
        highContrastCheck.setOpaque(false);
        card.add(highContrastCheck, g);

        g.gridy++;
        card.add(new JLabel("Font Size:"), g);
        g.gridy++;
        fontScaleSlider = new JSlider(100, 160, 120);
        card.add(fontScaleSlider, g);

        g.gridy++;
        card.add(new JLabel("Theme:"), g);
        g.gridy++;
        themeCombo = new JComboBox<>(new String[] { "Light Theme", "Dark Theme" });
        card.add(themeCombo, g);

        g.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        JButton createBtn = styledButton("Create Account", new Color(58, 123, 213));
        JButton cancelBtn = styledButton("Cancel", new Color(200, 200, 200));
        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        card.add(buttonPanel, g);

        g.gridy++;
        JButton loginLink = ghostButton("Already have an account? Log in");
        card.add(loginLink, g);

        gradientBg.add(card);

        createBtn.addActionListener(e -> registerUser());
        cancelBtn.addActionListener(e -> closeDialog());
        loginLink.addActionListener(e -> closeDialog());
    }

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
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
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

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleCombo.getSelectedItem();

        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setAge((int) ageSpinner.getValue());
        user.setRole(selectedRole);
        user.setPasswordHash(SecurityUtil.hashPassword(password));
        user.setHighContrast(highContrastCheck.isSelected());
        user.setPreferredFontScale(fontScaleSlider.getValue());
        user.setTheme(themeCombo.getSelectedItem().toString().toLowerCase().replace(" theme", ""));

        try {
            userDAO.create(user);
            User savedUser = userDAO.findByEmail(email); // ✅ returns User or null
            if (savedUser != null) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!\nYou can now log in.",
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                onRegistered.accept(savedUser);
                closeDialog();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null)
            window.dispose();
    }
}