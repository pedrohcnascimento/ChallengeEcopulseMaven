package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Family;
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
public class FamilyDao {
    private static final String COLUMNS = "cd_familia, nm_familia, ds_parentesco";

    public Family create(Family family) {
        String sql = "INSERT INTO T_SOUL_FAMILIA (nm_familia, ds_parentesco) VALUES (?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"CD_FAMILIA"})) {
            ps.setString(1, family.getName());
            ps.setString(2, family.getRelationshipDescription());
            ps.executeUpdate();
            family.setId(JdbcUtils.extractGeneratedId(ps));
            return family;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar família", e);
        }
    }

    public Optional<Family> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_FAMILIA WHERE cd_familia = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar família", e);
        }
    }

    public List<Family> findAll() {
        List<Family> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_FAMILIA ORDER BY cd_familia");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar famílias", e);
        }
    }

    public Family update(Family family) {
        String sql = "UPDATE T_SOUL_FAMILIA SET nm_familia = ?, ds_parentesco = ? WHERE cd_familia = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, family.getName());
            ps.setString(2, family.getRelationshipDescription());
            ps.setLong(3, family.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Família não encontrada: " + family.getId());
            return family;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar família", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_FAMILIA WHERE cd_familia = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir família", e);
        }
    }

    private Family map(ResultSet rs) throws SQLException {
        return new Family(rs.getLong("cd_familia"), rs.getString("nm_familia"), rs.getString("ds_parentesco"));
    }
}
