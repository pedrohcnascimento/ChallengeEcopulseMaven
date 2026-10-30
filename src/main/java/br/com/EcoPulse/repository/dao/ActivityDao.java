package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Activity;
import br.com.EcoPulse.exception.PersistenceException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ActivityDao {
    private static final String COLUMNS = "id_activity, cd_usuario, tp_atividade, dt_atividade, ds_metadata";

    public Activity create(Activity activity) {
        String sql = "INSERT INTO T_SOUL_ACTIVITY (cd_usuario, tp_atividade, dt_atividade, ds_metadata) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_ACTIVITY"})) {
            ps.setLong(1, activity.getUserId());
            ps.setString(2, activity.getType());
            JdbcUtils.setInstant(ps, 3, activity.getTimestamp());
            ps.setString(4, activity.getMetadata());
            ps.executeUpdate();
            activity.setId(JdbcUtils.extractGeneratedId(ps));
            return activity;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao registrar atividade", e);
        }
    }

    public Optional<Activity> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_ACTIVITY WHERE id_activity = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar atividade", e);
        }
    }

    public List<Activity> findAll() {
        List<Activity> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_ACTIVITY ORDER BY dt_atividade DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar atividades", e);
        }
    }

    public List<Activity> findByUserId(Long userId) {
        List<Activity> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_ACTIVITY WHERE cd_usuario = ? ORDER BY dt_atividade DESC")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar atividades do usuário", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_ACTIVITY WHERE id_activity = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir atividade", e);
        }
    }

    private Activity map(ResultSet rs) throws SQLException {
        return new Activity(
                rs.getLong("id_activity"),
                rs.getLong("cd_usuario"),
                rs.getString("tp_atividade"),
                JdbcUtils.getInstant(rs, "dt_atividade"),
                rs.getString("ds_metadata"));
    }
}
