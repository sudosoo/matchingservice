package com.nbcamp.gamematching.matchingservice.matching.entity;

import com.nbcamp.gamematching.matchingservice.matching.domain.MemberLog;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultMatching_id")
    private ResultMatching resultMatching;

    private Boolean evaluation = false;


    public MatchingLog(ResultMatching resultMatching, Member member) {
        this.resultMatching = resultMatching;
        this.member = member;
    }

    public void changeEvaluation() {
        this.evaluation = true;
    }

    public MemberLog getMemberIdAndNickname() {
        return new MemberLog(this.getMember().getId(),
                this.getMember().getProfile().getNickname());
    }

    public String getMemberNickname() {
        return this.getMember().getProfile().getNickname();
    }

    public void addMatchingLogToMember(Member resultMember) {
        resultMember.getMatchingLogs().add(this);
    }
}
