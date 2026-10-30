package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Community;
import br.com.EcoPulse.domain.MissionCommunity;
import br.com.EcoPulse.dto.CommunityDtos;
import br.com.EcoPulse.dto.UserDtos;
import br.com.EcoPulse.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/communities")
public class CommunityResource {
    private final CommunityService communityService;

    public CommunityResource(CommunityService communityService) {
        this.communityService = communityService;
    }

    @PostMapping
    public ResponseEntity<CommunityDtos.CommunityResponse> create(
            @Valid @RequestBody CommunityDtos.CommunityRequest request) {
        Community created = communityService.create(
                new Community(null, request.name(), request.description(), request.communityType(), null));
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityDtos.CommunityResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<CommunityDtos.CommunityResponse>> list() {
        return ResponseEntity.ok(communityService.getAll().stream().map(CommunityDtos.CommunityResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDtos.CommunityResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(CommunityDtos.CommunityResponse.from(communityService.getRequiredById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDtos.CommunityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CommunityDtos.CommunityRequest request) {
        Community updated = communityService.update(id, request.name(), request.description(), request.communityType());
        return ResponseEntity.ok(CommunityDtos.CommunityResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        communityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDtos.UserPublicResponse>> listMembers(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getMembers(id).stream().map(UserDtos.UserPublicResponse::from).toList());
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> addMember(@PathVariable Long id, @PathVariable Long userId) {
        communityService.addMember(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        communityService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/missions")
    public ResponseEntity<List<CommunityDtos.MissionCommunityResponse>> listMissions(@PathVariable Long id) {
        return ResponseEntity.ok(
                communityService.getMissions(id).stream().map(CommunityDtos.MissionCommunityResponse::from).toList());
    }

    @PostMapping("/{id}/missions")
    public ResponseEntity<CommunityDtos.MissionCommunityResponse> linkMission(
            @PathVariable Long id,
            @Valid @RequestBody CommunityDtos.MissionCommunityRequest request) {
        MissionCommunity created = communityService.linkMission(id, request.missionId(), request.score(), request.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommunityDtos.MissionCommunityResponse.from(created));
    }

    @PutMapping("/{id}/missions/{missionId}")
    public ResponseEntity<CommunityDtos.MissionCommunityResponse> updateMissionLink(
            @PathVariable Long id,
            @PathVariable Long missionId,
            @RequestBody CommunityDtos.MissionCommunityRequest request) {
        MissionCommunity updated = communityService.updateMissionLink(id, missionId, request.score(), request.status());
        return ResponseEntity.ok(CommunityDtos.MissionCommunityResponse.from(updated));
    }

    @DeleteMapping("/{id}/missions/{missionId}")
    public ResponseEntity<Void> unlinkMission(@PathVariable Long id, @PathVariable Long missionId) {
        communityService.unlinkMission(id, missionId);
        return ResponseEntity.noContent().build();
    }
}
