package org.userservice.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity with common fields for all entities
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // Getters
    @Id
    private Long id;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean deleted = false;
    
    private LocalDateTime deletedAt;
    
    @PrePersist
    private void generateId() {
        if (this.id == null) {
            this.id = SnowflakeIdGenerator.generate();
        }
    }
    
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
    
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
    }
}