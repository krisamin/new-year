package kr.isamin.newYear;

import kr.isamin.newYear.commands.NicknameCommand;
import kr.isamin.newYear.commands.RealtimeCommand;
import kr.isamin.newYear.events.ChatEventListener;
import kr.isamin.newYear.events.PlayerJoinEventListener;
import kr.isamin.newYear.events.PlayerQuitEventListener;
import kr.isamin.newYear.objects.NicknameManager;
import kr.isamin.newYear.objects.RealtimeManager;
import kr.isamin.newYear.objects.TaskManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewYear extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("===== HAPPY NEW YEAR 2025 Project =====");

        new TaskManager(this);
        new NicknameManager(this);
        new RealtimeManager(this);

        this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatEventListener(), this);

        registerNicknameCommand();
        startTasks();

        getLogger().info("HAPPY NEW YEAR 2025 Project 플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        stopTasks();
        getLogger().info("HAPPY NEW YEAR 2025 Project 플러그인이 비활성화되었습니다.");
    }

    private void startTasks() {
        TaskManager taskManager = TaskManager.getInstance();
        taskManager.startTasks();
    }

    private void stopTasks() {
        TaskManager taskManager = TaskManager.getInstance();
        taskManager.stopTasks();
    }

    private void registerNicknameCommand() {
        NicknameCommand nicknameCommand = new NicknameCommand(this);
        PluginCommand nicknamePluginCommand = this.getCommand("nickname");
        if (nicknamePluginCommand != null) {
            nicknamePluginCommand.setTabCompleter(nicknameCommand);
            nicknamePluginCommand.setExecutor(nicknameCommand);
        } else {
            this.getLogger().warning("Command 'nickname' is not defined");
        }

        RealtimeCommand realtimeCommand = new RealtimeCommand(this);
        PluginCommand realtimePluginCommand = this.getCommand("realtime");
        if (realtimePluginCommand != null) {
            realtimePluginCommand.setTabCompleter(realtimeCommand);
            realtimePluginCommand.setExecutor(realtimeCommand);
        } else {
            this.getLogger().warning("Command 'realtime' is not defined");
        }
    }
}
