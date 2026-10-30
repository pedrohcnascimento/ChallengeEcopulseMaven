package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.UserReward;
import br.com.EcoPulse.dto.ParticipationDtos;
import br.com.EcoPulse.service.UserRewardService;
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
@RequestMapping("/api/v1/user-rewards")
public class UserRewardResource {
    private final UserRewardService userRewardService;

    public UserRewardResource(UserRewardService userRewardService) {
        this.userRewardService = userRewardService;
    }

    @PostMapping
    public ResponseEntity<ParticipationDtos.UserRewardResponse> acquire(
            @Valid @RequestBody ParticipationDtos.AcquireRewardRequest request) {
        UserReward created = userRewardService.acquire(request.userId(), request.rewardId(), request.sourceMissionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ParticipationDtos.UserRewardResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationDtos.UserRewardResponse>> list(
            @RequestParam(name = "userId", required = false) Long userId) {
        List<UserReward> list = userId == null ? userRewardService.getAll() : userRewardService.getByUserId(userId);
        return ResponseEntity.ok(list.stream().map(ParticipationDtos.UserRewardResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipationDtos.UserRewardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ParticipationDtos.UserRewardResponse.from(userRewardService.getRequiredById(id)));
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<ParticipationDtos.UserRewardResponse> claim(@PathVariable Long id) {
        return ResponseEntity.ok(ParticipationDtos.UserRewardResponse.from(userRewardService.claim(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRewardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
