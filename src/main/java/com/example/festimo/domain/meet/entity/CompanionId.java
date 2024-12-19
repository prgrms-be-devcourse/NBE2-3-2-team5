package com.example.festimo.domain.meet.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanionId implements Serializable {

    private Long userId;
    private Long companionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanionId that = (CompanionId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(companionId, that.companionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, companionId);
    }
}
