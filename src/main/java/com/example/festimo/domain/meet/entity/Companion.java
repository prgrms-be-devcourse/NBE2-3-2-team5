package com.example.festimo.domain.meet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "companion")
public class Companion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companionId;

    @Column(nullable = false)
    private Long leaderId;

    @Column(nullable = false)
    private LocalDateTime companionDate;
}
