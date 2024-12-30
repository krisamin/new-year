package kr.isamin.newYear.objects;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.discordEvents.ChatEventListener;
import kr.isamin.newYear.libs.ConfigData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.json.simple.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DiscordManager {
    private static DiscordManager instance;
    private final ConfigData config;
    private final NewYear plugin;

    public JDA jda;
    public Guild guild;

    public static DiscordManager getInstance() {
        if (DiscordManager.instance == null)
            throw new Error("DiscordManager is not initialized. please initialize with call constructor first time.");
        return DiscordManager.instance;
    }

    public DiscordManager(NewYear plugin) {
        if (DiscordManager.instance != null)
            throw new Error("DiscordManager is initialized. Please use getInstance()");

        this.plugin = plugin;
        this.config = new ConfigData(this.plugin, "discord");

        this.init();

        DiscordManager.instance = this;
    }

    private void init() {
        this.config.init();

        String token = config.configData().getString("token");
        String server = config.configData().getString("server");
        if (token == null || server == null) {
            this.plugin.getLogger().info(("튕"));
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("HAPPY NEW YEAR 2025 Project"));
        builder.enableIntents(
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        );
        builder.addEventListeners(new ChatEventListener(this.plugin));
        builder.addEventListeners(new DiscordCommandManager());

        try {
            jda = builder.build();
            jda.awaitReady();
            jda.updateCommands()
                    .addCommands(
                            Commands.slash("ping", "핑 확인하기")
                                    .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                                    .setGuildOnly(false),
                            Commands.slash("verify", "인증을 진행합니다")
                                    .addOption(OptionType.STRING, "code", "인증 코드", true)
                                    .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                                    .setGuildOnly(true)
                    ).queue();

            guild = jda.getGuildById(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public String getServer() {
        return config.configData().getString("server");
    }

    public String getRoom() {
        return config.configData().getString("room");
    }

    public String getRole() {
        return config.configData().getString("role");
    }

    public void send(String name, String message, UUID uuid) {
        try {
            String hook = config.configData().getString("hook");

            this.plugin.getLogger().info(name);
            this.plugin.getLogger().info(message);

            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("username", name);
            jsonMap.put("content", message);
            jsonMap.put("avatar_url", "https://crafthead.net/avatar/" + uuid);
            JSONObject json = new JSONObject(jsonMap);

            URI uri = URI.create(hook);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
