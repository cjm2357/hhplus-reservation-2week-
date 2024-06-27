package com.example.lecture_reservation.lecture.dto;

import com.example.lecture_reservation.lecture.domain.Lecture;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LectureResponseDto {

    List<Lecture> lectureList;

    @Builder
    public LectureResponseDto(List<Lecture> lectureList) {
        this.lectureList = lectureList;
    }
}
