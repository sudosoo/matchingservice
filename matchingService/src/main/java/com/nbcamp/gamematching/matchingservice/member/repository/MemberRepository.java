package com.nbcamp.gamematching.matchingservice.member.repository;

import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import com.nbcamp.gamematching.matchingservice.member.repository.query.MemberQueryRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {


    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    @NotNull Page<Member> findAll(Pageable pageable);

    Optional<Member> findByProfileNickname(String friendNick);

    Member findByEmailAndProvider(String checkEmail, String checkProvider);
}
