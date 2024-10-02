package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "attach_url", length = 255, nullable = false)
    private String attachUrl;

    @Column(name = "attach_size")
    private long attachSize;

    @Embedded
    private TimeStamp timestamp;

    @Column(name = "attach_file_type", length = 100, nullable = false)
    private String attachFileType;

}

