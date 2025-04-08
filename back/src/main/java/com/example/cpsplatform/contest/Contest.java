package com.example.cpsplatform.contest;

import com.example.cpsplatform.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contest extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false,unique = true)
    private int season;

    @Column(nullable = false,name = "registration_start_at")
    private LocalDateTime registrationStartAt;

    @Column(nullable = false,name = "registration_end_at")
    private LocalDateTime registrationEndAt;

    @Column(nullable = false,name = "start_date")
    private LocalDateTime startTime;

    @Column(nullable = false,name = "end_date")
    private LocalDateTime endTime;

    @Builder
    private Contest(final String title, final String description, final int season, final LocalDateTime registrationStartAt,
                   final LocalDateTime registrationEndAt, final LocalDateTime startTime, final LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.season = season;
        this.registrationStartAt = registrationStartAt;
        this.registrationEndAt = registrationEndAt;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
