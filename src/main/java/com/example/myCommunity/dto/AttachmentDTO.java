package com.example.myCommunity.dto;

import com.example.myCommunity.domain.Attachment;
import lombok.Data;

@Data
public class AttachmentDTO {
    private Long attachmentId;
    private Long attachSize;
    private String attachUrl;
    private String fileName;

    // Entity에서 DTO로 변환하는 메소드
    public static AttachmentDTO fromEntity(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setAttachmentId(attachment.getAttachmentId());
        dto.setAttachSize(attachment.getFileSize());
        dto.setAttachUrl(attachment.getFilePath());
        dto.setFileName(attachment.getFileName());
        return dto;
    }
}
