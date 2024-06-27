package com.example.lecture_reservation.lecture.dto;

import com.example.lecture_reservation.lecture.domain.ApplyInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApplyRequestDto {
    private Integer userId;
    private Integer lectureId;

    public ApplyInfo toEntity() {
        return ApplyInfo.builder()
                .lectureId(lectureId)
                .build();
    }
}
