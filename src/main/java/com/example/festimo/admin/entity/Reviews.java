package com.example.festimo.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter

public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @NotNull
    @JoinColumn(name = "reviewer_id")
    private Long reviewerId;

    @NotNull
    @JoinColumn(name = "reviewee_id")
    private Long revieweeId;

    @NotNull
    @Column(name = "rating")
    private String rating;  //db에서는 enum

    @NotNull
    private String content;

    @NotNull
    private LocalDate createdAt;

    @NotNull
    private LocalDate updatedAt;


}
