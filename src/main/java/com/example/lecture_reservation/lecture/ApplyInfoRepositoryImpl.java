package com.example.lecture_reservation.lecture;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ApplyInfoRepositoryImpl extends JpaRepository<ApplyInfo, Integer>, ApplyInfoRepository{


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ApplyInfo> findById(Integer lectureId);

    ApplyInfo save(ApplyInfo applyInfo);
}