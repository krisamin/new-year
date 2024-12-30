package kr.isamin.newYear.tasks;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.objects.RealtimeManager;
import org.bukkit.World;

public class RealtimeTask implements Runnable {
    private final NewYear plugin;

    public RealtimeTask(NewYear plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        RealtimeManager manager = RealtimeManager.getInstance();
        if (!manager.enabled) {
            return;
        }
        int time = manager.getRealtimeToMinecraftTime();
        for (World world : this.plugin.getServer().getWorlds()) {
            world.setTime(time);
        }
    }
}
