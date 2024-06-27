package com.example.lecture_reservation.lecture;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepositoryImpl extends JpaRepository<Lecture, Integer>, LectureRepository{


}