package com.example.festimo.domain.meet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Applications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @Column( nullable = false)
    private Long userId;

    @Column( nullable = false)
    private Long companyId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // 기본값은 'PENDING'

    @Column(nullable = false)
    private LocalDateTime appliedDate = LocalDateTime.now();

    public Applications(Long userId, Long companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }


    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

}
