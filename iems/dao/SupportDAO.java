package iems.dao;

import iems.model.SupportRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportDAO {
    public void create(SupportRequest r) throws SQLException {
        String sql = "INSERT INTO support_requests(user_id,type,status,notes) VALUES(?,?,?,?)";
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setLong(1, r.getUserId());
            ps.setString(2, r.getType());
            ps.setString(3, r.getStatus());
            ps.setString(4, r.getNotes());
            ps.executeUpdate();
        }
    }

    public List<SupportRequest> forUser(long userId) throws SQLException {
        String sql = "SELECT * FROM support_requests WHERE user_id=?";
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<SupportRequest> list = new ArrayList<>();
            while (rs.next())
                list.add(map(rs));
            return list;
        }
    }

    public List<SupportRequest> all() throws SQLException {
        try (Statement st = Db.get().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM support_requests");
            List<SupportRequest> list = new ArrayList<>();
            while (rs.next())
                list.add(map(rs));
            return list;
        }
    }

    public void updateStatus(long id, String status) throws SQLException {
        try (PreparedStatement ps = Db.get().prepareStatement("UPDATE support_requests SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private SupportRequest map(ResultSet rs) throws SQLException {
        SupportRequest r = new SupportRequest();
        r.setId(rs.getLong("id"));
        r.setUserId(rs.getLong("user_id"));
        r.setType(rs.getString("type"));
        r.setStatus(rs.getString("status"));
        r.setNotes(rs.getString("notes"));
        return r;
    }
}