package iems.dao;

import iems.model.Grade;
import java.sql.*;
import java.util.*;

public class GradeDAO {
    private final Connection conn;

    public GradeDAO() throws SQLException {
        this.conn = Db.get();
    }

    public boolean create(Grade g) throws SQLException {
        String sql = "INSERT INTO grades (user_id, teacher_id, assignment_id, marks, feedback) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, g.getStudentId());
            ps.setLong(2, g.getTeacherId());
            ps.setLong(3, g.getAssignmentId());
            ps.setInt(4, g.getMarks());
            ps.setString(5, g.getFeedback());
            int affected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                g.setId(rs.getLong(1));
            return affected > 0;
        }
    }

    public Grade findById(long id) throws SQLException {
        String sql = "SELECT * FROM grades WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        }
        return null;
    }

    public boolean update(Grade g) throws SQLException {
        String sql = "UPDATE grades SET marks=?, feedback=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, g.getMarks());
            ps.setString(2, g.getFeedback());
            ps.setLong(3, g.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Grade> getByStudent(long studentId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public List<Grade> getByTeacher(long teacherId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE teacher_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private Grade map(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getLong("id"));
        g.setStudentId(rs.getLong("user_id"));
        g.setTeacherId(rs.getLong("teacher_id"));
        g.setAssignmentId(rs.getLong("assignment_id"));
        g.setMarks(rs.getInt("marks"));
        g.setFeedback(rs.getString("feedback"));
        return g;
    }
}