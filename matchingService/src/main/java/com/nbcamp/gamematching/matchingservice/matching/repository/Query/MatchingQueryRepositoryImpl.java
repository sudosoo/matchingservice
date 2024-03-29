package com.nbcamp.gamematching.matchingservice.matching.repository.Query;

import com.nbcamp.gamematching.matchingservice.matching.dto.QueryDto.MatchingResultQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.nbcamp.gamematching.matchingservice.matching.entity.QMatchingLog.matchingLog;
import static com.nbcamp.gamematching.matchingservice.matching.entity.QResultMatching.resultMatching;
import static com.nbcamp.gamematching.matchingservice.member.entity.QMember.member;


@RequiredArgsConstructor
public class MatchingQueryRepositoryImpl implements MatchingQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public List<MatchingResultQueryDto> findByMatchingResultByMemberId(Long memberId) {
        List<MatchingResultQueryDto> matching = queryFactory.select(Projections.constructor(
                        MatchingResultQueryDto.class,
                        matchingLog.resultMatching.id,
                        matchingLog.resultMatching.playMode,
                        matchingLog.resultMatching.gameInfo,
                        matchingLog.resultMatching.discordUrl)
                )
                .from(matchingLog)
                .join(resultMatching).on(matchingLog.resultMatching.id.eq(resultMatching.id))
                .where(matchingLog.member.id.eq(memberId))
                .fetch();

        List<MatchingResultQueryDto> matchingResultList = new ArrayList<>();
        for (MatchingResultQueryDto matchingResultQueryDto : matching) {
            Long matchingId = matchingResultQueryDto.getResultId();
            List<String> membersNickname = queryFactory.select(member.profile.nickname).from(member)
                    .join(matchingLog).on(member.id.eq(matchingLog.member.id))
                    .join(matchingLog.resultMatching, resultMatching)
                    .where(resultMatching.id.eq(matchingId))
                    .fetch();
            matchingResultQueryDto.addMembers(membersNickname);
            matchingResultList.add(matchingResultQueryDto);
        }
        return matchingResultList;
    }


}