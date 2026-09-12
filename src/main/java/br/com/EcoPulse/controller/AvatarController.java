package br.com.EcoPulse.controller;

import br.com.EcoPulse.domain.Avatar;
import br.com.EcoPulse.service.AvatarService;
import java.util.List;

public class AvatarController {
    private final AvatarService service = new AvatarService();
    public Avatar create(Avatar avatar){return service.create(avatar);}
    public List<Avatar> list(){return service.getAll();}
    public Avatar find(Long id){return service.findById(id).orElse(null);}
    public Avatar addExperience(Long id,long points){return service.addExperience(id,points);}
    public Avatar registerInteraction(Long id){return service.registerInteraction(id);}
    public boolean delete(Long id){return service.delete(id);}
}
