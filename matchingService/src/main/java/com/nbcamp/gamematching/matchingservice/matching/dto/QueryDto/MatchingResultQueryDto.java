package com.nbcamp.gamematching.matchingservice.matching.dto.QueryDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MatchingResultQueryDto {
    private Long resultId;
    private String discordUrl;
    private String playMode;
    private String gameInfo;
    private List<String> membersNickname = new ArrayList<>();
    public MatchingResultQueryDto(Long resultId,String playMode, String gameInfo,
                                  String discordUrl) {
        this.resultId = resultId;
        this.playMode = playMode;
        this.gameInfo = gameInfo;
        this.discordUrl = discordUrl;
    }
    public void addMembers(List<String> membersNickname){
        this.membersNickname.addAll(membersNickname);
    }

}
