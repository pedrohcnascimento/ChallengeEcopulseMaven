package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Streak;
import br.com.EcoPulse.dto.HabitDtos;
import br.com.EcoPulse.service.StreakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/streaks")
public class StreakResource {
    private final StreakService streakService;

    public StreakResource(StreakService streakService) {
        this.streakService = streakService;
    }

    @GetMapping
    public ResponseEntity<List<HabitDtos.StreakResponse>> list(
            @RequestParam(name = "userId", required = false) Long userId) {
        List<Streak> list = userId == null ? streakService.getAll() : streakService.getByUserId(userId);
        return ResponseEntity.ok(list.stream().map(HabitDtos.StreakResponse::from).toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HabitDtos.StreakResponse>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(streakService.getByUserId(userId).stream().map(HabitDtos.StreakResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitDtos.StreakResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(HabitDtos.StreakResponse.from(streakService.getRequiredById(id)));
    }
}
