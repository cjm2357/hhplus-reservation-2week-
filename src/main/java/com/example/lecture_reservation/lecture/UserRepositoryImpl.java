package com.example.lecture_reservation.lecture;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryImpl extends JpaRepository<User, Integer>, UserRepository {


}