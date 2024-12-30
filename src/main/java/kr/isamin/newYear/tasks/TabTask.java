package kr.isamin.newYear.tasks;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.RealtimeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TabTask implements Runnable {
    private final NewYear plugin;

    public TabTask(NewYear plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        RealtimeManager manager = RealtimeManager.getInstance();

        Component header = Component.text()
                .append(Component.newline())
                .append(
                        Component.text("  =  HAPPY NEW YEAR 2025 Project  =  ")
                                .color(NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD)
                )
                .append(Component.newline())
                .build();
        Component footer = Component.text()
                .append(Component.newline())
                .append(Component.text("현재 " + currentTime.format(timeFormat)))
                .append(Component.text("  "))
                .append(Component.text("일출 " + manager.sunriseTime.format(timeFormat)))
                .append(Component.text("  "))
                .append(Component.text("일몰 " + manager.sunsetTime.format(timeFormat)))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text(currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))))
                .append(Component.newline())
                .color(NamedTextColor.WHITE)
                .build();
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }
}
