package com.nbcamp.gamematching.matchingservice.matching.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nbcamp.gamematching.matchingservice.matching.service.MatchingService;
import com.nbcamp.gamematching.matchingservice.matching.dto.NicknameDto;
import com.nbcamp.gamematching.matchingservice.matching.dto.QueryDto.MatchingResultQueryDto;
import com.nbcamp.gamematching.matchingservice.matching.dto.RequestMatching;
import com.nbcamp.gamematching.matchingservice.matching.dto.ResponseUrlInfo;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import com.nbcamp.gamematching.matchingservice.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matching")
@Slf4j
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;
    private final SimpMessagingTemplate template;

    @MessageMapping(value = "/url")
    public void message(ResponseUrlInfo responseUrlInfo) {
        template.convertAndSend("/matchingsub/" + responseUrlInfo.getTopicName()
                , responseUrlInfo);
    }

    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<ResponseUrlInfo> joinRequest(@RequestBody final RequestMatching requestMatching,
                                                       @AuthenticationPrincipal final UserDetailsImpl userDetails,
                                                       final HttpServletRequest servletRequest)
            throws JsonProcessingException {

        var member = userDetails.getMember();
        var matchingMember = new RequestMatching(requestMatching, member.getEmail());
        log.info("Join Matching Useremail{} UserDiscordId{}", member.getEmail(), requestMatching.getDiscordId());
        var urlInfo = matchingService.matchingJoin(matchingMember, servletRequest);
        return ResponseEntity.ok(urlInfo);
    }

    @GetMapping("/findmember")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<List<MatchingResultQueryDto>> findByResultMatchingAndMember(@RequestParam final Long id) {
        return ResponseEntity.ok(matchingService.findByMatchingResultMemberNicknameByMemberId(id));
    }

    @GetMapping("/{matchingId}/members")
    public List<NicknameDto> getMatchingMembers(@PathVariable final Long matchingId,
                                                @AuthenticationPrincipal final UserDetailsImpl userDetails) {
        var member = userDetails.getMember();
        return matchingService.findMatchingMembers(matchingId, member.getId());
    }


}
