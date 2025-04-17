package com.example.cpsplatform.problem.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.contest.Contest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        //ex) 16회 대회에 초/중 섹션에 1번 문제가 복수일 경우를 유니크 제약 위반
        uniqueConstraints = @UniqueConstraint(columnNames = {"contest_id", "section", "problem_order"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Enumerated(EnumType.STRING)
    private Section section;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false)
    private ProblemType problemType;

    @Column(name = "problem_order")
    private Integer problemOrder;

    @Builder
    private Problem(final String title, final Contest contest, final Section section,
                   final String content, final ProblemType problemType, final Integer problemOrder) {
        this.title = title;
        this.contest = contest;
        this.section = section;
        this.content = content;
        this.problemType = problemType;
        this.problemOrder = problemOrder;
    }

    public static Problem createContestProblem(final String title, final Contest contest, final Section section,
                                               final String content, final Integer problemOrder){
        validateProblemOrder(problemOrder);
        validateContest(contest);
        return Problem.builder()
                .title(title)
                .content(content)
                .contest(contest)
                .section(section)
                .problemType(ProblemType.CONTEST)
                .problemOrder(problemOrder)
                .build();
    }

    private static void validateProblemOrder(final Integer problemOrder) {
        if(problemOrder == null){
            throw new IllegalArgumentException("출제용 문제는 문제번호가 필수입니다.");
        }
    }

    private static void validateContest(final Contest contest) {
        if(contest == null){
            throw new IllegalArgumentException("출제용 문제는 출제할 대회 정보가 필수입니다.");
        }
    }


}
