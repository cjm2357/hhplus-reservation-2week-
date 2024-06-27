package com.example.lecture_reservation.lecture.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ApplyResponseDto {
    private boolean completed;
    @Builder
    public ApplyResponseDto(boolean completed) {
        this.completed = completed;
    }
}
