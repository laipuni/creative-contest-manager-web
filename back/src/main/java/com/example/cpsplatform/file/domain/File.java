package com.example.cpsplatform.file.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.teamsolve.domain.TeamSolve;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLDelete(sql = "UPDATE File SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FileExtension extension;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private FileType fileType;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_solve_id")
    private TeamSolve teamSolve;

    @Builder
    private File(final String name, final String originalName, final FileExtension extension, final String mimeType,
                final Long size, final String path, final FileType fileType, final Problem problem, final TeamSolve teamSolve) {
        this.name = name;
        this.originalName = originalName;
        this.extension = extension;
        this.mimeType = mimeType;
        this.size = size;
        this.path = path;
        this.fileType = fileType;
        this.deleted = false;
        this.problem = problem;
        this.teamSolve = teamSolve;
    }

    public static File createContestProblemFile(final String name, final String originalName, final FileExtension extension, final String mimeType,
                                                final Long size, final String path, final FileType fileType, Problem problem){
        if(problem == null){
            //todo 엔티티 생성 위반 예외 만들기(500 or 400)
            throw new IllegalArgumentException("해당 대회 문제 파일은 대회 정보가 필수입니다.");
        }

        File file = File.builder()
                .name(name)
                .originalName(originalName)
                .extension(extension)
                .mimeType(mimeType)
                .size(size)
                .path(path)
                .fileType(fileType)
                .problem(problem)
                .build();
        problem.addFile(file);

        return file;
    }

    public static File createProblemAnswerFile(final String name, final String originalName, final FileExtension extension, final String mimeType,
                                                final Long size, final String path, final FileType fileType, TeamSolve teamSolve){
        if(teamSolve == null){
            //todo 엔티티 생성 위반 예외 만들기(500 or 400)
            throw new IllegalArgumentException("해당 답안지 제출 파일은 제출 정보가 필수입니다.");
        }

        return File.builder()
                .name(name)
                .originalName(originalName)
                .extension(extension)
                .mimeType(mimeType)
                .size(size)
                .path(path)
                .fileType(fileType)
                .teamSolve(teamSolve)
                .build();
    }

    public void setProblem(Problem problem){
        this.problem = problem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File that = (File) o;

        //둘 다 id가 있을 경우만 비교
        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }

        //둘 중 하나라도 id가 없으면 객체 동일성으로 판단
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // Hibernate 권장 방식
    }

}
