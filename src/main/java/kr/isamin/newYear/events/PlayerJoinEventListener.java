package kr.isamin.newYear.events;

import kr.isamin.newYear.objects.NicknameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

public class PlayerJoinEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NicknameManager manager = NicknameManager.getInstance();

        manager.setNickname(player, manager.getNickname(player));

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
