package com.example.lecture_reservation.lecture;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplyResponseDto {

    private boolean completed;
    @Builder
    public ApplyResponseDto(boolean completed) {
        this.completed = completed;
    }
}
