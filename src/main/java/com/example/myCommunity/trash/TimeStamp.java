package com.example.myCommunity.trash;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDateTime;


@Embeddable
@Getter
public class TimeStamp {
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    protected TimeStamp() {}

    public TimeStamp(LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

}
