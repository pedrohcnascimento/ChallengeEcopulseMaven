package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Post;
import br.com.EcoPulse.domain.PostCounter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class PostDtos {
    private PostDtos() { }

    public record PostCreateRequest(
            @NotNull(message = "Autor (userId) é obrigatório")
            Long userId,
            @NotBlank(message = "Conteúdo da publicação é obrigatório")
            @Size(max = 4000)
            String content,
            String status
    ) { }

    public record PostUpdateRequest(
            @NotBlank(message = "Conteúdo da publicação é obrigatório")
            @Size(max = 4000)
            String content,
            @NotBlank(message = "Status da publicação é obrigatório")
            String status
    ) { }

    public record PostCounterResponse(
            Long id,
            Long postId,
            Integer likeCount,
            Integer commentCount,
            Integer shareCount
    ) {
        public static PostCounterResponse from(PostCounter c) {
            if (c == null) return new PostCounterResponse(null, null, 0, 0, 0);
            return new PostCounterResponse(c.getId(), c.getPostId(), c.getLikeCount(), c.getCommentCount(), c.getShareCount());
        }
    }

    public record PostResponse(
            Long id,
            Long userId,
            String content,
            String status,
            PostCounterResponse counters,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static PostResponse from(Post p, PostCounter counter) {
            return new PostResponse(
                    p.getId(), p.getUserId(), p.getContent(), p.getStatus(),
                    PostCounterResponse.from(counter), p.getCreatedAt(), p.getUpdatedAt());
        }
    }
}
