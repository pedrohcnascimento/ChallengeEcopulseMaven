package br.com.EcoPulse.repository.dao;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.ConversationHistory;
import br.com.EcoPulse.exception.PersistenceException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** DAO para persistência da estrutura de histórico de conversas (sem API de chat nesta fase). */
@Repository
public class ConversationHistoryDao {
    private static final String COLUMNS = "id_conversa, cd_usuario, ds_conteudo, dt_envio, ds_destinatario";

    public ConversationHistory create(ConversationHistory history) {
        String sql = "INSERT INTO T_SOUL_HISTORICO_CONVERSA (cd_usuario, ds_conteudo, dt_envio, ds_destinatario) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, new String[]{"ID_CONVERSA"})) {
            ps.setLong(1, history.getUserId());
            ps.setString(2, history.getContent());
            JdbcUtils.setInstant(ps, 3, history.getSentAt());
            ps.setString(4, history.getRecipient());
            ps.executeUpdate();
            history.setId(JdbcUtils.extractGeneratedId(ps));
            return history;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao persistir histórico de conversa", e);
        }
    }

    public Optional<ConversationHistory> findById(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_HISTORICO_CONVERSA WHERE id_conversa = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao consultar histórico de conversa", e);
        }
    }

    public List<ConversationHistory> findByUserId(Long userId) {
        List<ConversationHistory> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM T_SOUL_HISTORICO_CONVERSA WHERE cd_usuario = ? ORDER BY dt_envio")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar histórico de conversas do usuário", e);
        }
    }

    private ConversationHistory map(ResultSet rs) throws SQLException {
        return new ConversationHistory(
                rs.getLong("id_conversa"),
                rs.getLong("cd_usuario"),
                rs.getString("ds_conteudo"),
                JdbcUtils.getInstant(rs, "dt_envio"),
                rs.getString("ds_destinatario"));
    }
}
