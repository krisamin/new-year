package kr.isamin.newYear.commands;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.RealtimeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealtimeCommand implements TabExecutor {
    private final NewYear plugin;

    public RealtimeCommand(NewYear plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("new-year.realtime.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.").color(NamedTextColor.RED));
            return true;
        }

        switch (args.length) {
            case 0:
                sender.sendMessage(Component.text("사용법: /realtime <status|enable|disable> [HH:mm]").color(NamedTextColor.RED));
                return true;
        }

        RealtimeManager manager = RealtimeManager.getInstance();

        switch (args[0]) {
            case "status":
                sender.sendMessage("=== Realtime 상태 ===");
                sender.sendMessage("상태: " + (manager.enabled ? "활성화됨 " : "비활성화됨"));
                sender.sendMessage("현재 시간: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                sender.sendMessage("일출 시간: " + manager.sunriseTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                sender.sendMessage("일몰 시간: " + manager.sunsetTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                sender.sendMessage("계산 시간: " + manager.getRealtimeToMinecraftTime());
                sender.sendMessage("마크 시간: " + Bukkit.getWorlds().getFirst().getTime());
                break;

            case "enable":
                manager.enabled = true;
                sender.sendMessage(Component.text("현실시간 연동이 활성화되었습니다.").color(NamedTextColor.GREEN));
                break;

            case "disable":
                manager.enabled = false;
                sender.sendMessage(Component.text("현실시간 연동이 비활성화되었습니다.").color(NamedTextColor.GREEN));
                break;

            default:
                sender.sendMessage(Component.text("잘못된 명령어입니다.").color(NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        switch(args.length) {
            case 1:
                completions.addAll(Arrays.asList("status", "enable", "disable"));
                break;
        }

        return completions;
    }
}
