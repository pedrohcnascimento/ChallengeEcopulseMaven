package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Avatar;
import br.com.EcoPulse.dto.AvatarDtos;
import br.com.EcoPulse.service.AvatarService;
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
@RequestMapping("/api/v1/avatars")
public class AvatarResource {
    private final AvatarService avatarService;

    public AvatarResource(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping
    public ResponseEntity<AvatarDtos.AvatarResponse> create(@Valid @RequestBody AvatarDtos.AvatarCreateRequest request) {
        Avatar avatar = new Avatar();
        avatar.setUserId(request.userId());
        avatar.setName(request.name());
        avatar.setVisualConfig(request.visualConfig());
        avatar.setPersonalityType(request.personalityType());
        Avatar created = avatarService.create(avatar);
        return ResponseEntity.status(HttpStatus.CREATED).body(AvatarDtos.AvatarResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<AvatarDtos.AvatarResponse>> list(
            @RequestParam(name = "userId", required = false) Long userId) {
        List<Avatar> avatars = userId == null ? avatarService.getAll() : avatarService.getByUserId(userId);
        return ResponseEntity.ok(avatars.stream().map(AvatarDtos.AvatarResponse::from).toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AvatarDtos.AvatarResponse>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(avatarService.getByUserId(userId).stream().map(AvatarDtos.AvatarResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvatarDtos.AvatarResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(AvatarDtos.AvatarResponse.from(avatarService.getRequiredById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvatarDtos.AvatarResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AvatarDtos.AvatarUpdateRequest request) {
        Avatar updated = avatarService.updateCustomization(id, request.name(), request.visualConfig(), request.personalityType());
        return ResponseEntity.ok(AvatarDtos.AvatarResponse.from(updated));
    }

    @PatchMapping("/{id}/experience")
    public ResponseEntity<AvatarDtos.AvatarResponse> addExperience(
            @PathVariable Long id,
            @Valid @RequestBody AvatarDtos.ExperienceRequest request) {
        Avatar updated = avatarService.addExperience(id, request.points());
        return ResponseEntity.ok(AvatarDtos.AvatarResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        avatarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
