package com.example.lecture_reservation.lecture.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApplyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int userId;
    private LocalDateTime applyTime;
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "lecture_id", referencedColumnName = "id")
    private Lecture lecture;

    @Builder
    public ApplyHistory(int id, Lecture lecture, int userId, LocalDateTime applyTime, boolean completed) {
        this.id = id;
        this.lecture = lecture;
        this.userId = userId;
        this.applyTime = applyTime;
        this.completed = completed;
    }
}

