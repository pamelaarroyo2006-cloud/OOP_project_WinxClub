package iems.dao;

import iems.model.TutoringProgram;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TutoringDAO {
    public List<TutoringProgram> all() throws SQLException {
        try (Statement st = Db.get().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM tutoring_programs");
            List<TutoringProgram> list = new ArrayList<>();
            while (rs.next())
                list.add(map(rs));
            return list;
        }
    }

    public void create(TutoringProgram t) throws SQLException {
        String sql = "INSERT INTO tutoring_programs(subject,modality,schedule,seats) VALUES(?,?,?,?)";
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setString(1, t.getSubject());
            ps.setString(2, t.getModality());
            ps.setString(3, t.getSchedule());
            ps.setInt(4, t.getSeatsAvailable());
            ps.executeUpdate();
        }
    }

    public Optional<TutoringProgram> findById(long id) throws SQLException {
        String sql = "SELECT * FROM tutoring_programs WHERE id=?";
        try (PreparedStatement ps = Db.get().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(map(rs));
        }
        return Optional.empty();
    }

    public void updateSeats(long id, int newSeats) throws SQLException {
        try (PreparedStatement ps = Db.get().prepareStatement("UPDATE tutoring_programs SET seats=? WHERE id=?")) {
            ps.setInt(1, newSeats);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private TutoringProgram map(ResultSet rs) throws SQLException {
        TutoringProgram t = new TutoringProgram();
        t.setId(rs.getLong("id"));
        t.setSubject(rs.getString("subject"));
        t.setModality(rs.getString("modality"));
        t.setSchedule(rs.getString("schedule"));
        t.setSeatsAvailable(rs.getInt("seats"));
        return t;
    }
}