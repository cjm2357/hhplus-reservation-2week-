package com.example.lecture_reservation.lecture.repository;

import com.example.lecture_reservation.lecture.domain.Lecture;

import java.util.List;
import java.util.Optional;

public interface LectureRepository {

    Lecture save(Lecture lecture);

    Optional<Lecture> findById(Integer id);

    List<Lecture> findAll();
}
