package com.example.cpsplatform.certificate.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "team_id","preliminary_contest_id","certificate_type"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certificate extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "certificate_title",nullable = false)
    private String title;

    @Column(nullable = false)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "certificate_type")
    private CertificateType certificateType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name = "team_id")
    private Team team;

    //예선 참가 확인서 필드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preliminary_contest_id")
    private Contest contest;
    @Builder
    public Certificate(final String serialNumber, final CertificateType certificateType,final String title, final Member member, final Team team, final Contest contest) {
        this.serialNumber = serialNumber;
        this.certificateType = certificateType;
        this.member = member;
        this.team = team;
        this.title = title;
        this.contest = contest;
    }

    public static Certificate createPreliminaryCertificate(final String serialNumber, final Contest contest, final Member member, final Team team){
        return Certificate.builder()
                .title(getPreliminaryCertificateTitle(contest.getSeason()))
                .certificateType(CertificateType.PRELIMINARY)
                .member(member)
                .team(team)
                .contest(contest)
                .serialNumber(serialNumber)
                .build();
    }

    private static String getPreliminaryCertificateTitle(final int season){
        return String.format("%d회 예선 참가 확인증",season);
    }
}
