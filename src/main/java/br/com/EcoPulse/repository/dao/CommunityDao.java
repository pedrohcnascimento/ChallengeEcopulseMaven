package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Community;
import br.com.EcoPulse.domain.MissionCommunity;
import br.com.EcoPulse.domain.UserCommunity;
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
public class CommunityDao {
    private static final String COLUMNS = "cd_comunidade, nm_comunidade, ds_comunidade, tp_comunidade, dt_criacao";

    public Community create(Community community) {
        String sql = "INSERT INTO T_SOUL_COMUNIDADE (nm_comunidade, ds_comunidade, tp_comunidade, dt_criacao) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"CD_COMUNIDADE"})) {
            ps.setString(1, community.getName());
            ps.setString(2, community.getDescription());
            ps.setString(3, community.getCommunityType());
            JdbcUtils.setInstant(ps, 4, community.getCreatedAt());
            ps.executeUpdate();
            community.setId(JdbcUtils.extractGeneratedId(ps));
            return community;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar comunidade", e);
        }
    }

    public Optional<Community> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_COMUNIDADE WHERE cd_comunidade = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar comunidade", e);
        }
    }

    public Optional<Community> findByName(String name) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_COMUNIDADE WHERE LOWER(nm_comunidade) = LOWER(?)")) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar comunidade por nome", e);
        }
    }

    public List<Community> findAll() {
        List<Community> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_COMUNIDADE ORDER BY cd_comunidade");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar comunidades", e);
        }
    }

    public Community update(Community community) {
        String sql = "UPDATE T_SOUL_COMUNIDADE SET nm_comunidade = ?, ds_comunidade = ?, tp_comunidade = ? WHERE cd_comunidade = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, community.getName());
            ps.setString(2, community.getDescription());
            ps.setString(3, community.getCommunityType());
            ps.setLong(4, community.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Comunidade não encontrada: " + community.getId());
            return community;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar comunidade", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_COMUNIDADE WHERE cd_comunidade = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir comunidade", e);
        }
    }

    public boolean isMember(Long userId, Long communityId) {
        String sql = "SELECT 1 FROM T_SOUL_USUARIO_COMUN WHERE cd_usuario = ? AND cd_comunidade = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao verificar membro da comunidade", e);
        }
    }

    public UserCommunity addMember(Long userId, Long communityId) {
        String sql = "INSERT INTO T_SOUL_USUARIO_COMUN (cd_usuario, cd_comunidade) VALUES (?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, communityId);
            ps.executeUpdate();
            return new UserCommunity(userId, communityId);
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao adicionar usuário à comunidade", e);
        }
    }

    public boolean removeMember(Long userId, Long communityId) {
        String sql = "DELETE FROM T_SOUL_USUARIO_COMUN WHERE cd_usuario = ? AND cd_comunidade = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, communityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao remover usuário da comunidade", e);
        }
    }

    public List<Long> findMemberIds(Long communityId) {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT cd_usuario FROM T_SOUL_USUARIO_COMUN WHERE cd_comunidade = ? ORDER BY cd_usuario";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong(1));
                return ids;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar membros da comunidade", e);
        }
    }

    public void deleteAllMembersByCommunity(Long communityId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_USUARIO_COMUN WHERE cd_comunidade = ?")) {
            ps.setLong(1, communityId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao remover vínculos de membros da comunidade", e);
        }
    }

    public Optional<MissionCommunity> findMissionLink(Long missionId, Long communityId) {
        String sql = "SELECT cd_desafio, cd_comunidade, nr_pontuacao, st_status FROM T_SOUL_DESAFIO_COMUN WHERE cd_desafio = ? AND cd_comunidade = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            ps.setLong(2, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapMissionCommunity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar missão da comunidade", e);
        }
    }

    public MissionCommunity addMissionLink(MissionCommunity mc) {
        String sql = "INSERT INTO T_SOUL_DESAFIO_COMUN (cd_desafio, cd_comunidade, nr_pontuacao, st_status) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, mc.getMissionId());
            ps.setLong(2, mc.getCommunityId());
            ps.setInt(3, mc.getScore());
            ps.setString(4, mc.getStatus());
            ps.executeUpdate();
            return mc;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao associar missão à comunidade", e);
        }
    }

    public MissionCommunity updateMissionLink(MissionCommunity mc) {
        String sql = "UPDATE T_SOUL_DESAFIO_COMUN SET nr_pontuacao = ?, st_status = ? WHERE cd_desafio = ? AND cd_comunidade = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mc.getScore());
            ps.setString(2, mc.getStatus());
            ps.setLong(3, mc.getMissionId());
            ps.setLong(4, mc.getCommunityId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Vínculo missão-comunidade não encontrado");
            return mc;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar missão da comunidade", e);
        }
    }

    public List<MissionCommunity> findMissionsByCommunity(Long communityId) {
        List<MissionCommunity> list = new ArrayList<>();
        String sql = "SELECT cd_desafio, cd_comunidade, nr_pontuacao, st_status FROM T_SOUL_DESAFIO_COMUN WHERE cd_comunidade = ? ORDER BY cd_desafio";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, communityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapMissionCommunity(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar missões da comunidade", e);
        }
    }

    public boolean removeMissionLink(Long missionId, Long communityId) {
        String sql = "DELETE FROM T_SOUL_DESAFIO_COMUN WHERE cd_desafio = ? AND cd_comunidade = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, missionId);
            ps.setLong(2, communityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao desvincular missão da comunidade", e);
        }
    }

    public void deleteAllMissionsByCommunity(Long communityId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_DESAFIO_COMUN WHERE cd_comunidade = ?")) {
            ps.setLong(1, communityId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao remover missões da comunidade", e);
        }
    }

    private Community map(ResultSet rs) throws SQLException {
        return new Community(
                rs.getLong("cd_comunidade"),
                rs.getString("nm_comunidade"),
                rs.getString("ds_comunidade"),
                rs.getString("tp_comunidade"),
                JdbcUtils.getInstant(rs, "dt_criacao"));
    }

    private MissionCommunity mapMissionCommunity(ResultSet rs) throws SQLException {
        return new MissionCommunity(
                rs.getLong("cd_desafio"),
                rs.getLong("cd_comunidade"),
                rs.getInt("nr_pontuacao"),
                rs.getString("st_status"));
    }
}
