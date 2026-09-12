package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.exception.PersistenceException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** DAO concreto responsável pela persistência JDBC de usuários. */
public class UserDao {
    private static final String COLUMNS = "id, external_id, username, email, created_at, updated_at";

    public User create(User user) {
        String sql = "INSERT INTO T_CHLNG_USERS (external_id, username, email, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getExternalId()); ps.setString(2, user.getUsername()); ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.from(user.getCreatedAt())); ps.setTimestamp(5, Timestamp.from(user.getUpdatedAt())); ps.executeUpdate();
            String idQuery = "SELECT id FROM T_CHLNG_USERS WHERE email = ?";
            try (PreparedStatement idStatement = c.prepareStatement(idQuery)) {
                idStatement.setString(1, user.getEmail());
                try (ResultSet result = idStatement.executeQuery()) {
                    if (!result.next()) throw new SQLException("Usuário criado, mas o ID não foi localizado");
                    user.setId(result.getLong("id"));
                }
            }
            return user;
        } catch (SQLException e) { throw new PersistenceException("Erro ao criar usuário", e); }
    }

    public Optional<User> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_CHLNG_USERS WHERE id = ?")) {
            ps.setLong(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        } catch (SQLException e) { throw new PersistenceException("Erro ao buscar usuário", e); }
    }

    public List<User> findByUsername(String username) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM T_CHLNG_USERS WHERE UPPER(username) LIKE UPPER(?) ORDER BY username";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + username.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) users.add(map(rs)); return users; }
        } catch (SQLException e) { throw new PersistenceException("Erro ao pesquisar usuários", e); }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_CHLNG_USERS ORDER BY id"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs)); return users;
        } catch (SQLException e) { throw new PersistenceException("Erro ao listar usuários", e); }
    }

    public User update(User user) {
        String sql = "UPDATE T_CHLNG_USERS SET external_id = ?, username = ?, email = ?, updated_at = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getExternalId()); ps.setString(2, user.getUsername()); ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.from(user.getUpdatedAt())); ps.setLong(5, user.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Usuário não encontrado: " + user.getId()); return user;
        } catch (SQLException e) { throw new PersistenceException("Erro ao atualizar usuário", e); }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM T_CHLNG_USERS WHERE id = ?")) {
            ps.setLong(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new PersistenceException("Erro ao excluir usuário", e); }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getLong("id"), rs.getString("external_id"), rs.getString("username"), rs.getString("email"),
                rs.getTimestamp("created_at").toInstant(), rs.getTimestamp("updated_at").toInstant());
    }
}
