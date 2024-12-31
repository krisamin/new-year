package kr.isamin.newYear.objects;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.libs.ConfigData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class RealtimeManager {
    private static RealtimeManager instance;
    private final ConfigData config;
    private final NewYear plugin;

    public boolean enabled = true;
    public LocalTime sunriseTime = LocalTime.of(6, 0);
    public LocalTime sunsetTime = LocalTime.of(18, 0);

    public static RealtimeManager getInstance() {
        if (RealtimeManager.instance == null)
            throw new Error("TimeManager is not initialized. please initialize with call constructor first time.");
        return RealtimeManager.instance;
    }

    public RealtimeManager(NewYear plugin) {
        if (RealtimeManager.instance != null)
            throw new Error("TimeManager is initialized. Please use getInstance()");

        this.plugin = plugin;
        this.config = new ConfigData(this.plugin, "time");

        this.init();

        RealtimeManager.instance = this;
    }

    private void init() {
        this.config.init();

        YamlConfiguration config = this.config.configData();

        String rawSunriseTime = config.getString("sunrise");
        String rawSunsetTime = config.getString("sunset");

        if (rawSunriseTime == null || rawSunsetTime == null) {
            this.plugin.getLogger().info("설정된 시간이 없습니다. 기본설정(6시, 18시)으로 설정됩니다.");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime newSunriseTime = LocalTime.parse(rawSunriseTime, formatter);
            LocalTime newSunsetTime = LocalTime.parse(rawSunsetTime, formatter);

            this.sunriseTime = newSunriseTime;
            this.sunsetTime = newSunsetTime;

            this.plugin.getLogger().info("일출/일몰 시간이 설정되었습니다.");
            this.plugin.getLogger().info("일출: " + rawSunriseTime);
            this.plugin.getLogger().info("일몰: " + rawSunsetTime);
        }  catch (DateTimeParseException e) {
            this.plugin.getLogger().info("잘못된 시간 형식입니다. HH:mm 형식으로 입력해주세요.");
            e.printStackTrace();
        }
    }

    public int getRealtimeToMinecraftTime() {
        LocalTime now = LocalTime.now();

        int totalSecondsInDay = 24 * 60 * 60;
        int currentSeconds = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();

        int sunriseSeconds = sunriseTime.getHour() * 3600 + sunriseTime.getMinute() * 60 + sunriseTime.getSecond();
        int sunsetSeconds = sunsetTime.getHour() * 3600 + sunsetTime.getMinute() * 60 + sunsetTime.getSecond();

        double minecraftTime;

        if (currentSeconds < sunriseSeconds) {
            minecraftTime = 12000 + (double)(currentSeconds + totalSecondsInDay - sunsetSeconds) * (23000 - 12000)
                    / (sunriseSeconds + totalSecondsInDay - sunsetSeconds);
        } else if (currentSeconds < sunsetSeconds) {
            minecraftTime = 23000 + (double)(currentSeconds - sunriseSeconds) * (12000 - 23000)
                    / (sunsetSeconds - sunriseSeconds);
        } else {
            minecraftTime = 12000 + (double)(currentSeconds - sunsetSeconds) * (23000 - 12000)
                    / (sunriseSeconds + totalSecondsInDay - sunsetSeconds);
        }

        return (int)(minecraftTime % 24000);
    }
}
