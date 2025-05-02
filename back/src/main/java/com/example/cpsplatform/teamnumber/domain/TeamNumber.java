package com.example.cpsplatform.teamnumber.domain;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamNumber {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", unique = true, nullable = false)
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
