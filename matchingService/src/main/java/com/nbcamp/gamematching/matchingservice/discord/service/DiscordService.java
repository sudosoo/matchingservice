package com.nbcamp.gamematching.matchingservice.discord.service;

import java.util.Optional;

public interface DiscordService {
    Optional<String> createChannel(String category, int limitPlayer);
}
