package com.example.lecture_reservation.lecture.dto;

import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplyHistoryResponseDto {

    private List<History> histories;

    @Getter
    @Setter
    public static class History {
        private int userId;
        private String lectureTitle;
        private LocalDateTime lectureDate;
        private boolean completed;

    }
    @Builder
    public ApplyHistoryResponseDto(List<ApplyHistory> applyHistories) {
        List<History> histories = new ArrayList<>();
        applyHistories.forEach(applyHistory -> {
            History history = new History();
            history.setUserId(applyHistory.getUserId());
            history.setLectureTitle(applyHistory.getLecture().getTitle());
            history.setLectureDate(applyHistory.getLecture().getDate());
            history.setCompleted(applyHistory.isCompleted());
            histories.add(history);
        });

        this.histories = histories;
    }
}
