package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Avatar;
import br.com.EcoPulse.repository.dao.AvatarDao;
import br.com.EcoPulse.repository.dao.UserDao;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AvatarService {
    private final AvatarDao dao = new AvatarDao();
    private final UserDao userDao = new UserDao();
    public Avatar create(Avatar avatar){
        if(avatar==null||avatar.getUserId()==null)throw new DomainValidationException("Usuário do avatar é obrigatório");
        if(userDao.findById(avatar.getUserId()).isEmpty())throw new ResourceNotFoundException("Usuário não encontrado: "+avatar.getUserId()+". Cadastre o usuário antes de criar o avatar.");
        if(avatar.getName()==null||avatar.getName().isBlank())throw new DomainValidationException("Nome do avatar é obrigatório");
        avatar.setName(avatar.getName().trim());avatar.setLevel(1);avatar.setExperiencePoints(0L);avatar.setCreatedAt(Instant.now());avatar.setUpdatedAt(avatar.getCreatedAt());avatar.setLastInteraction(avatar.getCreatedAt());return dao.create(avatar);
    }
    public List<Avatar> getAll(){return dao.findAll();}
    public Optional<Avatar> findById(Long id){if(id==null||id<=0)throw new DomainValidationException("ID inválido");return dao.findById(id);}
    public Avatar addExperience(Long id,long points){Avatar a=findById(id).orElseThrow(()->new ResourceNotFoundException("Avatar não encontrado: "+id));a.addExperience(points);return dao.update(a);}
    public Avatar registerInteraction(Long id){Avatar a=findById(id).orElseThrow(()->new ResourceNotFoundException("Avatar não encontrado: "+id));a.registerInteraction();return dao.update(a);}
    public boolean delete(Long id){return dao.deleteById(id);}
}
