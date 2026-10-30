package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Post;
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
public class PostDao {
    private static final String COLUMNS = "id_post, cd_usuario, ds_conteudo, st_post, dt_criacao, dt_atualizacao";

    public Post create(Post post) {
        String sql = "INSERT INTO T_SOUL_POSTS (cd_usuario, ds_conteudo, st_post, dt_criacao, dt_atualizacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_POST"})) {
            ps.setLong(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getStatus());
            JdbcUtils.setInstant(ps, 4, post.getCreatedAt());
            JdbcUtils.setInstant(ps, 5, post.getUpdatedAt());
            ps.executeUpdate();
            post.setId(JdbcUtils.extractGeneratedId(ps));
            return post;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar publicação", e);
        }
    }

    public Optional<Post> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_POSTS WHERE id_post = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar publicação", e);
        }
    }

    public List<Post> findAll() {
        List<Post> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_POSTS ORDER BY dt_criacao DESC, id_post DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar publicações", e);
        }
    }

    public List<Post> findByUserId(Long userId) {
        List<Post> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_POSTS WHERE cd_usuario = ? ORDER BY dt_criacao DESC")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar publicações do autor", e);
        }
    }

    public Post update(Post post) {
        String sql = "UPDATE T_SOUL_POSTS SET ds_conteudo = ?, st_post = ?, dt_atualizacao = ? WHERE id_post = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, post.getContent());
            ps.setString(2, post.getStatus());
            JdbcUtils.setInstant(ps, 3, post.getUpdatedAt());
            ps.setLong(4, post.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Publicação não encontrada: " + post.getId());
            return post;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar publicação", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_POSTS WHERE id_post = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir publicação", e);
        }
    }

    private Post map(ResultSet rs) throws SQLException {
        return new Post(
                rs.getLong("id_post"),
                rs.getLong("cd_usuario"),
                rs.getString("ds_conteudo"),
                rs.getString("st_post"),
                JdbcUtils.getInstant(rs, "dt_criacao"),
                JdbcUtils.getInstant(rs, "dt_atualizacao"));
    }
}
