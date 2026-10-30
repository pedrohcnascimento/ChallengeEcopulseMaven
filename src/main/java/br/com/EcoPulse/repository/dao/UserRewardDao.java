package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.UserReward;
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
public class UserRewardDao {
    private static final String COLUMNS =
            "id_usuario_reward, cd_usuario, id_reward, cd_desafio_origem, dt_aquisicao, fl_resgatada";

    public UserReward create(UserReward ur) {
        String sql = """
                INSERT INTO T_SOUL_USUARIO_REWARD (
                    cd_usuario, id_reward, cd_desafio_origem, dt_aquisicao, fl_resgatada
                ) VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_USUARIO_REWARD"})) {
            ps.setLong(1, ur.getUserId());
            ps.setLong(2, ur.getRewardId());
            JdbcUtils.setNullableLong(ps, 3, ur.getSourceMissionId());
            JdbcUtils.setInstant(ps, 4, ur.getAcquiredAt());
            ps.setInt(5, Boolean.TRUE.equals(ur.getIsClaimed()) ? 1 : 0);
            ps.executeUpdate();
            ur.setId(JdbcUtils.extractGeneratedId(ps));
            return ur;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao conceder recompensa ao usuário", e);
        }
    }

    public Optional<UserReward> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_REWARD WHERE id_usuario_reward = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar recompensa do usuário", e);
        }
    }

    public boolean existsByUserRewardAndMission(Long userId, Long rewardId, Long sourceMissionId) {
        String sql = "SELECT 1 FROM T_SOUL_USUARIO_REWARD WHERE cd_usuario = ? AND id_reward = ? AND cd_desafio_origem = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, rewardId);
            ps.setLong(3, sourceMissionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao verificar recompensa já concedida", e);
        }
    }

    public List<UserReward> findAll() {
        List<UserReward> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_REWARD ORDER BY id_usuario_reward");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar recompensas adquiridas", e);
        }
    }

    public List<UserReward> findByUserId(Long userId) {
        List<UserReward> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_REWARD WHERE cd_usuario = ? ORDER BY id_usuario_reward")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar recompensas do usuário", e);
        }
    }

    public UserReward update(UserReward ur) {
        String sql = "UPDATE T_SOUL_USUARIO_REWARD SET fl_resgatada = ? WHERE id_usuario_reward = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Boolean.TRUE.equals(ur.getIsClaimed()) ? 1 : 0);
            ps.setLong(2, ur.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Recompensa do usuário não encontrada: " + ur.getId());
            return ur;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao resgatar recompensa do usuário", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_USUARIO_REWARD WHERE id_usuario_reward = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir recompensa do usuário", e);
        }
    }

    private UserReward map(ResultSet rs) throws SQLException {
        return new UserReward(
                rs.getLong("id_usuario_reward"),
                rs.getLong("cd_usuario"),
                rs.getLong("id_reward"),
                JdbcUtils.getNullableLong(rs, "cd_desafio_origem"),
                JdbcUtils.getInstant(rs, "dt_aquisicao"),
                rs.getInt("fl_resgatada") == 1);
    }
}
