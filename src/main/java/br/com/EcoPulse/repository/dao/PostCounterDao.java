package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.PostCounter;
import br.com.EcoPulse.exception.PersistenceException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class PostCounterDao {
    private static final String COLUMNS = "id_contador, id_post, nr_curtidas, nr_comentarios, nr_compartilhamentos";

    public PostCounter create(PostCounter counter) {
        String sql = "INSERT INTO T_SOUL_CONTADORES (id_post, nr_curtidas, nr_comentarios, nr_compartilhamentos) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_CONTADOR"})) {
            ps.setLong(1, counter.getPostId());
            ps.setInt(2, counter.getLikeCount());
            ps.setInt(3, counter.getCommentCount());
            ps.setInt(4, counter.getShareCount());
            ps.executeUpdate();
            counter.setId(JdbcUtils.extractGeneratedId(ps));
            return counter;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao inicializar contadores da publicação", e);
        }
    }

    public Optional<PostCounter> findByPostId(Long postId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_CONTADORES WHERE id_post = ?")) {
            ps.setLong(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar contadores da publicação", e);
        }
    }

    public PostCounter update(PostCounter counter) {
        String sql = "UPDATE T_SOUL_CONTADORES SET nr_curtidas = ?, nr_comentarios = ?, nr_compartilhamentos = ? WHERE id_post = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, counter.getLikeCount());
            ps.setInt(2, counter.getCommentCount());
            ps.setInt(3, counter.getShareCount());
            ps.setLong(4, counter.getPostId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Contadores não encontrados para publicação: " + counter.getPostId());
            return counter;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar contadores da publicação", e);
        }
    }

    public void deleteByPostId(Long postId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_CONTADORES WHERE id_post = ?")) {
            ps.setLong(1, postId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir contadores da publicação", e);
        }
    }

    private PostCounter map(ResultSet rs) throws SQLException {
        return new PostCounter(
                rs.getLong("id_contador"),
                rs.getLong("id_post"),
                rs.getInt("nr_curtidas"),
                rs.getInt("nr_comentarios"),
                rs.getInt("nr_compartilhamentos"));
    }
}
