package com.example.cpsplatform.teamnumber.domain;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "team_number",uniqueConstraints =
        @UniqueConstraint(name = "uk_team_number_contestid",columnNames = "contest_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(nullable = false)
    private Integer lastTeamNumber;

    @Builder
    private TeamNumber(final Contest contest, final Integer lastTeamNumber){
        this.contest = contest;
        this.lastTeamNumber = lastTeamNumber;
    }

    public static TeamNumber of(final Contest contest, final Integer lastTeamNumber){
        return TeamNumber.builder()
                .contest(contest)
                .lastTeamNumber(lastTeamNumber)
                .build();
    }

    public String getNextTeamNumber(){
        this.lastTeamNumber += 1;
        return String.format("%03d", this.lastTeamNumber);
    }
}
