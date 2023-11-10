package com.nbcamp.gamematching.matchingservice.discord.service;

import com.nbcamp.gamematching.matchingservice.config.DiscordJdaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {
    private final DiscordJdaConfig jdaConfig;

    @Transactional
    public Optional<String> createChannel(String category, int matchingQuota) {
        Optional<String> url = Optional.empty();
        try {
            url = Optional.ofNullable(jdaConfig.createVoiceChannel(category, matchingQuota));
        } catch (ExecutionException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
        return url;
    }

    // 매일 자정 12시마다 비어있는 방을 지운다.
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteChannel() {
        jdaConfig.deleteVoiceChannel();
    }

}