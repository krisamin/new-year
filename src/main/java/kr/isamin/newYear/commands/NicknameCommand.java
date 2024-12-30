package kr.isamin.newYear.commands;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NicknameCommand implements TabExecutor {
    private final NewYear plugin;

    public NicknameCommand(NewYear plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("new-year.nickname.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.").color(NamedTextColor.RED));
            return true;
        }

        switch (args.length) {
            case 0:
                sender.sendMessage(Component.text("사용법: /nickname <set|get|reset> [username] [nickname]").color(NamedTextColor.RED));
                return true;
            case 1:
                sender.sendMessage(Component.text("플레이어 이름을 입력해주세요.").color(NamedTextColor.RED));
                return true;
        }

        String targetName = args[1];
        Player target = this.plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.").color(NamedTextColor.RED));
            return true;
        }

        switch (args[0]) {
            case "set":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("닉네임을 입력해주세요.").color(NamedTextColor.RED));
                    return true;
                }
                String nickname = args[2];
                this.setNicknameCommand(target, nickname, sender);
                break;

            case "reset":
                this.resetNicknameCommand(target, sender);
                break;

            case "get":
                this.getNicknameCommand(target, sender);
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
                completions.addAll(Arrays.asList("set", "get", "reset"));
                break;
            case 2:
                for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                    completions.add(player.getName());
                }
                break;
        }

        return completions;
    }

    private void setNicknameCommand(Player target, String nickname, CommandSender sender) {
        UserManager manager = UserManager.getInstance();
        manager.setNickname(target, Component.text(nickname));

        sender.sendMessage(Component.text("닉네임이 변경되었습니다: " + nickname).color(NamedTextColor.GREEN));
    }

    private void resetNicknameCommand(Player target, CommandSender sender) {
        UserManager manager = UserManager.getInstance();
        manager.setNickname(target, Component.text(target.getName()));

        sender.sendMessage(Component.text("닉네임이 초기화되었습니다.").color(NamedTextColor.GREEN));
    }

    private void getNicknameCommand(Player target, CommandSender sender) {
        UserManager manager = UserManager.getInstance();
        String nickname = manager.getNickname(target).content();

        sender.sendMessage(Component.text("현재 닉네임: " + nickname).color(NamedTextColor.GREEN));
    }
}