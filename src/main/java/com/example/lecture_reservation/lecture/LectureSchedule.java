package com.example.lecture_reservation.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class LectureSchedule {
    @Id
    @Column(name="LECTURE_ID")
    private int lectureId;

    private LocalDateTime date;

    private LocalDateTime openDate;

    @Builder
    public LectureSchedule(int lectureId, LocalDateTime date, LocalDateTime openDate) {
        this.lectureId = lectureId;
        this.date = date;
        this.openDate = openDate;
    }
}
