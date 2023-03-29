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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public ResponseUrlInfo matchingJoin(RequestMatching request, HttpServletRequest servletRequest)
            throws JsonProcessingException {
        var matchingQuota = Long.valueOf(request.getMemberNumbers());
        var matchingRoomCapacity = redisService.waitingUserCountAndRedisConnectByRedis(request.getKey());
        log.info(" 현재 방 입장 인원 =={ }==",matchingRoomCapacity.toString());
        if (matchingRoomCapacity < matchingQuota - 1) {
            redisService.machedEnterByRedis(request.getKey(), request);
            var topicName = createTopicnName(request);
            return new ResponseUrlInfo(request,topicName);
        }

        //매칭 정원이 찻을 경우
        redisService.machedEnterByRedis(request.getKey(), request);
        var resultMemberList =
                redisService.getMatchingMemberByRedis(request.getKey(), matchingQuota, RequestMatching.class);

        List<Member> members = resultMemberList.stream()
                .map(o -> memberService.responseMemberByMemberEmail(o.getMemberEmail())).toList();

        Optional<String> resultUrl = discordService.createChannel(resultMemberList.get(0).getGameMode(),
                Integer.parseInt(resultMemberList.get(0).getMemberNumbers()));

        var url = validateUrl(resultUrl);
        
        var topicName = resultMemberList.get(0).getMemberEmail();
        var resultMatching = ResultMatching.builder()
                .gameInfo(resultMemberList.get(0).getKey())
                .playMode(resultMemberList.get(0).getGameMode())
                .discordUrl(url)
                .build();
        resultMatchingRepository.saveAndFlush(resultMatching);

        for (int i = 0; i < resultMemberList.size(); i++) {
            var resultMember = members.get(i);
            MatchingLog matchingLog = new MatchingLog(resultMatching, resultMember);
            matchingLogRepository.saveAndFlush(matchingLog);
            matchingLog.addMatchingLogToMember(resultMember);
        }

        var currentmatchingId = resultMatchingRepository.findFirstByDiscordUrl(url)
                .orElseThrow(NotFoundMatchingException::new);

        return ResponseUrlInfo.builder()
                .matchingId(currentmatchingId.getId())
                .member(request)
                .topicName(topicName)
                .url(url).build();
    }

    public List<MatchingResultQueryDto> findByMatchingResultMemberNicknameByMemberId(Long id) {
        return matchingLogRepository.findByMatchingResultMemberNicknameByMemberId(id);
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
    private String createTopicnName(RequestMatching request) throws JsonProcessingException {
        var topicName = "";
        var topicNameSelector
                = redisService.findByFirstJoinUserByRedis(request.getKey(), RequestMatching.class);
        topicName = topicNameSelector.getMemberEmail();
        return topicName;
    }
    private String validateUrl(Optional<String> resultUrl) {
        String url = "";
        if (resultUrl.isPresent()) {
            url = resultUrl.get();
        } else {
            throw new NotFoundMatchingException();
        }
        return url;
    }
}
