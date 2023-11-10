package com.nbcamp.gamematching.matchingservice.matching.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nbcamp.gamematching.matchingservice.matching.dto.RequestMatching;
import com.nbcamp.gamematching.matchingservice.matching.entity.MatchingLog;
import com.nbcamp.gamematching.matchingservice.matching.entity.ResultMatching;
import com.nbcamp.gamematching.matchingservice.member.domain.GameType;
import com.nbcamp.gamematching.matchingservice.member.domain.MemberRoleEnum;
import com.nbcamp.gamematching.matchingservice.member.domain.Tier;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import com.nbcamp.gamematching.matchingservice.member.entity.Profile;
import com.nbcamp.gamematching.matchingservice.member.service.MemberServiceImpl;
import com.nbcamp.gamematching.matchingservice.redis.RedisService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@DirtiesContext
@Transactional
public class MatchingServiceImplTest {
    @Mock
    private MemberServiceImpl memberService;
    @Mock
    private MatchingServiceImpl matchingService;
    private RequestMatching request1;
    private RequestMatching request2;

    private Member member1;
    private Member member2;
    private ResultMatching resultMatching;
    @Autowired
    private RedisService redisService;
    private static RedisServer redisServer;

    @BeforeAll
    public static void start() {
        redisServer = new RedisServer(6378);
        redisServer.start();
    }
    @AfterAll
    public static void tearDown() {
        redisServer.stop();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {



        request1 = RequestMatching.builder()
                .memberEmail("sparta@aa.aa")
                .discordId("리릭")
                .discordNum("1663")
                .gameMode("ㅈㄱ")
                .gameName("LOL")
                .memberNumbers("2")
                .build();
        redisService.machedEnterByRedis(request1.getKey(), request1);

        //1번유저 생성
        member1 = Member.builder()
                .email("coco1@bb.bb")
                .password("qwerqwer")
                .role(MemberRoleEnum.USER)
                .profile(Profile.builder()
                        .nickname("coco")
                        .game(GameType.LOL)
                        .tier(Tier.SILVER)
                        .profileImage("coco")
                        .build())
                .build();

        request2 = RequestMatching.builder()
                .memberEmail("user@aa.aa")
                .discordId("RU")
                .discordNum("2322")
                .gameMode("ㅈㄱ")
                .gameName("LOL")
                .memberNumbers("2")
                .build();
        redisService.machedEnterByRedis(request2.getKey(), request2);

        //2번유저 생성
        member2 = Member.builder()
                .email("coco2@bb.bb")
                .password("qwerqwer")
                .role(MemberRoleEnum.USER)
                .profile(Profile.builder()
                        .nickname("coco2")
                        .game(GameType.LOL)
                        .tier(Tier.SILVER)
                        .profileImage("coco2")
                        .build())
                .build();

        resultMatching = ResultMatching.builder()
                .discordUrl("ww.aa.aa")
                .gameInfo(request1.getKey())
                .playMode(request1.getGameMode())
                .build();


        MatchingLog matchingLog1 = new MatchingLog(resultMatching,member1);
        MatchingLog matchingLog2 = new MatchingLog(resultMatching,member2);
    }

    @Test
    void testMatchingJoin() throws JsonProcessingException {
        var matchingQuota = Long.valueOf(request2.getMemberNumber());
        // Redis에서 나온 응답값과 비교할 List 생성
        List<RequestMatching> requests = Arrays.asList(request1, request2);
        //레디스에 현재 담겨있는 인원 수 검증
        Long result = redisService.waitingUserCountAndRedisConnectByRedis(request1.getKey());

        List<RequestMatching> requestMatchings = redisService.getMatchingMemberByRedis(request1.getKey(), matchingQuota, RequestMatching.class);

        // Redis에서 요청한 멤버와 매칭할 멤버들을 비교 검증
        Assertions.assertEquals(2L,result);
        Assertions.assertEquals(requests,requestMatchings);


    }


}
