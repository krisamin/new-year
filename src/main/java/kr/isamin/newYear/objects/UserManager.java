package kr.isamin.newYear.objects;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.libs.ConfigData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class UserManager {
    private static UserManager instance;
    private final ConfigData config;
    private final NewYear plugin;

    private final HashMap<UUID, Boolean> userMap = new HashMap<>();
    private final HashMap<UUID, String> codeMap = new HashMap<>();
    private final HashMap<UUID, TextComponent> nicknameMap = new HashMap<>();
    private final HashMap<UUID, String> discordMap = new HashMap<>();

    public static UserManager getInstance() {
        if (UserManager.instance == null)
            throw new Error("UserManager is not initialized. please initialize with call constructor first time.");
        return UserManager.instance;
    }

    public UserManager(NewYear plugin) {
        if (UserManager.instance != null)
            throw new Error("UserManager is initialized. Please use getInstance()");

        this.plugin = plugin;
        this.config = new ConfigData(this.plugin, "user");

        this.init();

        UserManager.instance = this;
    }

    private void init() {
        this.config.init();

        YamlConfiguration config = this.config.configData();

        for (String uuid : config.getKeys(false)) {
            Boolean verified = config.getBoolean(uuid + ".verified");
            this.userMap.put(UUID.fromString(uuid), verified);

            String nickname = config.getString(uuid + ".nickname");
            if (nickname == null) continue;
            this.nicknameMap.put(UUID.fromString(uuid), (TextComponent) GsonComponentSerializer.gson().deserialize(nickname));

            String discord = config.getString(uuid + ".discord");
            if (discord == null) continue;
            this.discordMap.put(UUID.fromString(uuid), discord);
        }
    }

    public void checkPlayer(Player player) {
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        String nameWithUUID = name + "(" + uuid + ")";
        this.plugin.getLogger().info("Player " + nameWithUUID + " is checking.");

        if(!this.userMap.containsKey(uuid)) {
            this.plugin.getLogger().info("Player " + nameWithUUID + " is new.");

            YamlConfiguration config = this.config.configData();

            config.set(uuid + ".verified", false);
            this.userMap.put(uuid, false);

            config.set(uuid + ".nickname", "인증안됨");
            this.nicknameMap.put(uuid, Component.text("인증안됨"));

            this.config.configData(config);
            this.config.commit();
        }
    }

    private String genCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int length = 6;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    public boolean verify(String code, String discord, String nickname) {
        try {
            UUID uuid = this.codeMap.keySet().stream().filter(key -> this.codeMap.get(key).equals(code)).findFirst().orElse(null);
            if (uuid == null) return false;

            String playerCode = this.codeMap.getOrDefault(uuid, null);
            if (playerCode == null) return false;
            if (!playerCode.equals(code)) return false;
            this.codeMap.remove(uuid);

            YamlConfiguration config = this.config.configData();

            config.set(uuid + ".verified", true);
            this.userMap.put(uuid, true);

            config.set(uuid + ".nickname", nickname);
            this.nicknameMap.put(uuid, Component.text(nickname));

            config.set(uuid + ".discord", discord);
            this.discordMap.put(uuid, discord);

            this.config.configData(config);
            this.config.commit();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getCode(Player player) {
        UUID uuid = player.getUniqueId();
        String code = genCode();
        this.codeMap.put(uuid, code);
        return code;
    }

    public boolean isVerified(UUID uuid) {
        return this.userMap.getOrDefault(uuid, false);
    }
    public boolean isVerified(Player player) {
        return this.isVerified(player.getUniqueId());
    }

    private @NotNull Player getPlayerSafety(String playerName) {
        Player player = this.plugin.getServer().getPlayer(playerName);
        if (player == null)
            throw new Error("Player " + playerName + " not found.");
        return player;
    }
    private @NotNull Player getPlayerSafety(UUID uuid) {
        Player player = this.plugin.getServer().getPlayer(uuid);
        if (player == null)
            throw new Error ("Player with UUID " + uuid + " is not found");
        return player;
    }

    public String getDiscord(UUID uuid) {
        return this.discordMap.getOrDefault(uuid, null);
    }
    public String getDiscord(@NotNull Player player) {
        return this.getDiscord(player.getUniqueId());
    }
    public String getDiscord(String playerName) {
        return this.getDiscord(this.getPlayerSafety(playerName).getUniqueId());
    }

    public TextComponent getNickname(UUID uuid) {
        if (!this.nicknameMap.containsKey(uuid))
            this.nicknameMap.put(uuid, (TextComponent) this.getPlayerSafety(uuid).displayName());
        return this.nicknameMap.get(uuid);
    }
    public TextComponent getNickname(@NotNull Player player) {
        return this.getNickname(player.getUniqueId());
    }
    public TextComponent getNickname(String playerName) {
        return this.getNickname(this.getPlayerSafety(playerName).getUniqueId());
    }

    public TextComponent setNickname(UUID uuid, TextComponent nickname, boolean updateTab) {
        Player player = this.getPlayerSafety(uuid);
        player.displayName(nickname);
        this.changePlayerDisplayName(player, nickname);
        if (updateTab) player.playerListName(nickname);

        YamlConfiguration config = this.config.configData();

        String rawNickname = GsonComponentSerializer.gson().serialize(nickname);
        config.set(uuid + ".nickname", rawNickname);
        this.nicknameMap.put(uuid, nickname);

        this.config.configData(config);
        this.config.commit();

        return nickname;
    }
    public TextComponent setNickname(@NotNull Player player, TextComponent nickname, boolean updateTab) {
        return this.setNickname(player.getUniqueId(), nickname, updateTab);
    }
    public TextComponent setNickname(String playerName, TextComponent nickname, boolean updateTab) {
        return this.setNickname(this.getPlayerSafety(playerName).getUniqueId(), nickname, updateTab);
    }
    public TextComponent setNickname(UUID uuid, TextComponent nickname) {
        return this.setNickname(uuid, nickname, true);
    }
    public TextComponent setNickname(@NotNull Player player, TextComponent nickname) {
        return this.setNickname(player.getUniqueId(), nickname, true);
    }
    public TextComponent setNickname(String playerName, TextComponent nickname) {
        return this.setNickname(this.getPlayerSafety(playerName).getUniqueId(), nickname, true);
    }

    private void changePlayerDisplayName(Player player, Component nickname) {
        String teamName = player.getUniqueId().toString() + "_team";
        Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.addEntry(player.getName());

        Component prefix = Component.text()
                .append(Component.text("["))
                .append(nickname)
                .append(Component.text("] "))
                .color(NamedTextColor.YELLOW)
                .build();

        team.displayName(nickname);
        team.prefix(prefix);

        for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            onlinePlayer.setScoreboard(scoreboard);
        }
    }
}
