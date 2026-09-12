package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Avatar;
import br.com.EcoPulse.repository.AvatarRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AvatarService {
    private final AvatarRepository repository = new AvatarRepository();
    public Avatar create(Avatar avatar){if(avatar==null||avatar.getUserId()==null)throw new IllegalArgumentException("Usuário do avatar é obrigatório");if(avatar.getName()==null||avatar.getName().isBlank())throw new IllegalArgumentException("Nome do avatar é obrigatório");avatar.setName(avatar.getName().trim());avatar.setLevel(1);avatar.setExperiencePoints(0L);avatar.setCreatedAt(Instant.now());avatar.setUpdatedAt(avatar.getCreatedAt());avatar.setLastInteraction(avatar.getCreatedAt());return repository.create(avatar);}
    public List<Avatar> getAll(){return repository.findAll();}
    public Optional<Avatar> findById(Long id){if(id==null||id<=0)throw new IllegalArgumentException("ID inválido");return repository.findById(id);}
    public Avatar addExperience(Long id,long points){Avatar a=findById(id).orElseThrow(()->new IllegalArgumentException("Avatar não encontrado: "+id));a.addExperience(points);return repository.update(a);}
    public Avatar registerInteraction(Long id){Avatar a=findById(id).orElseThrow(()->new IllegalArgumentException("Avatar não encontrado: "+id));a.registerInteraction();return repository.update(a);}
    public boolean delete(Long id){return repository.deleteById(id);}
}
