package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_id")
    private int attachId;

    // Document와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "doc_id", nullable = false)
    private Document document;

    @Column(name = "attach_url", length = 255, nullable = false)
    private String attachUrl;

    @Column(name = "attach_size")
    private long attachSize;

    @Column(name = "attach_generatedTime", nullable = false)
    private LocalDateTime attachGeneratedTime;

    @Column(name = "attach_changed")
    private LocalDateTime attachChanged;

    @Column(name = "attach_file_type", length = 100, nullable = false)
    private String attachFileType;

}

