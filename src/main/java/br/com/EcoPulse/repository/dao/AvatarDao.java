package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Avatar;
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
public class AvatarDao {
    private static final String COLUMNS =
            "id_avatar, cd_usuario, nm_avatar, ds_visual_config, ds_personalidade, nr_nivel, nr_xp, dt_criacao, dt_atualizacao";

    public Avatar create(Avatar avatar) {
        String sql = """
                INSERT INTO T_SOUL_AVATAR (
                    cd_usuario, nm_avatar, ds_visual_config, ds_personalidade, nr_nivel, nr_xp, dt_criacao, dt_atualizacao
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_AVATAR"})) {
            ps.setLong(1, avatar.getUserId());
            ps.setString(2, avatar.getName());
            ps.setString(3, avatar.getVisualConfig());
            ps.setString(4, avatar.getPersonalityType());
            ps.setInt(5, avatar.getLevel());
            ps.setLong(6, avatar.getExperiencePoints());
            JdbcUtils.setInstant(ps, 7, avatar.getCreatedAt());
            JdbcUtils.setInstant(ps, 8, avatar.getUpdatedAt());
            ps.executeUpdate();
            avatar.setId(JdbcUtils.extractGeneratedId(ps));
            return avatar;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar avatar", e);
        }
    }

    public Optional<Avatar> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_AVATAR WHERE id_avatar = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar avatar", e);
        }
    }

    public List<Avatar> findAll() {
        List<Avatar> result = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_AVATAR ORDER BY id_avatar");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
            return result;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar avatares", e);
        }
    }

    public List<Avatar> findByUserId(Long userId) {
        List<Avatar> result = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_AVATAR WHERE cd_usuario = ? ORDER BY id_avatar")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
                return result;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar avatares do usuário", e);
        }
    }

    public Avatar update(Avatar a) {
        String sql = """
                UPDATE T_SOUL_AVATAR
                   SET nm_avatar = ?, ds_visual_config = ?, ds_personalidade = ?, nr_nivel = ?, nr_xp = ?, dt_atualizacao = ?
                 WHERE id_avatar = ?
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getVisualConfig());
            ps.setString(3, a.getPersonalityType());
            ps.setInt(4, a.getLevel());
            ps.setLong(5, a.getExperiencePoints());
            JdbcUtils.setInstant(ps, 6, a.getUpdatedAt());
            ps.setLong(7, a.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Avatar não encontrado: " + a.getId());
            return a;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar avatar", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_AVATAR WHERE id_avatar = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir avatar", e);
        }
    }

    private Avatar map(ResultSet rs) throws SQLException {
        return new Avatar(
                rs.getLong("id_avatar"),
                rs.getLong("cd_usuario"),
                rs.getString("nm_avatar"),
                rs.getString("ds_visual_config"),
                rs.getString("ds_personalidade"),
                rs.getInt("nr_nivel"),
                rs.getLong("nr_xp"),
                JdbcUtils.getInstant(rs, "dt_criacao"),
                JdbcUtils.getInstant(rs, "dt_atualizacao"));
    }
}
