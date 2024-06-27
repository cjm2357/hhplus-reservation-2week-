package com.example.lecture_reservation.lecture.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;

    private LocalDateTime date;

    private LocalDateTime applyDate;

    private int seat;

}
