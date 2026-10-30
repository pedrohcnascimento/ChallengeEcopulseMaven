package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Activity;
import br.com.EcoPulse.dto.HabitDtos;
import br.com.EcoPulse.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityResource {
    private final ActivityService activityService;

    public ActivityResource(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<HabitDtos.ActivityResponse> register(
            @Valid @RequestBody HabitDtos.ActivityCreateRequest request) {
        Activity activity = new Activity(null, request.userId(), request.type(), request.timestamp(), request.metadata());
        Activity created = activityService.register(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(HabitDtos.ActivityResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<HabitDtos.ActivityResponse>> list(
            @RequestParam(name = "userId", required = false) Long userId) {
        List<Activity> list = userId == null ? activityService.getAll() : activityService.getByUserId(userId);
        return ResponseEntity.ok(list.stream().map(HabitDtos.ActivityResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitDtos.ActivityResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(HabitDtos.ActivityResponse.from(activityService.getRequiredById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
