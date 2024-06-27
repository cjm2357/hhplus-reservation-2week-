package com.example.lecture_reservation.lecture;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyHistoryRepositoryImpl extends JpaRepository<ApplyHistory, Integer>, ApplyHistoryRepository {

    List<ApplyHistory> findByUserId(Integer userId);

    List<ApplyHistory> findByLectureIdAndCompleted(Integer lectureId, Boolean completed);
}
