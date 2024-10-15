package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue
    @Column(name = "attach_id")
    private Long attachId;

    // Document와의 다대일 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String attachUrl;

    private long attachSize;

    @Embedded
    private TimeStamp timestamp;

    //private String attachFileType;

}

