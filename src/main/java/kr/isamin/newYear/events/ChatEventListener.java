package kr.isamin.newYear.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import kr.isamin.newYear.objects.NicknameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        NicknameManager manager = NicknameManager.getInstance();

        TextComponent nickname = manager.getNickname(player);

        ChatRenderer renderer = ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
                Component.text()
                        .append(nickname.color(NamedTextColor.YELLOW))
                        .append(Component.text(" | ").color(NamedTextColor.GRAY))
                        .append(message.color(NamedTextColor.WHITE))
                        .build()
        );

        event.renderer(renderer);
    }
}
