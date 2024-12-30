package kr.isamin.newYear.events;

import kr.isamin.newYear.objects.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UserManager manager = UserManager.getInstance();

        TextComponent nickname = manager.getNickname(player);

        TextComponent quitMessage = Component.text()
                .append(nickname.color(NamedTextColor.YELLOW))
                .append(Component.text(" | ").color(NamedTextColor.GRAY))
                .append(Component.text("퇴장").color(NamedTextColor.RED))
                .build();

        event.quitMessage(quitMessage);
    }
}
