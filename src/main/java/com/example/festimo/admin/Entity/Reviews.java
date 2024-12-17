package com.example.festimo.admin.Entity;

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
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Users reviewerId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "reviewee_id")
    private Users revieweeId;

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
