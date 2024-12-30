package kr.isamin.newYear.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.DiscordManager;
import kr.isamin.newYear.objects.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatEventListener implements Listener {
    private final NewYear plugin;

    public ChatEventListener(NewYear plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        UserManager manager = UserManager.getInstance();
        Player player = event.getPlayer();
        TextComponent nickname = manager.getNickname(player);

        DiscordManager discordManager = DiscordManager.getInstance();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            discordManager.send(
                    MiniMessage.miniMessage().serialize(nickname),
                    MiniMessage.miniMessage().serialize(event.originalMessage()),
                    player.getUniqueId()
            );
        });

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
