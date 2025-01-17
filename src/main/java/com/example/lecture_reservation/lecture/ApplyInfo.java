package com.example.lecture_reservation.lecture;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ApplyInfo {

    @Id
    @Column(name="LECTURE_ID")
    private int lectureId;

    private int userCnt;

    @Builder
    public ApplyInfo(int lectureId, int userCnt) {
        this.lectureId = lectureId;
        this.userCnt = userCnt;
    }
}
