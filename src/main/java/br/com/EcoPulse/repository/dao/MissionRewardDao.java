package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.MissionReward;
import br.com.EcoPulse.exception.PersistenceException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MissionRewardDao {

    public void addAssociation(Long missionId, Long rewardId) {
        String sql = "INSERT INTO T_SOUL_DESAFIO_REWARD (cd_desafio, id_reward) VALUES (?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            ps.setLong(2, rewardId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao associar recompensa à missão", e);
        }
    }

    public boolean existsAssociation(Long missionId, Long rewardId) {
        String sql = "SELECT 1 FROM T_SOUL_DESAFIO_REWARD WHERE cd_desafio = ? AND id_reward = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            ps.setLong(2, rewardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao verificar associação missão-recompensa", e);
        }
    }

    public int countByMissionId(Long missionId) {
        String sql = "SELECT COUNT(*) FROM T_SOUL_DESAFIO_REWARD WHERE cd_desafio = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao contar recompensas da missão", e);
        }
    }

    public int countByRewardId(Long rewardId) {
        String sql = "SELECT COUNT(*) FROM T_SOUL_DESAFIO_REWARD WHERE id_reward = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, rewardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao contar missões vinculadas à recompensa", e);
        }
    }

    public List<MissionReward> findByMissionId(Long missionId) {
        List<MissionReward> list = new ArrayList<>();
        String sql = "SELECT cd_desafio, id_reward FROM T_SOUL_DESAFIO_REWARD WHERE cd_desafio = ? ORDER BY id_reward";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new MissionReward(rs.getLong("cd_desafio"), rs.getLong("id_reward")));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar vínculos de recompensa da missão", e);
        }
    }

    public boolean removeAssociation(Long missionId, Long rewardId) {
        String sql = "DELETE FROM T_SOUL_DESAFIO_REWARD WHERE cd_desafio = ? AND id_reward = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            ps.setLong(2, rewardId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao remover associação missão-recompensa", e);
        }
    }

    public void deleteAllByMissionId(Long missionId) {
        String sql = "DELETE FROM T_SOUL_DESAFIO_REWARD WHERE cd_desafio = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao remover recompensas da missão", e);
        }
    }
}
