package com.nbcamp.gamematching.matchingservice.matching.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nbcamp.gamematching.matchingservice.matching.dto.ResponseUrlInfo;
import com.nbcamp.gamematching.matchingservice.matching.Service.MatchingService;
import com.nbcamp.gamematching.matchingservice.matching.dto.RequestMatching;
import com.nbcamp.gamematching.matchingservice.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matching")
@Slf4j
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;
    private final SimpMessagingTemplate template;

    @PostMapping("/join")
    @ResponseBody
    public ResponseUrlInfo joinRequest(@RequestBody RequestMatching requestMatching,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                       HttpServletRequest servletRequest) throws JsonProcessingException {
        var member = userDetails.getMember();
        var matchingMember = new RequestMatching(requestMatching,member.getEmail());
        log.info("Join Matching Useremail{} UserDiscordId{}",member.getEmail(),requestMatching.getDiscordId());
        return matchingService.matchingJoin(matchingMember,servletRequest);
    }

    @MessageMapping(value = "/url")
    public void message(ResponseUrlInfo responseUrlInfo){
        template.convertAndSend("/matchingsub/" + responseUrlInfo.getTopicName(), responseUrlInfo.getUrl());
    }


}