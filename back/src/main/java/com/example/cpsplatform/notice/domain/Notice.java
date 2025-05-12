package com.example.cpsplatform.notice.domain;

import com.example.cpsplatform.BaseEntity;
import com.example.cpsplatform.file.domain.File;
import com.example.cpsplatform.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L; // DEFAULT 0, 기본 값은 0으로 설정

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> files = new HashSet<>();

    @Builder
    private Notice(final String title, final String content, final Member writer, final Long viewCount) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.viewCount = viewCount;
    }

    public static Notice of(final String title, final String content, Member member){
        return Notice.builder()
                .title(title)
                .content(content)
                .writer(member)
                .viewCount(0L) // 조회수 0으로 설정
                .build();
    }

    public void addFile(File file){
        files.add(file);
    }

    public void removeFile(File file){
        files.remove(file);
        file.setNotice(null);
    }

    public void clearFile(){
        for (File file : files){
            file.setNotice(null);
        }
        files.clear();
    }

}
