package com.example.eatmate.app.domain.member.domain.repository;

import com.example.eatmate.app.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
