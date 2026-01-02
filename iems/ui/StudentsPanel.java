package iems.ui;

import iems.dao.*;
import iems.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;

public class StudentsPanel extends JPanel {
    private final User student;
    private final MaterialDAO materialDAO;
    private final AssignmentDAO assignmentDAO;
    private final GradeDAO gradeDAO;
    private final AttendanceDAO attendanceDAO;

    public StudentsPanel(User student) {
        this.student = student;

        try {
            this.materialDAO = new MaterialDAO();
            this.assignmentDAO = new AssignmentDAO();
            this.gradeDAO = new GradeDAO();
            this.attendanceDAO = new AttendanceDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }

        setLayout(new BorderLayout());
        setBackground(UIStyle.LIGHT_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.CARD_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = UIStyle.title("Student Dashboard - " + student.getFullName());
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Materials", createMaterialsPanel());
        tabs.addTab("Assignments", createAssignmentsPanel());
        tabs.addTab("Grades", createGradesPanel());
        tabs.addTab("Attendance", createAttendancePanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createMaterialsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Title", "Topic", "Level", "Format", "View" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            for (Material m : materialDAO.getAllMaterials()) {
                JButton viewBtn = new JButton("View");
                viewBtn.addActionListener(e -> {
                    try {
                        File file = new File(m.getFilePath());
                        if (file.exists()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "File not found: " + m.getFilePath(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                                "Unable to open file: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                model.addRow(new Object[] {
                        m.getTitle(), m.getTopic(), m.getLevel(), m.getFormat(), viewBtn
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading materials:\n" + e.getMessage());
        }
        return panel;
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Title", "Subject", "Due Date", "Marks" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            for (Assignment a : assignmentDAO.getAllAssignments()) {
                model.addRow(new Object[] {
                        a.getTitle(), a.getSubject(), a.getDueDate(), a.getTotalMarks()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading assignments:\n" + e.getMessage());
        }
        return panel;
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Assignment", "Marks" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            for (Grade g : gradeDAO.getByStudent(student.getId())) {
                model.addRow(new Object[] {
                        g.getAssignmentTitle(), g.getMarks()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading grades:\n" + e.getMessage());
        }
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Date", "Present" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            for (Attendance a : attendanceDAO.getByStudent(student.getId())) {
                model.addRow(new Object[] {
                        a.getDate(), a.isPresent() ? "Present" : "Absent"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading attendance:\n" + e.getMessage());
        }
        return panel;
    }
}