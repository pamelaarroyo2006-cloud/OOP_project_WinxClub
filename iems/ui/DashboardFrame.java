package iems.ui;

import iems.model.User;
import iems.model.Role;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import javax.swing.border.EmptyBorder;

public class DashboardFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final User user;

    public DashboardFrame(User user) {
        super("IEMS Dashboard - " + user.getFullName());
        this.user = user;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 800);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmAndLogout();
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createSidebar());
        splitPane.setRightComponent(content);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(1);
        splitPane.setEnabled(false);

        content.setBackground(UIStyle.LIGHT_BG);
        add(splitPane, BorderLayout.CENTER);

        initializePanels();

        if (user.getRole() == Role.TEACHER) {
            cards.show(content, "teacher");
        } else {
            cards.show(content, "materials");
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UIStyle.DARK_BLUE);
        sidebar.setPreferredSize(new Dimension(280, 0));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.DARK_BLUE);
        headerPanel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JLabel appTitle = new JLabel("TEAMS");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        appTitle.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(user.getFullName());
        userLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(user.getRole().toString().toUpperCase());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(new Color(148, 163, 184));

        JPanel userInfoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        userInfoPanel.setBackground(UIStyle.DARK_BLUE);
        userInfoPanel.add(appTitle);
        userInfoPanel.add(userLabel);
        userInfoPanel.add(roleLabel);

        headerPanel.add(userInfoPanel, BorderLayout.CENTER);
        sidebar.add(headerPanel, BorderLayout.NORTH);

        // Navigation with icons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UIStyle.DARK_BLUE);
        navPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        if (user.getRole() == Role.TEACHER) {
            JButton dashboardBtn = UIStyle.sidebarButton("Dashboard", true);
            dashboardBtn.addActionListener(e -> cards.show(content, "teacher"));
            navPanel.add(dashboardBtn);
        } else {
            JButton materialsBtn = UIStyle.sidebarButton("Learning Materials", true);
            materialsBtn.addActionListener(e -> cards.show(content, "materials"));
            navPanel.add(materialsBtn);
            navPanel.add(Box.createVerticalStrut(10));

            JButton tutoringBtn = UIStyle.sidebarButton("Tutoring Programs", false);
            tutoringBtn.addActionListener(e -> cards.show(content, "tutoring"));
            navPanel.add(tutoringBtn);
            navPanel.add(Box.createVerticalStrut(10));

            JButton supportBtn = UIStyle.sidebarButton("Educational Support", false);
            supportBtn.addActionListener(e -> cards.show(content, "support"));
            navPanel.add(supportBtn);
        }

        JScrollPane navScroll = new JScrollPane(navPanel);
        navScroll.setBorder(null);
        navScroll.getViewport().setBackground(UIStyle.DARK_BLUE);
        navScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebar.add(navScroll, BorderLayout.CENTER);

        // Bottom (Logout)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIStyle.DARK_BLUE);
        bottomPanel.setBorder(new EmptyBorder(0, 20, 30, 20));

        JButton logoutBtn = UIStyle.flatButton("Logout", UIStyle.WARNING_ORANGE);
        logoutBtn.addActionListener(e -> confirmAndLogout());
        bottomPanel.add(logoutBtn, BorderLayout.CENTER);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void initializePanels() {
        if (user.getRole() == Role.TEACHER) {
            content.add(new TeacherDashboardPanel(user), "teacher");
        } else {
            content.add(new MaterialsPanel(user), "materials");
            content.add(new TutoringPanel(user), "tutoring");
            content.add(new SupportPanel(user), "support");
        }
    }

    private void confirmAndLogout() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout, " + user.getFullName() + "?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            dispose();
            showLoginPanel();
        }
    }

    private void showLoginPanel() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame loginFrame = new JFrame("Inclusive Education Management System");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(500, 550);
            loginFrame.setLocationRelativeTo(null);

            LoginPanel login = null;
            try {
                login = new LoginPanel(
                        user -> {
                            try {
                                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            loginFrame.dispose();
                            new DashboardFrame(user).setVisible(true);
                        },
                        () -> {
                            JDialog registerDialog = new JDialog(loginFrame, "Register", true);
                            RegisterPanel reg = null;
                            try {
                                reg = new RegisterPanel(u -> {
                                    registerDialog.dispose();
                                    JOptionPane.showMessageDialog(loginFrame,
                                            "Account created. You can now log in.",
                                            "Registration Successful",
                                            JOptionPane.INFORMATION_MESSAGE);
                                });
                            } catch (SQLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            registerDialog.setContentPane(reg);
                            registerDialog.pack();
                            registerDialog.setSize(600, 700);
                            registerDialog.setLocationRelativeTo(loginFrame);
                            registerDialog.setVisible(true);
                        });
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            loginFrame.setContentPane(login);
            loginFrame.setVisible(true);
        });
    }
}