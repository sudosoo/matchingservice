package com.nbcamp.gamematching.matchingservice.matching.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestMatching {
    private String discordId;
    private String discordNum;
    private String discordName;
    private String memberNumbers;
    private String gameMode;
    private String gameName;
    private String memberEmail;
    private String key;

    @Builder
    public RequestMatching(RequestMatching requestMatching, String memberEmail) {
        this.gameMode = requestMatching.getGameMode();
        this.gameName = requestMatching.getGameName();
        this.memberNumbers = requestMatching.getMemberNumbers();
        this.discordId = requestMatching.getDiscordId();
        this.discordNum = requestMatching.getDiscordNum();
        this.memberEmail = memberEmail;
        this.discordName = this.discordId+"#"+this.discordNum;
        this.key = this.gameName + this.memberNumbers;
    }

}
