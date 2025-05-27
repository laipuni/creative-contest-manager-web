package com.example.cpsplatform.team.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.member.domain.organization.Organization;
import com.example.cpsplatform.member.domain.organization.school.School;
import com.example.cpsplatform.member.domain.organization.school.StudentType;
import com.example.cpsplatform.problem.domain.Section;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "team",uniqueConstraints = {
    @UniqueConstraint(name = "uk_team_contestid_number",columnNames = {"contest_id", "team_number"}),
    @UniqueConstraint(name = "uk_contestid_team_name",columnNames = {"name","contest_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, name = "name")
    private String name;

    @Column(nullable = false)
    private Boolean winner = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(name = "team_number", nullable = false)
    private String teamNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Division division;

    @Enumerated(EnumType.STRING)
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(name = "submit_status",nullable = false)
    private SubmitStatus status;

    @Column(name = "final_submit_count")
    private int finalSubmitCount;

    @Builder
    private Team(final String name, final Boolean winner, final Member leader,
                 final Contest contest, final String teamNumber, final Section section,
                 final Division division,final int finalSubmitCount, final SubmitStatus status){
        this.name = name;
        this.winner = winner;
        this.leader = leader;
        this.contest = contest;
        this.teamNumber = teamNumber;
        this.division = division;
        this.section = section;
        this.finalSubmitCount = finalSubmitCount;
        this.status = status;
    }

    public static Team of(final String name, final Boolean winner, final Member leader,
                          final Contest contest, final String teamNumber){

        Section section = determineSection(leader);
        Division division = determineDivision(leader);

        return Team.builder()
                .name(name)
                .winner(winner)
                .leader(leader)
                .contest(contest)
                .teamNumber(teamNumber)
                .section(section)
                .division(division)
                .status(SubmitStatus.NOT_SUBMITTED)
                .build();
    }

    //팀이 받는 문제 난이도를 팀장을 기준으로 정한다.
    private static Section determineSection(Member leader) {
        Organization organization = leader.getOrganization();

        if (organization instanceof School school) {
            StudentType studentType = school.getStudentType();

            if (studentType == StudentType.ELEMENTARY || studentType == StudentType.MIDDLE) {
                return Section.ELEMENTARY_MIDDLE;
            } else if (studentType == StudentType.HIGH) {
                return Section.HIGH_NORMAL;
            }
        }
        return Section.HIGH_NORMAL;
    }

    //팀이 받는 부문을 팀장을 기준으로 정한다.
    private static Division determineDivision(Member leader) {
        Organization organization = leader.getOrganization();

        if (organization instanceof School school) {
            StudentType studentType = school.getStudentType();
            if (studentType.equals(StudentType.ELEMENTARY)) {
                return Division.ELEMENTARY;
            } else if (studentType.equals(StudentType.MIDDLE)) {
                return Division.MIDDLE;
            } else if (studentType.equals(StudentType.HIGH)) {
                return Division.HIGH;
            }else if (studentType.equals(StudentType.COLLEGE)) {
                return Division.COLLEGE_GENERAL;
            }
        }

        return Division.COLLEGE_GENERAL;
    }

    public boolean isWinner(){
        return this.winner;
    }

    public void updateTeamName(String newName) {
        if (newName != null && !newName.isBlank()) {
            this.name = newName;
        }
    }

    public void isNotTeamLeader(Team team, String leaderId) {
        if (!team.getLeader().getLoginId().equals(leaderId)) {
            throw new IllegalArgumentException("팀장만 수정 또는 삭제할 수 있습니다.");
        }
    }

    public void changeWinner(boolean isWinner){
        this.winner = isWinner;
    }

    public void finalSubmit() {
        if(isNotFinalSubmit()){
            //최종 제출이 아닐 경우 최종 제출로 변경
            this.status = SubmitStatus.FINAL;
        } else{
            //최종 제출이면 팀의 제출 횟수를 증가
            increaseFinalSubmitCount();
        }
    }

    public void temporarySubmit() {
        this.status = SubmitStatus.TEMPORARY;
    }

    public void increaseFinalSubmitCount() {
        this.finalSubmitCount++;
    }

    public boolean isNotFinalSubmit() {
        return !this.status.equals(SubmitStatus.FINAL);
    }
}
