package com.example.lecture_reservation.lecture.repository.impl;

import com.example.lecture_reservation.lecture.domain.Lecture;
import com.example.lecture_reservation.lecture.repository.LectureRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepositoryImpl extends JpaRepository<Lecture, Integer>, LectureRepository {


}