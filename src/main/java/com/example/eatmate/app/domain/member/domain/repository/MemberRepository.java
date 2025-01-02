package com.example.eatmate.app.domain.member.domain.repository;

import com.example.eatmate.app.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByRefreshToken(String refreshToken);
    boolean existsByNickname(String nickname);
}

