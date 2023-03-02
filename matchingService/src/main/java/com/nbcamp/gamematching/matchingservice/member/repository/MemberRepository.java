package com.nbcamp.gamematching.matchingservice.member.repository;

import com.nbcamp.gamematching.matchingservice.member.domain.BuddiesMapping;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import java.util.Optional;

import com.nbcamp.gamematching.matchingservice.member.repository.query.MemberQueryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {


    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Member> findByProfileNickname(String friendNick);
}
