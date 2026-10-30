package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Streak {
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Sao_Paulo");

    private Long id;
    private Long userId;
    private String type;
    private Integer currentCount;
    private Integer highestCount;
    private Instant lastActivityAt;
    private Instant createdAt;
    private Instant updatedAt;

    public Streak() {
    }

    public Streak(Long id, Long userId, String type, Integer currentCount, Integer highestCount, Instant lastActivityAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.currentCount = currentCount;
        this.highestCount = highestCount;
        this.lastActivityAt = lastActivityAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCurrentCount() { return currentCount; }
    public void setCurrentCount(Integer currentCount) { this.currentCount = currentCount; }

    public Integer getHighestCount() { return highestCount; }
    public void setHighestCount(Integer highestCount) { this.highestCount = highestCount; }

    public Instant getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public void validateCounts() {
        if (currentCount == null || currentCount < 0) throw new DomainValidationException("Contagem atual de streak não pode ser negativa");
        if (highestCount == null || highestCount < currentCount) throw new DomainValidationException("O recorde (highestCount) deve ser maior ou igual à contagem atual");
    }

    public void registerActivity(Instant activityTime, ZoneId zoneId) {
        ZoneId zone = zoneId == null ? DEFAULT_ZONE : zoneId;
        Instant eventTime = activityTime == null ? Instant.now() : activityTime;
        if (currentCount == null || currentCount < 0) currentCount = 0;
        if (highestCount == null || highestCount < currentCount) highestCount = currentCount;

        if (lastActivityAt == null) {
            currentCount = 1;
        } else {
            LocalDate lastDate = lastActivityAt.atZone(zone).toLocalDate();
            LocalDate currentDate = eventTime.atZone(zone).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(lastDate, currentDate);
            if (daysBetween == 0) {
                if (currentCount == 0) currentCount = 1;
            } else if (daysBetween == 1) {
                currentCount += 1;
            } else if (daysBetween > 1) {
                currentCount = 1;
            }
        }
        if (currentCount > highestCount) highestCount = currentCount;
        if (lastActivityAt == null || eventTime.isAfter(lastActivityAt)) lastActivityAt = eventTime;
        updatedAt = Instant.now();
        validateCounts();
    }
}
