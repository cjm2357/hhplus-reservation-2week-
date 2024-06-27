package com.example.lecture_reservation.lecture.repository;

import com.example.lecture_reservation.lecture.domain.ApplyInfo;

import java.util.Optional;

public interface ApplyInfoRepository {

    ApplyInfo save(ApplyInfo applyInfo);

    Optional<ApplyInfo> findById(Integer id);

}
