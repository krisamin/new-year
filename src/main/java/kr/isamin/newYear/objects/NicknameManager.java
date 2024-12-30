package kr.isamin.newYear.objects;

import kr.isamin.newYear.NewYear;
import kr.isamin.newYear.libs.ConfigData;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.Set;

public class NicknameManager {
    private static NicknameManager instance;
    private final ConfigData config;
    private final NewYear plugin;
    private final HashMap<UUID, TextComponent> nicknameMap = new HashMap<>();

    public static NicknameManager getInstance() {
        if (NicknameManager.instance == null)
            throw new Error("PlayerNicknameManger is not initialized. please initialize with call constructor first time.");
        return NicknameManager.instance;
    }

    public NicknameManager(NewYear plugin) {
        if (NicknameManager.instance != null)
            throw new Error("PlayerNicknameManager is initialized. Please use getInstance()");

        this.plugin = plugin;
        this.config = new ConfigData(this.plugin, "nickname");

        this.init();

        NicknameManager.instance = this;
    }

    private void init() {
        this.config.init();

        YamlConfiguration config = this.config.configData();

        Set<String> uuidSet = config.getKeys(false);

        for (String uuid : uuidSet) {
            String json = config.getString(uuid + ".displayName");
            if (json == null) continue;
            this.nicknameMap.put(UUID.fromString(uuid), (TextComponent) GsonComponentSerializer.gson().deserialize(json));
        }
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

        this.nicknameMap.put(uuid, nickname);
        player.displayName(nickname);
        if (updateTab) player.playerListName(nickname);

        YamlConfiguration config = this.config.configData();

        String json = GsonComponentSerializer.gson().serialize(nickname);
        this.plugin.getLogger().info(json);
        config.set(uuid + ".displayName", json);

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
}
