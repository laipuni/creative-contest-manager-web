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
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "notice")
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

    public void modify(final String title, final String content) {
        this.title = StringUtils.hasText(title) ? title : this.title;
        this.content = StringUtils.hasText(content) ? content : this.content;
    }
}
