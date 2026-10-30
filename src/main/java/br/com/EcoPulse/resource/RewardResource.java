package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Reward;
import br.com.EcoPulse.dto.RewardDtos;
import br.com.EcoPulse.service.RewardService;
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
@RequestMapping("/api/v1/rewards")
public class RewardResource {
    private final RewardService rewardService;

    public RewardResource(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping
    public ResponseEntity<RewardDtos.RewardResponse> create(@Valid @RequestBody RewardDtos.RewardRequest request) {
        Reward reward = new Reward(null, request.name(), request.description(), request.type(), request.value(), request.details());
        return ResponseEntity.status(HttpStatus.CREATED).body(RewardDtos.RewardResponse.from(rewardService.create(reward)));
    }

    @GetMapping
    public ResponseEntity<List<RewardDtos.RewardResponse>> list() {
        return ResponseEntity.ok(rewardService.getAll().stream().map(RewardDtos.RewardResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RewardDtos.RewardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(RewardDtos.RewardResponse.from(rewardService.getRequiredById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RewardDtos.RewardResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RewardDtos.RewardRequest request) {
        Reward updated = new Reward(id, request.name(), request.description(), request.type(), request.value(), request.details());
        return ResponseEntity.ok(RewardDtos.RewardResponse.from(rewardService.update(id, updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rewardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
