package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.User;
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

/** DAO concreto responsável pela persistência JDBC de usuários em T_SOUL_USUARIO. */
@Repository
public class UserDao {
    private static final String COLUMNS =
            "cd_usuario, external_id, username, ds_login, ds_senha, nm_usuario, "
            + "dt_nascimento, nr_cpf, ds_nacionalidade, ds_email, cd_familia, dt_criacao, dt_atualizacao";

    public User create(User user) {
        String sql = """
                INSERT INTO T_SOUL_USUARIO (
                    external_id, username, ds_login, ds_senha, nm_usuario,
                    dt_nascimento, nr_cpf, ds_nacionalidade, ds_email, cd_familia, dt_criacao, dt_atualizacao
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"CD_USUARIO"})) {
            ps.setString(1, user.getExternalId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getName());
            JdbcUtils.setLocalDate(ps, 6, user.getBirthDate());
            ps.setString(7, user.getCpf());
            ps.setString(8, user.getNationality());
            ps.setString(9, user.getEmail());
            JdbcUtils.setNullableLong(ps, 10, user.getFamilyId());
            JdbcUtils.setInstant(ps, 11, user.getCreatedAt());
            JdbcUtils.setInstant(ps, 12, user.getUpdatedAt());
            ps.executeUpdate();
            user.setId(JdbcUtils.extractGeneratedId(ps));
            return user;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao criar usuário", e);
        }
    }

    public Optional<User> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO WHERE cd_usuario = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário", e);
        }
    }

    public Optional<User> findByLogin(String login) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO WHERE LOWER(ds_login) = LOWER(?)")) {
            ps.setString(1, login.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por login", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO WHERE LOWER(ds_email) = LOWER(?)")) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por e-mail", e);
        }
    }

    public Optional<User> findByCpf(String cpf) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO WHERE nr_cpf = ?")) {
            ps.setString(1, cpf.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por CPF", e);
        }
    }

    public List<User> findByUsername(String username) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM T_SOUL_USUARIO WHERE UPPER(username) LIKE UPPER(?) ORDER BY username";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + username.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(map(rs));
                return users;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao pesquisar usuários", e);
        }
    }

    public List<User> findByFamilyId(Long familyId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM T_SOUL_USUARIO WHERE cd_familia = ? ORDER BY cd_usuario";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, familyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(map(rs));
                return users;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar membros da família", e);
        }
    }

    public void clearFamilyReferences(Long familyId) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE T_SOUL_USUARIO SET cd_familia = NULL WHERE cd_familia = ?")) {
            ps.setLong(1, familyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao desvincular membros da família", e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_USUARIO ORDER BY cd_usuario");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
            return users;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar usuários", e);
        }
    }

    public User update(User user) {
        String sql = """
                UPDATE T_SOUL_USUARIO
                   SET external_id = ?, username = ?, ds_login = ?, ds_senha = ?, nm_usuario = ?,
                       dt_nascimento = ?, nr_cpf = ?, ds_nacionalidade = ?, ds_email = ?, cd_familia = ?, dt_atualizacao = ?
                 WHERE cd_usuario = ?
                """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getExternalId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getName());
            JdbcUtils.setLocalDate(ps, 6, user.getBirthDate());
            ps.setString(7, user.getCpf());
            ps.setString(8, user.getNationality());
            ps.setString(9, user.getEmail());
            JdbcUtils.setNullableLong(ps, 10, user.getFamilyId());
            JdbcUtils.setInstant(ps, 11, user.getUpdatedAt());
            ps.setLong(12, user.getId());
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Usuário não encontrado: " + user.getId());
            return user;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar usuário", e);
        }
    }

    public boolean deleteById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM T_SOUL_USUARIO WHERE cd_usuario = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir usuário", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("cd_usuario"),
                rs.getString("external_id"),
                rs.getString("username"),
                rs.getString("ds_login"),
                rs.getString("ds_senha"),
                rs.getString("nm_usuario"),
                JdbcUtils.getLocalDate(rs, "dt_nascimento"),
                rs.getString("nr_cpf"),
                rs.getString("ds_nacionalidade"),
                rs.getString("ds_email"),
                JdbcUtils.getNullableLong(rs, "cd_familia"),
                JdbcUtils.getInstant(rs, "dt_criacao"),
                JdbcUtils.getInstant(rs, "dt_atualizacao"));
    }
}
