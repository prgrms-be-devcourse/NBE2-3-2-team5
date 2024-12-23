package com.example.festimo.domain.meet.entity;

import java.time.LocalDateTime;

import com.example.festimo.domain.user.domain.User;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "companion_member")
public class Companion_member {

    @EmbeddedId
    private CompanionMemberId id;

    @ManyToOne
    @MapsId("userId") // EmbeddedId의 userId와 매핑
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;



    @Column(nullable = false)
    private LocalDateTime joinedDate;

}
