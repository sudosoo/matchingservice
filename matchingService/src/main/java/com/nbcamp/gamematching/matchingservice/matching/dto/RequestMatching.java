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
    private String memberNumber;
    private String gameMode;
    private String gameName;
    private String memberEmail;
    private String key;

    public RequestMatching(RequestMatching requestMatching, String memberEmail) {
        this.gameMode = requestMatching.getGameMode();
        this.gameName = requestMatching.getGameName();
        this.memberNumber = requestMatching.getMemberNumber();
        this.discordId = requestMatching.getDiscordId();
        this.discordNum = requestMatching.getDiscordNum();
        this.memberEmail = memberEmail;
        this.discordName = this.discordId+"#"+this.discordNum;
        this.key = this.gameName + this.memberNumber;
    }

    @Builder
    public RequestMatching(String discordId, String discordNum, String memberNumber, String gameMode, String gameName, String memberEmail) {
        this.discordId = discordId;
        this.discordNum = discordNum;
        this.memberNumber = memberNumber;
        this.gameMode = gameMode;
        this.gameName = gameName;
        this.memberEmail = memberEmail;
        this.discordName = this.discordId+"#"+this.discordNum;
        this.key = this.gameName + this.memberNumber;
    }
}
