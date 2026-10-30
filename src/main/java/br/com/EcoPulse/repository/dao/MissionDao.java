package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Mission;
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
public class MissionDao {
    private static final String COLUMNS =
            "cd_desafio, ds_nomedesafio, ds_descricao, ds_tipo, ds_regras, dt_iniciodesafio, dt_fimdesafio, st_status, dt_criacao, dt_atualizacao";

    public Mission create(Mission m) {
        String sql = """
                INSERT INTO T_SOUL_DESAFIO (
                    ds_nomedesafio, ds_descricao, ds_tipo, ds_regras, dt_iniciodesafio, dt_fimdesafio, st_status, dt_criacao, dt_atualizacao
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"CD_DESAFIO"})) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getType());
            ps.setString(4, m.getRules());
            JdbcUtils.setInstant(ps, 5, m.getStartDate());
            JdbcUtils.setInstant(ps, 6, m.getEndDate());
            ps.setString(7, m.getStatus());
            JdbcUtils.setInstant(ps, 8, m.getCreatedAt());
            JdbcUtils.setInstant(ps, 9, m.getUpdatedAt());
            ps.executeUpdate();
            m.setId(JdbcUtils.extractGeneratedId(ps));
            return m;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar missão", e);
        }
    }

    public Optional<Mission> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_DESAFIO WHERE cd_desafio = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar missão", e);
        }
    }

    public List<Mission> findAll(boolean onlyActive) {
        List<Mission> result = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM T_SOUL_DESAFIO"
                + (onlyActive ? " WHERE st_status = 'ATIVA'" : "")
                + " ORDER BY cd_desafio";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar missões", e);
        }
    }

    public Mission update(Mission m) {
        String sql = """
                UPDATE T_SOUL_DESAFIO
                   SET ds_nomedesafio = ?, ds_descricao = ?, ds_tipo = ?, ds_regras = ?,
                       dt_iniciodesafio = ?, dt_fimdesafio = ?, st_status = ?, dt_atualizacao = ?
                 WHERE cd_desafio = ?
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getType());
            ps.setString(4, m.getRules());
            JdbcUtils.setInstant(ps, 5, m.getStartDate());
            JdbcUtils.setInstant(ps, 6, m.getEndDate());
            ps.setString(7, m.getStatus());
            JdbcUtils.setInstant(ps, 8, m.getUpdatedAt());
            ps.setLong(9, m.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Missão não encontrada: " + m.getId());
            return m;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar missão", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_DESAFIO WHERE cd_desafio = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir missão", e);
        }
    }

    private Mission map(ResultSet r) throws SQLException {
        return new Mission(
                r.getLong("cd_desafio"),
                r.getString("ds_nomedesafio"),
                r.getString("ds_descricao"),
                r.getString("ds_tipo"),
                r.getString("ds_regras"),
                JdbcUtils.getInstant(r, "dt_iniciodesafio"),
                JdbcUtils.getInstant(r, "dt_fimdesafio"),
                r.getString("st_status"),
                JdbcUtils.getInstant(r, "dt_criacao"),
                JdbcUtils.getInstant(r, "dt_atualizacao"));
    }
}
