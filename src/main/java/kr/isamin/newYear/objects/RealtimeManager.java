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

    private boolean isDay(LocalTime currentTime, LocalTime sunriseTime, LocalTime sunsetTime) {
        return !currentTime.isBefore(sunriseTime) && currentTime.isBefore(sunsetTime);
    }

    public int getRealtimeToMinecraftTime() {
        LocalTime currentTime = LocalTime.now();
        long dayLength = ChronoUnit.MINUTES.between(sunriseTime, sunsetTime);
        long nightLength = 1440 - dayLength;

        if (isDay(currentTime, sunriseTime, sunsetTime)) {
            // 낮 시간 (23000 -> 12000)
            long minutesSinceSunrise = ChronoUnit.MINUTES.between(sunriseTime, currentTime);
            double progressOfDay = (double) minutesSinceSunrise / dayLength;
            return (int) (23000 + (progressOfDay * 13000)) % 24000;
        } else {
            // 밤 시간 (12000 -> 23000)
            LocalTime adjustedTime = currentTime;
            if (currentTime.isBefore(sunriseTime)) {
                adjustedTime = currentTime.plusHours(24);
            }
            long minutesSinceSunset = ChronoUnit.MINUTES.between(sunsetTime, adjustedTime);
            double progressOfNight = (double) minutesSinceSunset / nightLength;
            return (int) (12000 + (progressOfNight * 11000));
        }
    }
}
