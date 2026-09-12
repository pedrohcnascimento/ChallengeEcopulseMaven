package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Mission;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class MissionService {
    private final MissionDao dao = new MissionDao();
    public Mission create(Mission mission){validate(mission);mission.setTitle(mission.getTitle().trim());mission.setIsActive(true);mission.setCreatedAt(Instant.now());mission.setUpdatedAt(mission.getCreatedAt());return dao.create(mission);}
    public List<Mission> getAll(){return dao.findAll(false);}
    public List<Mission> getActive(){return dao.findAll(true);}
    public Optional<Mission> findById(Long id){if(id==null||id<=0)throw new IllegalArgumentException("ID inválido");return dao.findById(id);}
    public Mission updateDetails(Long id,String title,String description,Integer points){Mission m=findById(id).orElseThrow(()->new IllegalArgumentException("Missão não encontrada: "+id));m.updateDetails(title,description,points);return dao.update(m);}
    public Mission changeStatus(Long id,boolean active){Mission m=findById(id).orElseThrow(()->new IllegalArgumentException("Missão não encontrada: "+id));if(active)m.activate();else m.deactivate();return dao.update(m);}
    public boolean delete(Long id){return dao.deleteById(id);}
    private void validate(Mission m){if(m==null||m.getTitle()==null||m.getTitle().isBlank())throw new IllegalArgumentException("Título da missão é obrigatório");if(m.getType()==null||m.getType().isBlank())throw new IllegalArgumentException("Tipo da missão é obrigatório");if(m.getRewardPoints()==null||m.getRewardPoints()<0)throw new IllegalArgumentException("Pontuação inválida");}
}
