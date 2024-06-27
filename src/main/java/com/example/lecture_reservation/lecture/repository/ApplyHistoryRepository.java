package com.example.lecture_reservation.lecture.repository;

import com.example.lecture_reservation.lecture.domain.ApplyHistory;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyHistoryRepository {

    ApplyHistory save(ApplyHistory applyHistory);

    List<ApplyHistory> findByUserId(Integer userId);

    @Query("SELECT h FROM ApplyHistory h WHERE (h.lecture.id = :lectureId) and (h.completed = :completed)")
    List<ApplyHistory> findByLectureIdAndCompleted(Integer lectureId, Boolean completed);


}
