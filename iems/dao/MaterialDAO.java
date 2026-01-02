package iems.dao;

import iems.model.Material;
import java.sql.*;
import java.util.*;

public class MaterialDAO {
    private final Connection conn;

    public MaterialDAO() throws SQLException {
        this.conn = Db.get();
    }

    public boolean create(Material m) throws SQLException {
        String sql = "INSERT INTO materials (title, topic, level, format, file_path, teacher_id) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getTopic());
            ps.setString(3, m.getLevel());
            ps.setString(4, m.getFormat());
            ps.setString(5, m.getFilePath());
            ps.setLong(6, m.getTeacherId());
            int affected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                m.setId(rs.getLong(1));
            return affected > 0;
        }
    }

    public List<Material> all() throws SQLException {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM materials";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public boolean update(Material m) throws SQLException {
        String sql = "UPDATE materials SET title=?, topic=?, level=?, format=?, file_path=? WHERE id=? AND teacher_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getTopic());
            ps.setString(3, m.getLevel());
            ps.setString(4, m.getFormat());
            ps.setString(5, m.getFilePath());
            ps.setLong(6, m.getId());
            ps.setLong(7, m.getTeacherId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id, long teacherId) throws SQLException {
        String sql = "DELETE FROM materials WHERE id=? AND teacher_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, teacherId);
            return ps.executeUpdate() > 0;
        }
    }

    private Material map(ResultSet rs) throws SQLException {
        Material m = new Material();
        m.setId(rs.getLong("id"));
        m.setTitle(rs.getString("title"));
        m.setTopic(rs.getString("topic"));
        m.setLevel(rs.getString("level"));
        m.setFormat(rs.getString("format"));
        m.setFilePath(rs.getString("file_path"));
        m.setTeacherId(rs.getLong("teacher_id"));
        return m;
    }

    public List<Material> getAllMaterials() throws SQLException {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM materials";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Material m = new Material();
                m.setId(rs.getLong("id"));
                m.setTitle(rs.getString("title"));
                m.setTopic(rs.getString("topic"));
                m.setLevel(rs.getString("level"));
                m.setFormat(rs.getString("format"));
                m.setFilePath(rs.getString("file_path"));
                m.setTeacherId(rs.getLong("teacher_id"));
                m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(m);
            }
        }
        return list;
    }
}