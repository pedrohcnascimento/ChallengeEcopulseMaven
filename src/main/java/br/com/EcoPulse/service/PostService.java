package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Post;
import br.com.EcoPulse.domain.PostCounter;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.PostCounterDao;
import br.com.EcoPulse.repository.dao.PostDao;
import br.com.EcoPulse.repository.dao.UserDao;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PostService {
    private final PostDao postDao;
    private final PostCounterDao postCounterDao;
    private final UserDao userDao;

    public PostService(PostDao postDao, PostCounterDao postCounterDao, UserDao userDao) {
        this.postDao = postDao;
        this.postCounterDao = postCounterDao;
        this.userDao = userDao;
    }

    /** Cria a publicação e inicializa seu registro 1:1 em PostCounter na mesma transação. */
    public Post create(Long userId, String content, String status) {
        if (userId == null || userId <= 0 || userDao.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Autor não encontrado: " + userId);
        }
        Instant now = Instant.now();
        String effectiveStatus = (status == null || status.isBlank()) ? Post.STATUS_PUBLICADO : status;
        Post post = new Post(null, userId, content, effectiveStatus, now, now);
        post.validate();

        return ConnectionFactory.executeInTransaction(() -> {
            Post created = postDao.create(post);
            postCounterDao.create(new PostCounter(null, created.getId(), 0, 0, 0));
            return created;
        });
    }

    public List<Post> getAll() {
        return postDao.findAll();
    }

    public List<Post> getByUserId(Long userId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (userDao.findById(userId).isEmpty()) throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        return postDao.findByUserId(userId);
    }

    public Post getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID da publicação inválido");
        return postDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicação não encontrada: " + id));
    }

    public PostCounter getCounters(Long postId) {
        getRequiredById(postId);
        return postCounterDao.findByPostId(postId)
                .orElseGet(() -> postCounterDao.create(new PostCounter(null, postId, 0, 0, 0)));
    }

    public Post update(Long id, String content, String status) {
        Post existing = getRequiredById(id);
        existing.setContent(content);
        existing.setStatus(status);
        existing.setUpdatedAt(Instant.now());
        existing.validate();
        return postDao.update(existing);
    }

    public PostCounter registerLike(Long postId) {
        PostCounter counter = getCounters(postId);
        counter.incrementLikes();
        counter.validate();
        return postCounterDao.update(counter);
    }

    public PostCounter registerComment(Long postId) {
        PostCounter counter = getCounters(postId);
        counter.incrementComments();
        counter.validate();
        return postCounterDao.update(counter);
    }

    public PostCounter registerShare(Long postId) {
        PostCounter counter = getCounters(postId);
        counter.incrementShares();
        counter.validate();
        return postCounterDao.update(counter);
    }

    public void delete(Long id) {
        getRequiredById(id);
        ConnectionFactory.executeInTransaction(() -> {
            postCounterDao.deleteByPostId(id);
            postDao.deleteById(id);
        });
    }
}
