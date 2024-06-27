package com.example.lecture_reservation.lecture;

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
    private int lectureId;
    private LocalDateTime applyTime;
    private boolean completed;


    @Builder
    public ApplyHistory(int id, int lectureId, int userId, LocalDateTime applyTime, boolean completed) {
        this.id = id;
        this.lectureId = lectureId;
        this.userId = userId;
        this.applyTime = applyTime;
        this.completed = completed;
    }
}

