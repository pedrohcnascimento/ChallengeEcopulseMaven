package br.com.EcoPulse.repository;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Avatar;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvatarRepository {
    private static final String COLUMNS = "id, user_id, name, visual_config, personality_type, level_number, experience_points, last_interaction, created_at, updated_at";
    public Avatar create(Avatar avatar) {
        String sql = "INSERT INTO T_CHLNG_AVATARS (user_id,name,visual_config,personality_type,level_number,experience_points,last_interaction,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c=ConnectionFactory.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setLong(1,avatar.getUserId()); ps.setString(2,avatar.getName()); ps.setString(3,avatar.getVisualConfig()); ps.setString(4,avatar.getPersonalityType()); ps.setInt(5,avatar.getLevel()); ps.setLong(6,avatar.getExperiencePoints()); ps.setTimestamp(7,Timestamp.from(avatar.getLastInteraction())); ps.setTimestamp(8,Timestamp.from(avatar.getCreatedAt())); ps.setTimestamp(9,Timestamp.from(avatar.getUpdatedAt())); ps.executeUpdate();
            try (PreparedStatement id=c.prepareStatement("SELECT id FROM T_CHLNG_AVATARS WHERE user_id=? AND name=? ORDER BY id DESC FETCH FIRST 1 ROWS ONLY")) { id.setLong(1,avatar.getUserId()); id.setString(2,avatar.getName()); try(ResultSet rs=id.executeQuery()){if(rs.next()) avatar.setId(rs.getLong(1));} }
            return avatar;
        } catch(SQLException e){throw new IllegalStateException("Erro ao criar avatar",e);}
    }
    public Optional<Avatar> findById(Long id){try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT "+COLUMNS+" FROM T_CHLNG_AVATARS WHERE id=?")){ps.setLong(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next()?Optional.of(map(rs)):Optional.empty();}}catch(SQLException e){throw new IllegalStateException("Erro ao buscar avatar",e);}}
    public List<Avatar> findAll(){List<Avatar> result=new ArrayList<>();try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("SELECT "+COLUMNS+" FROM T_CHLNG_AVATARS ORDER BY id");ResultSet rs=ps.executeQuery()){while(rs.next())result.add(map(rs));return result;}catch(SQLException e){throw new IllegalStateException("Erro ao listar avatares",e);}}
    public Avatar update(Avatar a){String sql="UPDATE T_CHLNG_AVATARS SET name=?,visual_config=?,personality_type=?,level_number=?,experience_points=?,last_interaction=?,updated_at=? WHERE id=?";try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,a.getName());ps.setString(2,a.getVisualConfig());ps.setString(3,a.getPersonalityType());ps.setInt(4,a.getLevel());ps.setLong(5,a.getExperiencePoints());ps.setTimestamp(6,Timestamp.from(a.getLastInteraction()));ps.setTimestamp(7,Timestamp.from(a.getUpdatedAt()));ps.setLong(8,a.getId());if(ps.executeUpdate()==0)throw new IllegalArgumentException("Avatar não encontrado: "+a.getId());return a;}catch(SQLException e){throw new IllegalStateException("Erro ao atualizar avatar",e);}}
    public boolean deleteById(Long id){try(Connection c=ConnectionFactory.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM T_CHLNG_AVATARS WHERE id=?")){ps.setLong(1,id);return ps.executeUpdate()>0;}catch(SQLException e){throw new IllegalStateException("Erro ao excluir avatar",e);}}
    private Avatar map(ResultSet rs)throws SQLException{return new Avatar(rs.getLong("id"),rs.getLong("user_id"),rs.getString("name"),rs.getString("visual_config"),rs.getString("personality_type"),rs.getInt("level_number"),rs.getLong("experience_points"),toInstant(rs.getTimestamp("last_interaction")),rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant());}
    private java.time.Instant toInstant(Timestamp t){return t==null?null:t.toInstant();}
}
