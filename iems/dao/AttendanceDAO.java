package iems.dao;

import iems.model.Attendance;
import java.sql.*;
import java.util.*;

public class AttendanceDAO {
    private final Connection conn;

    public AttendanceDAO() throws SQLException {
        this.conn = Db.get();
    }

    public boolean create(Attendance a) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, teacher_id, date, present) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getStudentId());
            ps.setLong(2, a.getTeacherId());
            ps.setDate(3, java.sql.Date.valueOf(a.getDate()));
            ps.setBoolean(4, a.isPresent());
            int affected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                a.setId(rs.getLong(1));
            return affected > 0;
        }
    }

    public List<Attendance> getByStudent(long studentId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    private Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getLong("id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setTeacherId(rs.getLong("teacher_id"));
        a.setDate(rs.getDate("date").toLocalDate());
        a.setPresent(rs.getBoolean("present"));
        return a;
    }
}