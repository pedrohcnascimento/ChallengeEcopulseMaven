package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Family;
import br.com.EcoPulse.dto.FamilyDtos;
import br.com.EcoPulse.dto.UserDtos;
import br.com.EcoPulse.service.FamilyService;
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
@RequestMapping("/api/v1/families")
public class FamilyResource {
    private final FamilyService familyService;

    public FamilyResource(FamilyService familyService) {
        this.familyService = familyService;
    }

    @PostMapping
    public ResponseEntity<FamilyDtos.FamilyResponse> create(@Valid @RequestBody FamilyDtos.FamilyRequest request) {
        Family created = familyService.create(new Family(null, request.name(), request.relationshipDescription()));
        return ResponseEntity.status(HttpStatus.CREATED).body(FamilyDtos.FamilyResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<FamilyDtos.FamilyResponse>> list() {
        return ResponseEntity.ok(familyService.getAll().stream().map(FamilyDtos.FamilyResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyDtos.FamilyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(FamilyDtos.FamilyResponse.from(familyService.getRequiredById(id)));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDtos.UserPublicResponse>> listMembers(@PathVariable Long id) {
        return ResponseEntity.ok(familyService.getMembers(id).stream().map(UserDtos.UserPublicResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilyDtos.FamilyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FamilyDtos.FamilyRequest request) {
        Family updated = familyService.update(id, request.name(), request.relationshipDescription());
        return ResponseEntity.ok(FamilyDtos.FamilyResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        familyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
