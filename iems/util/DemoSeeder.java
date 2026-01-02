package iems.util;

import iems.dao.*;
import iems.model.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class DemoSeeder {
    public static void seedDemoData(long teacherId, long studentId) throws SQLException {
        MaterialDAO materialDAO = new MaterialDAO();
        AssignmentDAO assignmentDAO = new AssignmentDAO();
        GradeDAO gradeDAO = new GradeDAO();
        AttendanceDAO attendanceDAO = new AttendanceDAO();

        // Materials
        Material m1 = new Material("Intro to Java", "Programming", "Beginner", "PDF", "/files/java_intro.pdf",
                teacherId);
        Material m2 = new Material("Database Basics", "SQL", "Intermediate", "DOCX", "/files/sql_basics.docx",
                teacherId);
        materialDAO.create(m1);
        materialDAO.create(m2);

        // Assignments
        Assignment a1 = new Assignment("Java Quiz", "Programming", LocalDate.now().plusDays(7), 100, teacherId);
        Assignment a2 = new Assignment("SQL Exercise", "Database", LocalDate.now().plusDays(10), 50, teacherId);
        assignmentDAO.create(a1);
        assignmentDAO.create(a2);

        // Grades
        Grade g1 = new Grade(studentId, a1.getId(), teacherId, 85);
        Grade g2 = new Grade(studentId, a2.getId(), teacherId, 45);
        gradeDAO.create(g1);
        gradeDAO.create(g2);

        // Attendance
        Attendance att1 = new Attendance(studentId, teacherId, LocalDate.now().minusDays(2), true);
        Attendance att2 = new Attendance(studentId, teacherId, LocalDate.now().minusDays(1), false);
        attendanceDAO.create(att1);
        attendanceDAO.create(att2);
    }
}