package kr.isamin.newYear.discordEvents;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.DiscordManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class ChatEventListener extends ListenerAdapter {
    private final NewYear plugin;

    public ChatEventListener(NewYear plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().isSystem()) return;
        if (!event.isFromGuild()) return;
        if (event.isFromThread()) return;

        DiscordManager manager = DiscordManager.getInstance();

        String content = event.getMessage().getContentDisplay();
        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();
        Member member = event.getMember();
        if (member == null) return;
        String nickname = member.getNickname();
        if (nickname == null) return;

        String server = manager.getServer();
        String room = manager.getRoom();

        if(!server.equals(guild.getId()) || !room.equals(channel.getId())) return;

        Component message = Component.text()
                .append(
                        Component.text(nickname)
                                .color(NamedTextColor.BLUE)
                )
                .append(
                        Component.text(" | ")
                                .color(NamedTextColor.GRAY)
                )
                .append(
                        Component.text(content)
                                .color(NamedTextColor.WHITE)
                )
                .build();
        this.plugin.getServer().broadcast(message);
    }
}
