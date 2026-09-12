package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Avatar;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AvatarService {
    private final AvatarDao dao = new AvatarDao();
    public Avatar create(Avatar avatar){if(avatar==null||avatar.getUserId()==null)throw new IllegalArgumentException("Usuário do avatar é obrigatório");if(avatar.getName()==null||avatar.getName().isBlank())throw new IllegalArgumentException("Nome do avatar é obrigatório");avatar.setName(avatar.getName().trim());avatar.setLevel(1);avatar.setExperiencePoints(0L);avatar.setCreatedAt(Instant.now());avatar.setUpdatedAt(avatar.getCreatedAt());avatar.setLastInteraction(avatar.getCreatedAt());return dao.create(avatar);}
    public List<Avatar> getAll(){return dao.findAll();}
    public Optional<Avatar> findById(Long id){if(id==null||id<=0)throw new IllegalArgumentException("ID inválido");return dao.findById(id);}
    public Avatar addExperience(Long id,long points){Avatar a=findById(id).orElseThrow(()->new IllegalArgumentException("Avatar não encontrado: "+id));a.addExperience(points);return dao.update(a);}
    public Avatar registerInteraction(Long id){Avatar a=findById(id).orElseThrow(()->new IllegalArgumentException("Avatar não encontrado: "+id));a.registerInteraction();return dao.update(a);}
    public boolean delete(Long id){return dao.deleteById(id);}
}
