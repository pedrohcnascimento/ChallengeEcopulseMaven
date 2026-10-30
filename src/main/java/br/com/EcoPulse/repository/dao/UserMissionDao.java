package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.UserMission;
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
public class UserMissionDao {
    private static final String COLUMNS =
            "id_usuario_desafio, cd_usuario, cd_desafio, st_participacao, nr_progresso, dt_conclusao, dt_atribuicao";

    public UserMission create(UserMission um) {
        String sql = """
                INSERT INTO T_SOUL_USUARIO_DESAFIO (
                    cd_usuario, cd_desafio, st_participacao, nr_progresso, dt_conclusao, dt_atribuicao
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_USUARIO_DESAFIO"})) {
            ps.setLong(1, um.getUserId());
            ps.setLong(2, um.getMissionId());
            ps.setString(3, um.getStatus());
            ps.setInt(4, um.getProgress());
            JdbcUtils.setInstant(ps, 5, um.getCompletedAt());
            JdbcUtils.setInstant(ps, 6, um.getAssignedAt());
            ps.executeUpdate();
            um.setId(JdbcUtils.extractGeneratedId(ps));
            return um;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atribuir missão ao usuário", e);
        }
    }

    public Optional<UserMission> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_DESAFIO WHERE id_usuario_desafio = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar participação em missão", e);
        }
    }

    public Optional<UserMission> findByUserAndMission(Long userId, Long missionId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_DESAFIO WHERE cd_usuario = ? AND cd_desafio = ?")) {
            ps.setLong(1, userId);
            ps.setLong(2, missionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao verificar participação do usuário na missão", e);
        }
    }

    public List<UserMission> findAll() {
        List<UserMission> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_DESAFIO ORDER BY id_usuario_desafio");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar participações em missões", e);
        }
    }

    public List<UserMission> findByUserId(Long userId) {
        List<UserMission> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO_DESAFIO WHERE cd_usuario = ? ORDER BY id_usuario_desafio")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar missões do usuário", e);
        }
    }

    public UserMission update(UserMission um) {
        String sql = """
                UPDATE T_SOUL_USUARIO_DESAFIO
                   SET st_participacao = ?, nr_progresso = ?, dt_conclusao = ?
                 WHERE id_usuario_desafio = ?
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, um.getStatus());
            ps.setInt(2, um.getProgress());
            JdbcUtils.setInstant(ps, 3, um.getCompletedAt());
            ps.setLong(4, um.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Participação não encontrada: " + um.getId());
            return um;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar participação em missão", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_USUARIO_DESAFIO WHERE id_usuario_desafio = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao remover participação em missão", e);
        }
    }

    private UserMission map(ResultSet rs) throws SQLException {
        return new UserMission(
                rs.getLong("id_usuario_desafio"),
                rs.getLong("cd_usuario"),
                rs.getLong("cd_desafio"),
                rs.getString("st_participacao"),
                rs.getInt("nr_progresso"),
                JdbcUtils.getInstant(rs, "dt_conclusao"),
                JdbcUtils.getInstant(rs, "dt_atribuicao"));
    }
}
