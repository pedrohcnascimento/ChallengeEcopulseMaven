package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.Post;
import br.com.EcoPulse.dto.PostDtos;
import br.com.EcoPulse.service.PostService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostResource {
    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDtos.PostResponse> create(@Valid @RequestBody PostDtos.PostCreateRequest request) {
        Post created = postService.create(request.userId(), request.content(), request.status());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PostDtos.PostResponse.from(created, postService.getCounters(created.getId())));
    }

    @GetMapping
    public ResponseEntity<List<PostDtos.PostResponse>> list(
            @RequestParam(name = "userId", required = false) Long userId) {
        List<Post> posts = userId == null ? postService.getAll() : postService.getByUserId(userId);
        List<PostDtos.PostResponse> response = posts.stream()
                .map(p -> PostDtos.PostResponse.from(p, postService.getCounters(p.getId())))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDtos.PostResponse> findById(@PathVariable Long id) {
        Post post = postService.getRequiredById(id);
        return ResponseEntity.ok(PostDtos.PostResponse.from(post, postService.getCounters(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDtos.PostResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PostDtos.PostUpdateRequest request) {
        Post updated = postService.update(id, request.content(), request.status());
        return ResponseEntity.ok(PostDtos.PostResponse.from(updated, postService.getCounters(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/counters")
    public ResponseEntity<PostDtos.PostCounterResponse> getCounters(@PathVariable Long id) {
        return ResponseEntity.ok(PostDtos.PostCounterResponse.from(postService.getCounters(id)));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostDtos.PostCounterResponse> like(@PathVariable Long id) {
        return ResponseEntity.ok(PostDtos.PostCounterResponse.from(postService.registerLike(id)));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<PostDtos.PostCounterResponse> comment(@PathVariable Long id) {
        return ResponseEntity.ok(PostDtos.PostCounterResponse.from(postService.registerComment(id)));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<PostDtos.PostCounterResponse> share(@PathVariable Long id) {
        return ResponseEntity.ok(PostDtos.PostCounterResponse.from(postService.registerShare(id)));
    }
}
