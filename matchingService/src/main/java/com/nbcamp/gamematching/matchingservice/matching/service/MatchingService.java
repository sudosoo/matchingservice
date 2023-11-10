package com.nbcamp.gamematching.matchingservice.matching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nbcamp.gamematching.matchingservice.matching.dto.NicknameDto;
import com.nbcamp.gamematching.matchingservice.matching.dto.QueryDto.MatchingResultQueryDto;
import com.nbcamp.gamematching.matchingservice.matching.dto.ResponseUrlInfo;
import com.nbcamp.gamematching.matchingservice.matching.dto.RequestMatching;

import com.nbcamp.gamematching.matchingservice.matching.entity.ResultMatching;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;


public interface MatchingService {

    ResponseUrlInfo matchingJoin(RequestMatching requestMatching) throws JsonProcessingException;
    List<MatchingResultQueryDto> findByMatchingResultMemberNicknameByMemberId(Long memberId);
    List<NicknameDto> findMatchingMembers(Long matchingId, Long memberId);
    ResultMatching findResultMatchingById(Long matchingId);
}
