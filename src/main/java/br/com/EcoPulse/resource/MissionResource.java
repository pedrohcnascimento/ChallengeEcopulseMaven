package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.domain.Reward;
import br.com.EcoPulse.dto.MissionDtos;
import br.com.EcoPulse.dto.RewardDtos;
import br.com.EcoPulse.service.MissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missions")
public class MissionResource {
    private final MissionService missionService;

    public MissionResource(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    public ResponseEntity<MissionDtos.MissionResponse> create(@Valid @RequestBody MissionDtos.MissionCreateRequest request) {
        Mission mission = new Mission(
                null,
                request.title(),
                request.description(),
                request.type(),
                request.rules(),
                request.startDate(),
                request.endDate(),
                request.status() == null ? Mission.STATUS_ATIVA : request.status(),
                null,
                null);
        Mission created = missionService.create(mission, request.rewardIds());
        List<Reward> rewards = missionService.getRewardsByMission(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MissionDtos.MissionResponse.from(created, rewards));
    }

    @GetMapping
    public ResponseEntity<List<MissionDtos.MissionResponse>> list(
            @RequestParam(name = "onlyActive", defaultValue = "false") boolean onlyActive,
            @RequestParam(name = "onlyEligible", defaultValue = "false") boolean onlyEligible) {
        List<Mission> missions = onlyEligible
                ? missionService.getEligible()
                : (onlyActive ? missionService.getActive() : missionService.getAll());
        List<MissionDtos.MissionResponse> response = missions.stream()
                .map(m -> MissionDtos.MissionResponse.from(m, missionService.getRewardsByMission(m.getId())))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionDtos.MissionResponse> findById(@PathVariable Long id) {
        Mission mission = missionService.getRequiredById(id);
        return ResponseEntity.ok(MissionDtos.MissionResponse.from(mission, missionService.getRewardsByMission(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionDtos.MissionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MissionDtos.MissionUpdateRequest request) {
        Mission updated = missionService.updateDetails(
                id,
                request.title(),
                request.description(),
                request.type(),
                request.rules(),
                request.startDate(),
                request.endDate());
        return ResponseEntity.ok(MissionDtos.MissionResponse.from(updated, missionService.getRewardsByMission(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MissionDtos.MissionResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody MissionDtos.MissionStatusRequest request) {
        Mission updated = missionService.changeStatus(id, request.status());
        return ResponseEntity.ok(MissionDtos.MissionResponse.from(updated, missionService.getRewardsByMission(id)));
    }

    @GetMapping("/{id}/rewards")
    public ResponseEntity<List<RewardDtos.RewardResponse>> listMissionRewards(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getRewardsByMission(id).stream().map(RewardDtos.RewardResponse::from).toList());
    }

    @PostMapping("/{id}/rewards/{rewardId}")
    public ResponseEntity<List<RewardDtos.RewardResponse>> addReward(
            @PathVariable Long id,
            @PathVariable Long rewardId) {
        List<Reward> rewards = missionService.addRewardToMission(id, rewardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(rewards.stream().map(RewardDtos.RewardResponse::from).toList());
    }

    @DeleteMapping("/{id}/rewards/{rewardId}")
    public ResponseEntity<Void> removeReward(
            @PathVariable Long id,
            @PathVariable Long rewardId) {
        missionService.removeRewardFromMission(id, rewardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        missionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
