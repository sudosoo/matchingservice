package com.nbcamp.gamematching.matchingservice.matching.repository.Query;

import com.nbcamp.gamematching.matchingservice.matching.dto.QueryDto.MatchingResultQueryDto;

import java.util.List;
import java.util.Optional;

public interface MatchingQueryRepository{
    List<MatchingResultQueryDto> findByMatchingResultMemberNicknameByMemberId(Long memberId);

}
