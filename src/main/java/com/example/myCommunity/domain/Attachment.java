package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Attachment {

    @Id
    @GeneratedValue
    @Column(name = "attach_id")
    private Long attachmentId;

    // Document와의 다대일 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String fileName;

    private String filePath;

    private Long fileSize;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Builder
    public Attachment(Post post, String fileName, String filePath, Long fileSize) {
        this.post = post;
        this.fileName= fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

}

