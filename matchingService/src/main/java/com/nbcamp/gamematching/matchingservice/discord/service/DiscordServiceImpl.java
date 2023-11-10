package com.nbcamp.gamematching.matchingservice.discord.service;

import com.nbcamp.gamematching.matchingservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {
    private JDA jda;
    @Value("${jda.discord.guild.id}")
    private String guildId;
    @Value("${jda.discord.guild.category.Egid}")
    private String EgcategoryId;
    @Value("${jda.discord.guild.category.Hgid}")
    private String HgcategoryId;

    public DiscordServiceImpl(@Value("${jda.discord.key}") String discordToken) {
        try {
            jda = JDABuilder.createDefault(discordToken)
                    .setStatus(OnlineStatus.ONLINE)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public Optional<String> createChannel(String category, int matchingQuota) {
        Optional<String> url = Optional.empty();
        try {
            url = Optional.ofNullable(String.valueOf(createVoiceChannel(category, matchingQuota)));
        } catch (NullPointerException e) {
            throw new NotFoundException.NotFoundMatchingException();
        }
        return url;
    }

    public String createVoiceChannel(String category, int matchingQuota) {
        String channelUrl = "";
        Guild guild = jda.getGuildById(guildId);
        try {
            switch (category) {
                case ("즐겜"):
                    return getUrl("즐겜", channelUrl, guild, EgcategoryId, matchingQuota);

                case ("빡겜"):
                    return getUrl("빡겜", channelUrl, guild, HgcategoryId, matchingQuota);
            }
        } catch (RuntimeException e) {
            throw new NotFoundException.NotFoundMatchingException();
        }
        return channelUrl;
    }

    private String getUrl(String channelName, String channelUrl,
                          Guild guild, String categoryId, int matchingQuota) {
        Category category = guild.getCategoryById(categoryId);
        try {
            VoiceChannel voiceChannel = category.createVoiceChannel(channelName)
                    .addPermissionOverride(guild.getPublicRole(),
                            EnumSet.of(Permission.VOICE_CONNECT),
                            EnumSet.of(Permission.VIEW_CHANNEL))
                    .reason("매칭 완료 방 생성").submit().get();
            voiceChannel.getManager().setUserLimit(matchingQuota).queue();
            channelUrl = voiceChannel.createInvite().setMaxAge(300).submit().get().getUrl();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return channelUrl;
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteVoiceChannel() {
        Guild guild = jda.getGuildById(guildId);
        List<VoiceChannel> channelList = guild.getVoiceChannels();
        for (VoiceChannel guildChannel : channelList) {
            System.out.print(guildChannel);
            List<Member> memberList = guildChannel.getMembers();
            if (memberList.isEmpty()) {
                guildChannel.delete().reason("사용자가 없으므로 제거").queue();
            }
        }
    }

}
