package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;

public class PostCounter {
    private Long id;
    private Long postId;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;

    public PostCounter() {
    }

    public PostCounter(Long id, Long postId, Integer likeCount, Integer commentCount, Integer shareCount) {
        this.id = id;
        this.postId = postId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Integer getShareCount() { return shareCount; }
    public void setShareCount(Integer shareCount) { this.shareCount = shareCount; }

    public void validate() {
        if (likeCount == null || likeCount < 0 || commentCount == null || commentCount < 0 || shareCount == null || shareCount < 0) {
            throw new DomainValidationException("Contadores de publicação não podem ser negativos");
        }
    }

    public void incrementLikes() { this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1; }
    public void incrementComments() { this.commentCount = (this.commentCount == null ? 0 : this.commentCount) + 1; }
    public void incrementShares() { this.shareCount = (this.shareCount == null ? 0 : this.shareCount) + 1; }
}
