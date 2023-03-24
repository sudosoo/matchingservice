package com.nbcamp.gamematching.matchingservice.discord.service;


import com.nbcamp.gamematching.matchingservice.discord.dto.DiscordRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DiscordService {
    Optional<String> createChannel (String category,int limitPlayer);
}
