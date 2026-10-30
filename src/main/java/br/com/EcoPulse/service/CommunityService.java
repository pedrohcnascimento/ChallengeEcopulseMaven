package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Community;
import br.com.EcoPulse.domain.MissionCommunity;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.exception.BusinessConflictException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.CommunityDao;
import br.com.EcoPulse.repository.dao.MissionDao;
import br.com.EcoPulse.repository.dao.UserDao;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityService {
    private final CommunityDao communityDao;
    private final UserDao userDao;
    private final MissionDao missionDao;

    public CommunityService(CommunityDao communityDao, UserDao userDao, MissionDao missionDao) {
        this.communityDao = communityDao;
        this.userDao = userDao;
        this.missionDao = missionDao;
    }

    public Community create(Community community) {
        if (community == null) throw new DomainValidationException("Comunidade obrigatória");
        community.validate();
        if (communityDao.findByName(community.getName()).isPresent()) {
            throw new BusinessConflictException("Já existe uma comunidade com este nome: " + community.getName());
        }
        community.setCreatedAt(Instant.now());
        return communityDao.create(community);
    }

    public List<Community> getAll() {
        return communityDao.findAll();
    }

    public Community getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID da comunidade inválido");
        return communityDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunidade não encontrada: " + id));
    }

    public Community update(Long id, String name, String description, String communityType) {
        Community existing = getRequiredById(id);
        existing.setName(name);
        existing.setDescription(description);
        existing.setCommunityType(communityType);
        existing.validate();
        communityDao.findByName(existing.getName())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new BusinessConflictException("Já existe outra comunidade com este nome"); });
        return communityDao.update(existing);
    }

    public void delete(Long id) {
        getRequiredById(id);
        ConnectionFactory.executeInTransaction(() -> {
            communityDao.deleteAllMembersByCommunity(id);
            communityDao.deleteAllMissionsByCommunity(id);
            communityDao.deleteById(id);
        });
    }

    public void addMember(Long communityId, Long userId) {
        getRequiredById(communityId);
        if (userId == null || userId <= 0 || userDao.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        }
        if (communityDao.isMember(userId, communityId)) {
            throw new BusinessConflictException("O usuário já participa desta comunidade");
        }
        communityDao.addMember(userId, communityId);
    }

    public void removeMember(Long communityId, Long userId) {
        getRequiredById(communityId);
        if (!communityDao.removeMember(userId, communityId)) {
            throw new ResourceNotFoundException("Vínculo entre usuário e comunidade não encontrado");
        }
    }

    public List<User> getMembers(Long communityId) {
        getRequiredById(communityId);
        List<Long> memberIds = communityDao.findMemberIds(communityId);
        List<User> members = new ArrayList<>();
        for (Long id : memberIds) {
            userDao.findById(id).ifPresent(members::add);
        }
        return members;
    }

    public MissionCommunity linkMission(Long communityId, Long missionId, Integer score, String status) {
        getRequiredById(communityId);
        if (missionId == null || missionId <= 0 || missionDao.findById(missionId).isEmpty()) {
            throw new ResourceNotFoundException("Missão não encontrada: " + missionId);
        }
        if (communityDao.findMissionLink(missionId, communityId).isPresent()) {
            throw new BusinessConflictException("A missão já está vinculada a esta comunidade");
        }
        MissionCommunity mc = new MissionCommunity(
                missionId,
                communityId,
                score == null ? 0 : score,
                (status == null || status.isBlank()) ? "ATIVA" : status);
        mc.validate();
        return communityDao.addMissionLink(mc);
    }

    public MissionCommunity updateMissionLink(Long communityId, Long missionId, Integer score, String status) {
        getRequiredById(communityId);
        MissionCommunity existing = communityDao.findMissionLink(missionId, communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo missão-comunidade não encontrado"));
        if (score != null) existing.setScore(score);
        if (status != null && !status.isBlank()) existing.setStatus(status);
        existing.validate();
        return communityDao.updateMissionLink(existing);
    }

    public List<MissionCommunity> getMissions(Long communityId) {
        getRequiredById(communityId);
        return communityDao.findMissionsByCommunity(communityId);
    }

    public void unlinkMission(Long communityId, Long missionId) {
        getRequiredById(communityId);
        if (!communityDao.removeMissionLink(missionId, communityId)) {
            throw new ResourceNotFoundException("Vínculo missão-comunidade não encontrado");
        }
    }
}
