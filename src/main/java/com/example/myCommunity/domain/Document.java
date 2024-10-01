package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private int docId;

    // User와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Board와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Board board;

    @Column(name = "doc_generated", nullable = false)
    private LocalDateTime docGenerated;

    @Column(name = "doc_modified", nullable = false)
    private LocalDateTime docModified;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "liked", nullable = false)
    private int liked;

    @Column(name = "doc_text", length = 1000, nullable = false)
    private String docText;

    // 첨부파일 목록 (1:N)
    @OneToMany(mappedBy = "document")
    private List<Attachment> attachments;

    // 게시글의 댓글 목록 (1:N)
    @OneToMany(mappedBy = "document")
    private List<Comment> comments;
}

