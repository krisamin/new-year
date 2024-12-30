package kr.isamin.newYear.objects;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.tasks.RealtimeTask;
import kr.isamin.newYear.tasks.TabTask;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static TaskManager instance;
    private final NewYear plugin;
    private final List<Integer> taskIds;

    public static TaskManager getInstance() {
        if (TaskManager.instance == null)
            throw new Error("TaskManager is not initialized. please initialize with call constructor first time.");
        return TaskManager.instance;
    }

    public TaskManager(NewYear plugin) {
        if (TaskManager.instance != null)
            throw new Error("TaskManager is initialized. Please use getInstance()");

        this.plugin = plugin;
        this.taskIds = new ArrayList<>();

        TaskManager.instance = this;
    }

    public void startTasks() {
        startRealtimeTask();
        startTabTask();
    }

    public void stopTasks() {
        for (int taskId : taskIds) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        taskIds.clear();
    }

    private void startRealtimeTask() {
        int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                plugin,
                new RealtimeTask(this.plugin),
                0L,
                20L
        );
        taskIds.add(taskId);
    }

    private void startTabTask() {
        int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                plugin,
                new TabTask(this.plugin),
                0L,
                20L
        );
        taskIds.add(taskId);
    }
}
