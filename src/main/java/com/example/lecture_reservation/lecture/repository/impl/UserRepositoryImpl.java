package com.example.lecture_reservation.lecture.repository.impl;

import com.example.lecture_reservation.lecture.domain.User;
import com.example.lecture_reservation.lecture.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryImpl extends JpaRepository<User, Integer>, UserRepository {


}