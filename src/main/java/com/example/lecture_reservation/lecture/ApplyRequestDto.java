package com.example.lecture_reservation.lecture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyRequestDto {

    private Integer userId;
    private Integer lectureId;

    public ApplyInfo toEntity() {
        return ApplyInfo.builder()
                .lectureId(lectureId)
                .build();
    }


}
