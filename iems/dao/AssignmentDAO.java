package iems.dao;

import iems.model.Assignment;
import java.sql.*;
import java.util.*;

public class AssignmentDAO {
    private final Connection conn;

    public AssignmentDAO() throws SQLException {
        this.conn = Db.get();
    }

    public boolean create(Assignment a) throws SQLException {
        String sql = "INSERT INTO assignments (title, subject, due_date, total_marks, teacher_id) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getSubject());
            ps.setDate(3, java.sql.Date.valueOf(a.getDueDate()));
            ps.setInt(4, a.getTotalMarks());
            ps.setLong(5, a.getTeacherId());
            int affected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                a.setId(rs.getLong(1));
            return affected > 0;
        }
    }

    public boolean update(Assignment a) throws SQLException {
        String sql = "UPDATE assignments SET title=?, subject=?, due_date=?, total_marks=? WHERE id=? AND teacher_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getSubject());
            ps.setDate(3, java.sql.Date.valueOf(a.getDueDate()));
            ps.setInt(4, a.getTotalMarks());
            ps.setLong(5, a.getId());
            ps.setLong(6, a.getTeacherId());
            return ps.executeUpdate() > 0;
        }
    }

    // 🔹 Added delete method
    public boolean delete(long id, long teacherId) throws SQLException {
        String sql = "DELETE FROM assignments WHERE id=? AND teacher_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, teacherId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Assignment> getAssignmentsByTeacher(long teacherId) throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE teacher_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private Assignment map(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setId(rs.getLong("id"));
        a.setTitle(rs.getString("title"));
        a.setSubject(rs.getString("subject"));
        a.setDueDate(rs.getDate("due_date").toLocalDate());
        a.setTotalMarks(rs.getInt("total_marks"));
        a.setTeacherId(rs.getLong("teacher_id"));
        return a;
    }

    public List<Assignment> getAllAssignments() throws SQLException {
        List<Assignment> list = new ArrayList<>();
        String sql = "SELECT * FROM assignments";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Assignment a = new Assignment();
                a.setId(rs.getLong("id"));
                a.setTitle(rs.getString("title"));
                a.setSubject(rs.getString("subject"));
                a.setDueDate(rs.getDate("due_date").toLocalDate());
                a.setTotalMarks(rs.getInt("total_marks"));
                a.setTeacherId(rs.getLong("teacher_id"));
                list.add(a);
            }
        }
        return list;
    }
}