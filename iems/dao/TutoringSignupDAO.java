package iems.dao;

import iems.model.TutoringSignup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutoringSignupDAO {
    public List<TutoringSignup> forUser(long userId) throws SQLException {
        String sql = "SELECT * FROM tutoring_signups WHERE user_id=?";
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<TutoringSignup> list = new ArrayList<>();
            while (rs.next())
                list.add(map(rs));
            return list;
        }
    }

    public void create(long programId, long userId, String status) throws SQLException {
        String sql = "INSERT INTO tutoring_signups(program_id,user_id,status) VALUES(?,?,?)";
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setLong(1, programId);
            ps.setLong(2, userId);
            ps.setString(3, status);
            ps.executeUpdate();
        }
    }

    private TutoringSignup map(ResultSet rs) throws SQLException {
        TutoringSignup s = new TutoringSignup();
        s.setId(rs.getLong("id"));
        s.setProgramId(rs.getLong("program_id"));
        s.setUserId(rs.getLong("user_id"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}