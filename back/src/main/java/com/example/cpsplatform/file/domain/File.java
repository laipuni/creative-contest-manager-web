package com.example.cpsplatform.file.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.problem.domain.Problem;
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
    @GeneratedValue
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

    @Builder
    private File(final String name, final String originalName, final FileExtension extension, final String mimeType,
                final Long size, final String path, final FileType fileType, final Problem problem) {
        this.name = name;
        this.originalName = originalName;
        this.extension = extension;
        this.mimeType = mimeType;
        this.size = size;
        this.path = path;
        this.fileType = fileType;
        this.deleted = false;
        this.problem = problem;
    }

    public static File createContestProblemFile(final String name, final String originalName, final FileExtension extension, final String mimeType,
                                                final Long size, final String path, final FileType fileType, Problem problem){
        if(problem == null){
            //todo 엔티티 생성 위반 예외 만들기(500 or 400)
            throw new IllegalArgumentException("해당 대회 문제 파일은 대회 정보가 필수입니다.");
        }
        return File.builder()
                .name(name)
                .originalName(originalName)
                .extension(extension)
                .mimeType(mimeType)
                .size(size)
                .path(path)
                .fileType(fileType)
                .problem(problem)
                .build();
    }


}
