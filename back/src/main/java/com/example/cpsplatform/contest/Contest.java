package com.example.cpsplatform.contest;

import com.example.cpsplatform.BaseEntity;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@SQLDelete(sql = "UPDATE Contest SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private boolean deleted;

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
        this.deleted = false;
    }

    public static Contest of(final String title, final String description, final int season, final LocalDateTime registrationStartAt,
                             final LocalDateTime registrationEndAt, final LocalDateTime startTime, final LocalDateTime endTime){
        validRegistrationAt(registrationEndAt,registrationStartAt);
        validStartAndEndAt(startTime, endTime);

        return Contest.builder()
                .title(title)
                .description(description)
                .season(season)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public void updateContest(final String title, final String description, final int season, final LocalDateTime registrationStartAt,
                              final LocalDateTime registrationEndAt, final LocalDateTime startTime, final LocalDateTime endTime){
        validRegistrationAt(registrationEndAt,registrationStartAt);
        validStartAndEndAt(startTime, endTime);

        this.title = StringUtils.isBlank(title) ? this.title : title;
        this.description = StringUtils.isBlank(description) ? this.description : description;
        this.season = season;
        this.registrationStartAt = registrationStartAt == null ? this.registrationStartAt : registrationStartAt;
        this.registrationEndAt = registrationEndAt == null ? this.registrationEndAt : registrationEndAt;
        this.startTime = startTime == null ? this.startTime : startTime;
        this.endTime = endTime == null ? this.endTime : endTime;
    }

    private static void validStartAndEndAt(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("대회 종료 시간은 대회 시작 시간보다 이후여야 합니다.");
        }
    }

    private static void validRegistrationAt(LocalDateTime registrationEndAt,final LocalDateTime registrationStartAt) {
        if (registrationEndAt.isBefore(registrationStartAt)) {
            throw new IllegalArgumentException("접수 종료 기간은 접수 시작 기간보다 이후여야 합니다.");
        }
    }

    public boolean isNotOngoing(final LocalDateTime now){
        return now.isBefore(startTime) || now.isAfter(endTime);
    }

    public boolean isNotRegistering(final LocalDateTime now){
        return now.isBefore(registrationStartAt) || now.isAfter(registrationEndAt);
    }

    public void recover(){
        this.deleted = false;
    }

}
