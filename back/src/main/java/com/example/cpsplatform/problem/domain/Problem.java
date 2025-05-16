package com.example.cpsplatform.problem.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.file.domain.File;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(
        name = "problem",uniqueConstraints = {
        //ex) 16회 대회에 초/중 섹션에 1번 문제가 복수일 경우를 유니크 제약 위반
        @UniqueConstraint(name = "uk_problem_contest_section_order", columnNames = {"contest_id", "section", "problem_order"}),
        @UniqueConstraint(name = "uk_problem_title",columnNames = "title")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true,name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "section")
    private Section section;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false)
    private ProblemType problemType;

    @Column(name = "problem_order")
    private Integer problemOrder;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "problem",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> files = new HashSet<>();

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


    public void addFile(final File file) {
        files.add(file);
    }

    public void removeFile(final File file) {
        files.remove(file);
        file.setProblem(null);
    }

    public void updateContestProblem(final String title, final Section section,
                       final String content, final Integer problemOrder){
        this.title = StringUtils.hasText(title) ? title : this.title;
        this.section = section != null ? section : this.section;
        this.content = content;
        this.problemOrder = problemOrder != null ? problemOrder : this.problemOrder;
    }
}
