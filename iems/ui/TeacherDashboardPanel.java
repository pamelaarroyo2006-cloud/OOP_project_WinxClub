package iems.ui;

import iems.dao.*;
import iems.model.User;
import iems.model.Material;
import iems.model.Assignment;
import iems.model.Attendance;
import iems.model.Grade;
import iems.model.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TeacherDashboardPanel extends JPanel {
    private final User teacher;
    private final UserDAO userDAO;
    private final MaterialDAO materialDAO;
    private final AssignmentDAO assignmentDAO;
    private final GradeDAO gradeDAO;
    private final AttendanceDAO attendanceDAO;

    private JTable materialsTable, assignmentsTable, studentsTable;
    private DefaultTableModel materialsTableModel, assignmentsTableModel, studentsTableModel;

    public TeacherDashboardPanel(User teacher) {
        this.teacher = teacher;
        try {
            this.userDAO = new UserDAO();
            this.materialDAO = new MaterialDAO();
            this.assignmentDAO = new AssignmentDAO();
            this.gradeDAO = new GradeDAO();
            this.attendanceDAO = new AttendanceDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to the database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }

        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Materials", createMaterialsPanel());
        tabs.addTab("Assignments", createAssignmentsPanel());
        tabs.addTab("Students", createStudentsPanel());
        add(tabs, BorderLayout.CENTER);
    }

    // ===== Helper to style buttons =====
    private JButton styledButton(String text, Color bg, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(160, 40));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });

        b.addActionListener(e -> action.run());
        return b;
    }

    // ===== Materials Tab =====
    private JPanel createMaterialsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        materialsTableModel = new DefaultTableModel(
                new Object[] { "ID", "Title", "Topic", "Level", "Format", "File Path" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        materialsTable = new JTable(materialsTableModel);
        panel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);

        try {
            for (Material m : materialDAO.all()) {
                materialsTableModel.addRow(new Object[] {
                        m.getId(), m.getTitle(), m.getTopic(),
                        m.getLevel(), m.getFormat(), m.getFilePath()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading materials:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(styledButton("Upload Material", new Color(58, 123, 213), () -> uploadMaterial()));
        toolbar.add(styledButton("Edit Material", new Color(34, 139, 34), () -> editMaterial()));
        toolbar.add(styledButton("Delete Material", new Color(220, 20, 60), () -> deleteMaterial()));
        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private void uploadMaterial() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        String filePath = chooser.getSelectedFile().getAbsolutePath();

        JTextField titleField = new JTextField();
        JTextField topicField = new JTextField();
        JTextField levelField = new JTextField();
        JTextField formatField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Topic:"));
        form.add(topicField);
        form.add(new JLabel("Level:"));
        form.add(levelField);
        form.add(new JLabel("Format:"));
        form.add(formatField);

        if (JOptionPane.showConfirmDialog(this, form,
                "Enter Material Details", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        Material m = new Material();
        m.setTitle(titleField.getText().trim());
        m.setTopic(topicField.getText().trim());
        m.setLevel(levelField.getText().trim());
        m.setFormat(formatField.getText().trim());
        m.setFilePath(filePath);
        m.setTeacherId(teacher.getId());
        m.setCreatedAt(LocalDateTime.now());

        try {
            if (materialDAO.create(m)) {
                materialsTableModel.addRow(new Object[] {
                        m.getId(), m.getTitle(), m.getTopic(),
                        m.getLevel(), m.getFormat(), m.getFilePath()
                });
                JOptionPane.showMessageDialog(this, "Material uploaded successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editMaterial() {
        int row = materialsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a material first.");
            return;
        }
        long id = ((Number) materialsTableModel.getValueAt(row, 0)).longValue();

        JTextField titleField = new JTextField((String) materialsTableModel.getValueAt(row, 1));
        JTextField topicField = new JTextField((String) materialsTableModel.getValueAt(row, 2));
        JTextField levelField = new JTextField((String) materialsTableModel.getValueAt(row, 3));
        JTextField formatField = new JTextField((String) materialsTableModel.getValueAt(row, 4));
        JTextField filePathField = new JTextField((String) materialsTableModel.getValueAt(row, 5));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Topic:"));
        form.add(topicField);
        form.add(new JLabel("Level:"));
        form.add(levelField);
        form.add(new JLabel("Format:"));
        form.add(formatField);
        form.add(new JLabel("File Path:"));
        form.add(filePathField);

        if (JOptionPane.showConfirmDialog(this, form,
                "Edit Material", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        Material m = new Material();
        m.setId(id);
        m.setTitle(titleField.getText().trim());
        m.setTopic(topicField.getText().trim());
        m.setLevel(levelField.getText().trim());
        m.setFormat(formatField.getText().trim());
        m.setFilePath(filePathField.getText().trim());
        m.setTeacherId(teacher.getId());

        try {
            if (materialDAO.update(m)) {
                materialsTableModel.setValueAt(m.getTitle(), row, 1);
                materialsTableModel.setValueAt(m.getTopic(), row, 2);
                materialsTableModel.setValueAt(m.getLevel(), row, 3);
                materialsTableModel.setValueAt(m.getFormat(), row, 4);
                materialsTableModel.setValueAt(m.getFilePath(), row, 5);
                JOptionPane.showMessageDialog(this, "Material updated successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMaterial() {
        int row = materialsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a material first.");
            return;
        }
        long id = ((Number) materialsTableModel.getValueAt(row, 0)).longValue();

        if (JOptionPane.showConfirmDialog(this,
                "Delete this material?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        try {
            if (materialDAO.delete(id, teacher.getId())) {
                materialsTableModel.removeRow(row);
                JOptionPane.showMessageDialog(this, "Material deleted.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Assignments Tab =====
    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        assignmentsTableModel = new DefaultTableModel(
                new Object[] { "ID", "Title", "Subject", "Due Date", "Marks" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        assignmentsTable = new JTable(assignmentsTableModel);
        panel.add(new JScrollPane(assignmentsTable), BorderLayout.CENTER);

        try {
            for (Assignment a : assignmentDAO.getAssignmentsByTeacher(teacher.getId())) {
                assignmentsTableModel.addRow(new Object[] {
                        a.getId(), a.getTitle(), a.getSubject(),
                        a.getDueDate().toString(), a.getTotalMarks()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading assignments:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(styledButton("Add Assignment", new Color(58, 123, 213), () -> addAssignment()));
        toolbar.add(styledButton("Edit Assignment", new Color(34, 139, 34), () -> editAssignment()));
        toolbar.add(styledButton("Delete Assignment", new Color(220, 20, 60), () -> deleteAssignment()));
        toolbar.add(styledButton("Give Grades", new Color(255, 140, 0), () -> giveGrades()));
        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private void addAssignment() {
        JTextField titleField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextField dueDateField = new JTextField("YYYY-MM-DD");
        JTextField marksField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Subject:"));
        form.add(subjectField);
        form.add(new JLabel("Due Date:"));
        form.add(dueDateField);
        form.add(new JLabel("Total Marks:"));
        form.add(marksField);

        if (JOptionPane.showConfirmDialog(this, form,
                "Add Assignment", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        Assignment a = new Assignment();
        a.setTitle(titleField.getText().trim());
        a.setSubject(subjectField.getText().trim());
        try {
            LocalDate dueDate = LocalDate.parse(dueDateField.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            a.setDueDate(dueDate);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            a.setTotalMarks(Integer.parseInt(marksField.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Marks must be a number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        a.setTeacherId(teacher.getId());

        try {
            if (assignmentDAO.create(a)) {
                assignmentsTableModel.addRow(new Object[] {
                        a.getId(), a.getTitle(), a.getSubject(),
                        a.getDueDate().toString(), a.getTotalMarks()
                });
                JOptionPane.showMessageDialog(this, "Assignment added successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAssignment() {
        int row = assignmentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an assignment first.");
            return;
        }

        long id = ((Number) assignmentsTableModel.getValueAt(row, 0)).longValue();
        String currentTitle = (String) assignmentsTableModel.getValueAt(row, 1);
        String currentSubject = (String) assignmentsTableModel.getValueAt(row, 2);
        String currentDueDate = (String) assignmentsTableModel.getValueAt(row, 3);
        int currentMarks = ((Number) assignmentsTableModel.getValueAt(row, 4)).intValue();

        JTextField titleField = new JTextField(currentTitle);
        JTextField subjectField = new JTextField(currentSubject);
        JTextField dueDateField = new JTextField(currentDueDate);
        JTextField marksField = new JTextField(String.valueOf(currentMarks));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Subject:"));
        form.add(subjectField);
        form.add(new JLabel("Due Date:"));
        form.add(dueDateField);
        form.add(new JLabel("Total Marks:"));
        form.add(marksField);

        if (JOptionPane.showConfirmDialog(this, form,
                "Edit Assignment", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        Assignment a = new Assignment();
        a.setId(id);
        a.setTitle(titleField.getText().trim());
        a.setSubject(subjectField.getText().trim());
        try {
            LocalDate dueDate = LocalDate.parse(dueDateField.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            a.setDueDate(dueDate);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            a.setTotalMarks(Integer.parseInt(marksField.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Marks must be a number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        a.setTeacherId(teacher.getId());

        try {
            if (assignmentDAO.update(a)) {
                assignmentsTableModel.setValueAt(a.getTitle(), row, 1);
                assignmentsTableModel.setValueAt(a.getSubject(), row, 2);
                assignmentsTableModel.setValueAt(a.getDueDate().toString(), row, 3);
                assignmentsTableModel.setValueAt(a.getTotalMarks(), row, 4);
                JOptionPane.showMessageDialog(this, "Assignment updated successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAssignment() {
        int row = assignmentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an assignment first.");
            return;
        }
        long id = ((Number) assignmentsTableModel.getValueAt(row, 0)).longValue();

        if (JOptionPane.showConfirmDialog(this,
                "Delete this assignment?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        try {
            if (assignmentDAO.delete(id, teacher.getId())) {
                assignmentsTableModel.removeRow(row);
                JOptionPane.showMessageDialog(this, "Assignment deleted.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void giveGrades() {
        int row = assignmentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an assignment first.");
            return;
        }

        long assignmentId = ((Number) assignmentsTableModel.getValueAt(row, 0)).longValue();
        String assignmentTitle = (String) assignmentsTableModel.getValueAt(row, 1);

        JTextField studentIdField = new JTextField();
        JTextField gradeField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Assignment:"));
        form.add(new JLabel(assignmentTitle));
        form.add(new JLabel("Student ID:"));
        form.add(studentIdField);
        form.add(new JLabel("Marks:"));
        form.add(gradeField);

        if (JOptionPane.showConfirmDialog(this, form,
                "Give Grades", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        try {
            long studentId = Long.parseLong(studentIdField.getText().trim());
            int score = Integer.parseInt(gradeField.getText().trim());

            Grade g = new Grade();
            g.setAssignmentId(assignmentId);
            g.setStudentId(studentId);
            g.setTeacherId(teacher.getId());
            g.setMarks(score);

            if (gradeDAO.create(g)) {
                JOptionPane.showMessageDialog(this, "Grade recorded successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "IDs and marks must be numbers.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Students Tab =====
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        studentsTableModel = new DefaultTableModel(
                new Object[] { "ID", "Name", "Email", "Role" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        studentsTable = new JTable(studentsTableModel);
        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        try {
            for (User u : userDAO.getAllUsers()) {
                studentsTableModel.addRow(new Object[] {
                        u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(styledButton("Add Student", new Color(58, 123, 213), () -> addStudent()));
        toolbar.add(styledButton("Export Student List", new Color(34, 139, 34), () -> exportStudentList()));
        toolbar.add(styledButton("Mark Attendance", new Color(255, 140, 0), () -> markAttendance()));
        toolbar.add(styledButton("Export Reports", new Color(128, 0, 128), () -> exportReports()));
        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private void addStudent() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField roleField = new JTextField("STUDENT"); // default

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Role:"));
        form.add(roleField);

        if (JOptionPane.showConfirmDialog(this, form,
                "Add Student", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        User u = new User();
        u.setFullName(nameField.getText().trim());
        u.setEmail(emailField.getText().trim());
        try {
            u.setRole(Role.valueOf(roleField.getText().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid role. Use STUDENT or TEACHER.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.create(u)) {
                studentsTableModel.addRow(new Object[] {
                        u.getId(), u.getFullName(), u.getEmail(), u.getRole().name()
                });
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportStudentList() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Student List");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new java.io.File(file.getAbsolutePath() + ".csv");
        }

        try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
            // header
            for (int col = 0; col < studentsTableModel.getColumnCount(); col++) {
                pw.print(studentsTableModel.getColumnName(col));
                if (col < studentsTableModel.getColumnCount() - 1)
                    pw.print(",");
            }
            pw.println();
            // rows
            for (int row = 0; row < studentsTableModel.getRowCount(); row++) {
                for (int col = 0; col < studentsTableModel.getColumnCount(); col++) {
                    Object value = studentsTableModel.getValueAt(row, col);
                    pw.print(value != null ? value.toString() : "");
                    if (col < studentsTableModel.getColumnCount() - 1)
                        pw.print(",");
                }
                pw.println();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting student list:\n" + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Student list exported to:\n" + file.getAbsolutePath());
    }

    private void markAttendance() {
        int row = studentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }

        long studentId = ((Number) studentsTableModel.getValueAt(row, 0)).longValue();
        String studentName = (String) studentsTableModel.getValueAt(row, 1);

        String[] options = { "Present", "Absent" };
        int choice = JOptionPane.showOptionDialog(this,
                "Mark attendance for " + studentName,
                "Attendance", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == -1)
            return;

        boolean isPresent = choice == 0;
        Attendance a = new Attendance();
        a.setStudentId(studentId);
        a.setTeacherId(teacher.getId());
        a.setDate(LocalDate.now());
        a.setPresent(isPresent);

        try {
            if (attendanceDAO.create(a)) {
                JOptionPane.showMessageDialog(this,
                        "Attendance marked: " + studentName + " - " + (isPresent ? "Present" : "Absent"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReports() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Reports");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new java.io.File(file.getAbsolutePath() + ".csv");
        }

        try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
            pw.println("Student ID,Name,Email,Attendance Date,Present,Assignment ID,Assignment Title,Marks");

            for (User u : userDAO.getAllUsers()) {
                long studentId = u.getId();
                var attendanceList = attendanceDAO.getByStudent(studentId);
                var gradeList = gradeDAO.getByStudent(studentId);

                int max = Math.max(attendanceList.size(), gradeList.size());
                if (max == 0) {
                    pw.printf("%d,%s,%s,,,,,\n", studentId, u.getFullName(), u.getEmail());
                } else {
                    for (int i = 0; i < max; i++) {
                        Attendance a = i < attendanceList.size() ? attendanceList.get(i) : null;
                        Grade g = i < gradeList.size() ? gradeList.get(i) : null;
                        pw.printf("%d,%s,%s,%s,%s,%s,%s,%s\n",
                                studentId,
                                u.getFullName(),
                                u.getEmail(),
                                a != null ? a.getDate().toString() : "",
                                a != null ? (a.isPresent() ? "Present" : "Absent") : "",
                                g != null ? g.getAssignmentId() : "",
                                g != null ? g.getAssignmentTitle() : "",
                                g != null ? g.getMarks() : "");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting reports:\n" + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Reports exported to:\n" + file.getAbsolutePath());
    }
}