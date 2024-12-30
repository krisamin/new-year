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

        this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            sendToDiscord(
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

    private void sendToDiscord(String name, String message, UUID uuid) {
        try {
            DiscordManager discordManager = DiscordManager.getInstance();
            String hook = discordManager.getHook();

            this.plugin.getLogger().info(name);
            this.plugin.getLogger().info(message);

            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("username", name);
            jsonMap.put("content", message);
            jsonMap.put("avatar_url", "https://crafthead.net/avatar/" + uuid);
            JSONObject json = new JSONObject(jsonMap);

            URI uri = URI.create(hook);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
