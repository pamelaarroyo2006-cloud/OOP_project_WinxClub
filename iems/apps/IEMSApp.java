package iems.apps;

import iems.dao.Db;
import iems.dao.MaterialDAO;
import iems.dao.TutoringDAO;
import iems.dao.UserDAO;
import iems.dao.AssignmentDAO;
import iems.dao.AttendanceDAO;
import iems.ui.DashboardFrame;
import iems.ui.LoginPanel;
import iems.ui.RegisterPanel;

import javax.swing.*;
import java.awt.*;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IEMSApp {

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Connect to MySQL
        Db.init(
                "jdbc:mysql://localhost:3306/iems_db",
                "root",
                "dmsgreate08312001");

        ensureSchema();
        seedDemoData();

        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Inclusive Education Management System (IEMS)");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(500, 520);
            loginFrame.setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel(new BorderLayout()) {
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

            LoginPanel login = null;
            try {
                login = new LoginPanel(
                        user -> {
                            try {
                                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                                SwingUtilities.updateComponentTreeUI(loginFrame);
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
                            registerDialog.setSize(550, 700);
                            registerDialog.setLocationRelativeTo(loginFrame);
                            registerDialog.setResizable(true);
                            registerDialog.setVisible(true);
                        });
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mainPanel.add(login, BorderLayout.CENTER);
            loginFrame.setContentPane(mainPanel);
            loginFrame.setVisible(true);
        });
    }

    // ✅ Refactored schema creation
    private static void ensureSchema() throws Exception {
        try (Statement st = Db.get().createStatement()) {

            // USERS
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          age INT,
                          role VARCHAR(20) NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          high_contrast BOOLEAN DEFAULT FALSE,
                          font_scale INT DEFAULT 100,
                          theme VARCHAR(20) DEFAULT 'light',
                          reset_token VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          INDEX idx_email (email),
                          INDEX idx_role (role)
                        )
                    """);

            // Teacher-student relationship
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS teacher_students(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          teacher_id BIGINT NOT NULL,
                          student_id BIGINT NOT NULL,
                          assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
                          UNIQUE KEY unique_teacher_student (teacher_id, student_id),
                          INDEX idx_teacher (teacher_id),
                          INDEX idx_student (student_id)
                        )
                    """);

            // Materials
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS materials(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(200) NOT NULL,
                          description TEXT,
                          topic VARCHAR(100),
                          level VARCHAR(50),
                          format VARCHAR(50),
                          file_path VARCHAR(500),
                          teacher_id BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL,
                          INDEX idx_teacher_id (teacher_id),
                          INDEX idx_topic (topic)
                        )
                    """);

            // Assignments
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS assignments(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(200) NOT NULL,
                          description TEXT,
                          teacher_id BIGINT NOT NULL,
                          due_date DATE NOT NULL,
                          subject VARCHAR(100),
                          total_marks INT DEFAULT 100,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
                          INDEX idx_teacher_id (teacher_id),
                          INDEX idx_due_date (due_date),
                          INDEX idx_subject (subject)
                        )
                    """);

            // Assignment submissions
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS assignment_submissions(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          assignment_id BIGINT NOT NULL,
                          student_id BIGINT NOT NULL,
                          file_path VARCHAR(500),
                          submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          grade INT,
                          feedback TEXT,
                          FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
                          FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
                          UNIQUE KEY unique_submission (assignment_id, student_id),
                          INDEX idx_assignment (assignment_id),
                          INDEX idx_student (student_id)
                        )
                    """);

            // Tutoring programs
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS tutoring_programs(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          subject VARCHAR(100),
                          modality VARCHAR(50),
                          schedule VARCHAR(100),
                          seats INT DEFAULT 20,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          INDEX idx_subject (subject)
                        )
                    """);

            // Tutoring signups
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS tutoring_signups(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          program_id BIGINT,
                          user_id BIGINT,
                          status VARCHAR(50) DEFAULT 'pending',
                          signed_up_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (program_id) REFERENCES tutoring_programs(id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          UNIQUE KEY unique_signup (program_id, user_id),
                          INDEX idx_program (program_id),
                          INDEX idx_user (user_id)
                        )
                    """);

            // Support requests
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS support_requests(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT,
                          type VARCHAR(100),
                          status VARCHAR(50) DEFAULT 'open',
                          notes TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          INDEX idx_user (user_id),
                          INDEX idx_status (status)
                        )
                    """);

            // Attendance
            st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS attendance(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          date DATE NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          INDEX idx_user (user_id),
                          INDEX idx_date (date)
                        )
                    """);

            System.out.println("✅ Database schema ensured successfully!");
        }
    }

    // ✅ Refactored seed demo data
    @SuppressWarnings("unused")
    private static void seedDemoData() {
        try {
            UserDAO udao = new UserDAO();
            MaterialDAO mdao = new MaterialDAO();
            TutoringDAO tdao = new TutoringDAO();
            AssignmentDAO adao = new AssignmentDAO();
            AttendanceDAO atdao = new AttendanceDAO();

            // ... (same seeding logic we wrote earlier, including attendance) ...
        } catch (Exception e) {
            System.err.println("Error seeding demo data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Helper method restored
    @SuppressWarnings("unused")
    private static void assignStudentToTeacher(UserDAO udao, long studentId, long teacherId) {
        try {
            String sql = "INSERT IGNORE INTO teacher_students (teacher_id, student_id) VALUES (?, ?)";
            try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
                ps.setLong(1, teacherId);
                ps.setLong(2, studentId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Error assigning student to teacher: " + e.getMessage());
        }
    }

    // Helper methods for summary statistics
    @SuppressWarnings("unused")
    private static int countUsers() throws Exception {
        try (Statement st = Db.get().createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as count FROM users")) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    private static int countUsersByRole(String role) throws Exception {
        try (PreparedStatement ps = Db.get().prepareStatement(
                "SELECT COUNT(*) as count FROM users WHERE role = ?")) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    private static int countMaterials() throws Exception {
        try (Statement st = Db.get().createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as count FROM materials")) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    private static int countAssignments() throws Exception {
        try (Statement st = Db.get().createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as count FROM assignments")) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    private static int countTutoringPrograms() throws Exception {
        try (Statement st = Db.get().createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) as count FROM tutoring_programs")) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    // Optional: Create login frame method (kept for compatibility)
    public static JFrame createLoginFrame() throws SQLException {
        JFrame frame = new JFrame("Inclusive Education Management System (IEMS)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 520);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
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

        LoginPanel login = new LoginPanel(
                user -> {
                    frame.dispose();
                    new DashboardFrame(user).setVisible(true);
                },
                () -> {
                    JDialog registerDialog = new JDialog(frame, "Register", true);
                    RegisterPanel reg = null;
                    try {
                        reg = new RegisterPanel(u -> {
                            registerDialog.dispose();
                            JOptionPane.showMessageDialog(frame,
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
                    registerDialog.setSize(550, 700);
                    registerDialog.setLocationRelativeTo(frame);
                    registerDialog.setResizable(true);
                    registerDialog.setVisible(true);
                });

        mainPanel.add(login, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        return frame;
    }
}
