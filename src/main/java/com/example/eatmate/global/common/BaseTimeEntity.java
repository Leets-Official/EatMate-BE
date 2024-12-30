package com.example.eatmate.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}