package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Streak;
import br.com.EcoPulse.exception.PersistenceException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StreakDao {
    private static final String COLUMNS =
            "id_streak, cd_usuario, tp_streak, nr_atual, nr_recorde, dt_ultima_atividade, dt_criacao, dt_atualizacao";

    public Streak create(Streak s) {
        String sql = """
                INSERT INTO T_SOUL_STREAK (
                    cd_usuario, tp_streak, nr_atual, nr_recorde, dt_ultima_atividade, dt_criacao, dt_atualizacao
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_STREAK"})) {
            ps.setLong(1, s.getUserId());
            ps.setString(2, s.getType());
            ps.setInt(3, s.getCurrentCount());
            ps.setInt(4, s.getHighestCount());
            JdbcUtils.setInstant(ps, 5, s.getLastActivityAt());
            JdbcUtils.setInstant(ps, 6, s.getCreatedAt());
            JdbcUtils.setInstant(ps, 7, s.getUpdatedAt());
            ps.executeUpdate();
            s.setId(JdbcUtils.extractGeneratedId(ps));
            return s;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar streak", e);
        }
    }

    public Optional<Streak> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_STREAK WHERE id_streak = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar streak", e);
        }
    }

    public Optional<Streak> findByUserAndType(Long userId, String type) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_STREAK WHERE cd_usuario = ? AND UPPER(tp_streak) = UPPER(?)")) {
            ps.setLong(1, userId);
            ps.setString(2, type.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar streak do usuário por tipo", e);
        }
    }

    public List<Streak> findAll() {
        List<Streak> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_STREAK ORDER BY id_streak");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar streaks", e);
        }
    }

    public List<Streak> findByUserId(Long userId) {
        List<Streak> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_STREAK WHERE cd_usuario = ? ORDER BY id_streak")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar streaks do usuário", e);
        }
    }

    public Streak update(Streak s) {
        String sql = """
                UPDATE T_SOUL_STREAK
                   SET nr_atual = ?, nr_recorde = ?, dt_ultima_atividade = ?, dt_atualizacao = ?
                 WHERE id_streak = ?
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getCurrentCount());
            ps.setInt(2, s.getHighestCount());
            JdbcUtils.setInstant(ps, 3, s.getLastActivityAt());
            JdbcUtils.setInstant(ps, 4, s.getUpdatedAt());
            ps.setLong(5, s.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Streak não encontrado: " + s.getId());
            return s;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar streak", e);
        }
    }

    private Streak map(ResultSet rs) throws SQLException {
        return new Streak(
                rs.getLong("id_streak"),
                rs.getLong("cd_usuario"),
                rs.getString("tp_streak"),
                rs.getInt("nr_atual"),
                rs.getInt("nr_recorde"),
                JdbcUtils.getInstant(rs, "dt_ultima_atividade"),
                JdbcUtils.getInstant(rs, "dt_criacao"),
                JdbcUtils.getInstant(rs, "dt_atualizacao"));
    }
}
