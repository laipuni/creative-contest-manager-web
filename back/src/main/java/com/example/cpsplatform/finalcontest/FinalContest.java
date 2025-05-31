package com.example.cpsplatform.finalcontest;

import com.example.cpsplatform.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinalContest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Builder
    private FinalContest(final String title,
                         final String location,
                         final LocalDateTime startTime,
                         final LocalDateTime endTime) {
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public static FinalContest of(final String title, final String location, final LocalDateTime startTime, final LocalDateTime endTime){
        return FinalContest.builder()
                .title(title)
                .location(location)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }


    public void update(final String title, final String location, final LocalDateTime startTime, final LocalDateTime endTime) {
        this.title = StringUtils.hasText(title) ? title : "미정";
        this.location = StringUtils.hasText(location) ? location : "미정";
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
