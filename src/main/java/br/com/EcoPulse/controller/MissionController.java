package br.com.EcoPulse.controller;

import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.service.MissionService;
import java.time.Instant;
import java.util.List;

public class MissionController {
    private final MissionService service = new MissionService();
    public Mission create(Mission mission, List<Long> rewardIds) { return service.create(mission, rewardIds); }
    public List<Mission> list() { return service.getAll(); }
    public List<Mission> listActive() { return service.getActive(); }
    public Mission find(Long id) { return service.findById(id).orElse(null); }
    public Mission updateDetails(Long id, String title, String description, String type, String rules, Instant startDate, Instant endDate) {
        return service.updateDetails(id, title, description, type, rules, startDate, endDate);
    }
    public Mission changeStatus(Long id, boolean active) { return service.changeStatus(id, active); }
    public boolean delete(Long id) { return service.delete(id); }
}
