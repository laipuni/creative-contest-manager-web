package com.example.cpsplatform.contest;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.finalcontest.FinalContest;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
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
@Table(name = "contest", uniqueConstraints = {
        @UniqueConstraint(name = "uk_contest_season", columnNames = {"season"})
})
@SQLDelete(sql = "UPDATE contest SET deleted = true WHERE id=?")
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

    @Column(nullable = false)
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

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "final_contest_id")
    private FinalContest finalContest;

    @Builder
    private Contest(final String title, final String description, final int season, final LocalDateTime registrationStartAt,
                    final LocalDateTime registrationEndAt, final LocalDateTime startTime, final LocalDateTime endTime,final boolean deleted,
                    final FinalContest finalContest) {
        this.title = title;
        this.description = description;
        this.season = season;
        this.registrationStartAt = registrationStartAt;
        this.registrationEndAt = registrationEndAt;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deleted = deleted;
        this.finalContest = finalContest;
    }

    public static Contest of(final String title, final String description, final int season, final LocalDateTime registrationStartAt,
                             final LocalDateTime registrationEndAt, final LocalDateTime startTime, final LocalDateTime endTime,
                             final FinalContest finalContest) {

        // 접수 시작 < 접수 마감
        validRegistrationAt(registrationEndAt, registrationStartAt);
        // 접수 마감 < 대회 시작
        validRegistrationEndAndStartTimeAt(registrationEndAt, startTime);
        // 대회 시작 < 대회 종료
        validStartAndEndAt(startTime, endTime);

        // 접수 시작 < 접수 마감 < 대회 시작 < 대회 종료 순으로 시간 설정이 되어야 한다.

        return Contest.builder()
                .title(title)
                .description(description)
                .season(season)
                .registrationStartAt(registrationStartAt)
                .registrationEndAt(registrationEndAt)
                .startTime(startTime)
                .endTime(endTime)
                .finalContest(finalContest)
                .deleted(false)
                .build();
    }

    public void updateContest(final String title, final String description, final int season, final LocalDateTime registrationStartAt,
                              final LocalDateTime registrationEndAt, final LocalDateTime startTime, final LocalDateTime endTime) {

        // 접수 시작 < 접수 마감
        validRegistrationAt(registrationEndAt, registrationStartAt);
        // 접수 마감 < 대회 시작
        validRegistrationEndAndStartTimeAt(registrationEndAt, startTime);
        // 대회 시작 < 대회 종료
        validStartAndEndAt(startTime, endTime);

        // 접수 시작 < 접수 마감 < 대회 시작 < 대회 종료 순으로 시간 설정이 되어야 한다.

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
            throw new IllegalArgumentException("대회 종료 시간은 대회 시작 시간 이후여야 합니다.");
        }
    }

    private static void validRegistrationAt(LocalDateTime registrationEndAt, final LocalDateTime registrationStartAt) {
        if (registrationEndAt.isBefore(registrationStartAt)) {
            throw new IllegalArgumentException("접수 종료 시간은 접수 시작 시간 이후여야 합니다.");
        }
    }

    private static void validRegistrationEndAndStartTimeAt(LocalDateTime registrationEndAt, final LocalDateTime startTime) {
        if (startTime.isBefore(registrationEndAt)) {
            throw new IllegalArgumentException("대회 시작 시간은 접수 마감 시간 이후여야 합니다.");
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
