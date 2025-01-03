package kr.isamin.newYear.events;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.DiscordManager;
import kr.isamin.newYear.objects.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

public class PlayerJoinEventListener implements Listener {
    private final NewYear plugin;

    public PlayerJoinEventListener(NewYear plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UserManager manager = UserManager.getInstance();
        manager.checkPlayer(player);

        boolean verified = manager.isVerified(player);

        if(!verified) {
            String code = manager.getCode(player);
            String verifyCommand = "/verify " + code;
            Component message = Component.text()
                    .append(
                            Component.text("인증을 진행해 주세요.")
                                    .color(NamedTextColor.YELLOW)
                    )
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(
                            Component.text(verifyCommand)
                                    .color(NamedTextColor.RED)
                    )
                    .build();

            player.kick(message);
            return;
        }

        manager.setNickname(player, manager.getNickname(player));

        DiscordManager discordManager = DiscordManager.getInstance();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            discordManager.send(
                    MiniMessage.miniMessage().serialize(manager.getNickname(player)),
                    "# 입장",
                    player.getUniqueId()
            );
        });

        Component joinMessage = Component.text()
                .append(manager.getNickname(player).color(NamedTextColor.YELLOW))
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.text("입장").color(NamedTextColor.GREEN))
                .build();
        event.joinMessage(joinMessage);

        Component title = Component.text("HAPPY NEW YEAR")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        Component subtitle = Component.text("2025 Project")
                .color(NamedTextColor.YELLOW);
        player.showTitle(Title.title(
                title,
                subtitle,
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(1000)
                )
        ));
    }
}
