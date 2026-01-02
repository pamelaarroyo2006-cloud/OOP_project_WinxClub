package iems.dao;

import iems.model.User;
import iems.model.Role;
import java.sql.*;
import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {
    private final Connection conn;

    public UserDAO() throws SQLException {
        this.conn = Db.get();
    }

    // Utility: hash a raw password with SHA-256
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Create: store SHA-256 hash
    public boolean create(User u) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, age, high_contrast, font_scale, theme, reset_token) "
                +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, sha256(u.getPasswordHash())); // hash before storing
            ps.setString(4, u.getRole().name());
            ps.setInt(5, u.getAge());
            ps.setBoolean(6, u.isHighContrast());
            ps.setInt(7, u.getPreferredFontScale());
            ps.setString(8, u.getTheme());
            ps.setString(9, u.getResetToken());
            int affected = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                u.setId(rs.getLong(1));
            return affected > 0;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        }
        return null;
    }

    // Authenticate: hash raw input and compare
    public User authenticate(String email, String rawPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE email=? AND password_hash=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, sha256(rawPassword)); // hash input before comparing
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        }
        return null;
    }

    public java.util.List<User> getAllUsers() throws SQLException {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public Optional<User> findByResetToken(String token) throws SQLException {
        String sql = "SELECT * FROM users WHERE reset_token=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    public boolean updatePassword(long userId, String newRawPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash=?, reset_token=NULL WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sha256(newRawPassword)); // hash before saving
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash")); // stored SHA-256 hash
        u.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
        u.setAge(rs.getInt("age"));
        u.setHighContrast(rs.getBoolean("high_contrast"));
        u.setPreferredFontScale(rs.getInt("font_scale"));
        u.setTheme(rs.getString("theme"));
        u.setResetToken(rs.getString("reset_token"));
        return u;
    }
}