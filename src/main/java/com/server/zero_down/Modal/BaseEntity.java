package com.server.zero_down.Modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
public abstract class BaseEntity {

    @Id
    @Column(length = 12, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            // Generate 12-character alphanumeric UUID
            this.id = UUID.randomUUID()
                    .toString()
                    .replace("-", "")   // remove hyphens
                    .substring(0, 12)   // take first 12 chars
                    .toUpperCase();     // optional: keep uppercase
        }

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
