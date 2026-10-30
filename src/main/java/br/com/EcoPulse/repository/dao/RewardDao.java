package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Reward;
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
public class RewardDao {
    private static final String COLUMNS = "id_reward, nm_reward, ds_reward, tp_reward, vl_reward, ds_detalhes";

    public Reward create(Reward reward) {
        String sql = "INSERT INTO T_SOUL_REWARD (nm_reward, ds_reward, tp_reward, vl_reward, ds_detalhes) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_REWARD"})) {
            ps.setString(1, reward.getName());
            ps.setString(2, reward.getDescription());
            ps.setString(3, reward.getType());
            ps.setInt(4, reward.getValue());
            ps.setString(5, reward.getDetails());
            ps.executeUpdate();
            reward.setId(JdbcUtils.extractGeneratedId(ps));
            return reward;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar recompensa", e);
        }
    }

    public Optional<Reward> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_REWARD WHERE id_reward = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar recompensa", e);
        }
    }

    public List<Reward> findAll() {
        List<Reward> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_REWARD ORDER BY id_reward");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar recompensas", e);
        }
    }

    public List<Reward> findByMissionId(Long missionId) {
        List<Reward> list = new ArrayList<>();
        String sql = """
                SELECT r.id_reward, r.nm_reward, r.ds_reward, r.tp_reward, r.vl_reward, r.ds_detalhes
                  FROM T_SOUL_REWARD r
                  JOIN T_SOUL_DESAFIO_REWARD dr ON dr.id_reward = r.id_reward
                 WHERE dr.cd_desafio = ?
                 ORDER BY r.id_reward
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar recompensas da missão", e);
        }
    }

    public Reward update(Reward reward) {
        String sql = "UPDATE T_SOUL_REWARD SET nm_reward = ?, ds_reward = ?, tp_reward = ?, vl_reward = ?, ds_detalhes = ? WHERE id_reward = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reward.getName());
            ps.setString(2, reward.getDescription());
            ps.setString(3, reward.getType());
            ps.setInt(4, reward.getValue());
            ps.setString(5, reward.getDetails());
            ps.setLong(6, reward.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Recompensa não encontrada: " + reward.getId());
            return reward;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar recompensa", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_REWARD WHERE id_reward = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir recompensa", e);
        }
    }

    private Reward map(ResultSet rs) throws SQLException {
        return new Reward(
                rs.getLong("id_reward"),
                rs.getString("nm_reward"),
                rs.getString("ds_reward"),
                rs.getString("tp_reward"),
                rs.getInt("vl_reward"),
                rs.getString("ds_detalhes"));
    }
}
