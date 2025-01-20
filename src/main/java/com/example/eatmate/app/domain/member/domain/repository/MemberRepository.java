package com.example.eatmate.app.domain.member.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.eatmate.app.domain.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	Optional<Member> findByRefreshToken(String refreshToken);

	boolean existsByNickname(String nickname);

	boolean existsByPhoneNumber(String phoneNumber);

	boolean existsByStudentNumber(Long studentNumber);

	@Query("SELECT m FROM Member m LEFT JOIN FETCH m.profileImage WHERE m.email = :email")
	Optional<Member> findByEmailWithProfileImage(@Param("email") String email);

	@Query("SELECT m FROM Member m LEFT JOIN FETCH m.profileImage WHERE m.memberId = :memberId")
	Optional<Member> findByIdWithProfileImage(@Param("memberId") Long id);
}

