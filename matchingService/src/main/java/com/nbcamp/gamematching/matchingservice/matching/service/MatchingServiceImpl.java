package com.nbcamp.gamematching.matchingservice.matching.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nbcamp.gamematching.matchingservice.discord.service.DiscordService;
import com.nbcamp.gamematching.matchingservice.exception.NotFoundException.NotFoundMatchingException;
import com.nbcamp.gamematching.matchingservice.matching.dto.NicknameDto;
import com.nbcamp.gamematching.matchingservice.matching.dto.QueryDto.MatchingResultQueryDto;
import com.nbcamp.gamematching.matchingservice.matching.dto.RequestMatching;
import com.nbcamp.gamematching.matchingservice.matching.dto.ResponseUrlInfo;
import com.nbcamp.gamematching.matchingservice.matching.entity.MatchingLog;
import com.nbcamp.gamematching.matchingservice.matching.entity.ResultMatching;
import com.nbcamp.gamematching.matchingservice.matching.repository.MatchingLogRepository;
import com.nbcamp.gamematching.matchingservice.matching.repository.ResultMatchingRepository;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import com.nbcamp.gamematching.matchingservice.member.service.MemberService;
import com.nbcamp.gamematching.matchingservice.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Long.parseLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {
    private final DiscordService discordService;
    private final ResultMatchingRepository resultMatchingRepository;
    private final MatchingLogRepository matchingLogRepository;
    private final MemberService memberService;
    private final RedisService redisService;

    @Transactional
    public ResponseUrlInfo matchingJoin(RequestMatching request)
            throws JsonProcessingException {
        //방 현재 인원 체크
        long matchingQuota = parseLong(request.getMemberNumber());
        var matchingRoomCapacity = redisService.waitingUserCountAndRedisConnectByRedis(request.getKey());
        log.info("현재 방 입장 인원 =={}==",matchingRoomCapacity.toString());

        //매칭 정원이 차지 않았을 경우
        if (matchingRoomCapacity < matchingQuota - 1) {
            redisService.machedEnterByRedis(request.getKey(), request);
            var topicNameSelector
                    = redisService.findByFirstJoinUserByRedis(request.getKey(), RequestMatching.class);
            String topicName = topicNameSelector.getMemberEmail();
            return new ResponseUrlInfo(request,topicName);
        }

        //매칭 정원이 찻을 경우
        redisService.machedEnterByRedis(request.getKey(), request);
        var resultMemberList =
                redisService.getMatchingMemberByRedis(request.getKey(), matchingQuota, RequestMatching.class);

        List<Member> members = resultMemberList.stream()
                .map(o -> memberService.responseMemberByMemberEmail(o.getMemberEmail())).toList();

        String resultUrl = discordService.createChannel(resultMemberList.get(0).getGameMode(),
                Integer.parseInt(resultMemberList.get(0).getMemberNumber())).orElseThrow(() -> new IllegalArgumentException("url을 찾을 수 없습니다."));

        var topicName = resultMemberList.get(0).getMemberEmail();
        var resultMatching = ResultMatching.builder()
                .gameInfo(resultMemberList.get(0).getKey())
                .playMode(resultMemberList.get(0).getGameMode())
                .discordUrl(resultUrl)
                .build();
        resultMatchingRepository.save(resultMatching);

        for (int i = 0; i < resultMemberList.size(); i++) {
            var resultMember = members.get(i);
            MatchingLog matchingLog = new MatchingLog(resultMatching, resultMember);
            matchingLogRepository.saveAndFlush(matchingLog);
            matchingLog.addMatchingLogToMember(resultMember);
        }

        var currentmatchingId= resultMatchingRepository.findFirstByDiscordUrl(resultUrl)
                .orElseThrow(NotFoundMatchingException::new);
        return ResponseUrlInfo.builder()
                .matchingId(currentmatchingId.getId())
                .member(request)
                .topicName(topicName)
                .url(resultUrl).build();
    }

    @Override
    public List<MatchingResultQueryDto> findByMatchingResultMemberNicknameByMemberId(Long id) {
        return matchingLogRepository.findByMatchingResultByMemberId(id);
    }

    @Override
    public List<NicknameDto> findMatchingMembers(Long matchingId, Long memberId) {
        ResultMatching resultMatching = resultMatchingRepository.findById(matchingId)
                .orElseThrow(NotFoundMatchingException::new);
        List<MatchingLog> matchingLogs = matchingLogRepository.findAllByResultMatching(
                resultMatching);
        return memberService.findNicknamesInMatching(matchingLogs, memberId);
    }

    @Override
    public ResultMatching findResultMatchingById(Long matchingId) {
        return resultMatchingRepository.findById(matchingId)
                .orElseThrow(NotFoundMatchingException::new);
    }
}
