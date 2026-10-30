package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.UserMission;
import br.com.EcoPulse.dto.ParticipationDtos;
import br.com.EcoPulse.service.UserMissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participations")
public class UserMissionResource {
    private final UserMissionService userMissionService;

    public UserMissionResource(UserMissionService userMissionService) {
        this.userMissionService = userMissionService;
    }

    @PostMapping
    public ResponseEntity<ParticipationDtos.UserMissionResponse> assign(
            @Valid @RequestBody ParticipationDtos.AssignMissionRequest request) {
        UserMission created = userMissionService.assignMission(request.userId(), request.missionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ParticipationDtos.UserMissionResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationDtos.UserMissionResponse>> list(
            @RequestParam(name = "userId", required = false) Long userId) {
        List<UserMission> list = userId == null ? userMissionService.getAll() : userMissionService.getByUserId(userId);
        return ResponseEntity.ok(list.stream().map(ParticipationDtos.UserMissionResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipationDtos.UserMissionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ParticipationDtos.UserMissionResponse.from(userMissionService.getRequiredById(id)));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<ParticipationDtos.UserMissionResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody ParticipationDtos.ProgressUpdateRequest request) {
        UserMission updated = userMissionService.updateProgress(id, request.progress());
        return ResponseEntity.ok(ParticipationDtos.UserMissionResponse.from(updated));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ParticipationDtos.UserMissionResponse> complete(@PathVariable Long id) {
        UserMission completed = userMissionService.completeMission(id);
        return ResponseEntity.ok(ParticipationDtos.UserMissionResponse.from(completed));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userMissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
