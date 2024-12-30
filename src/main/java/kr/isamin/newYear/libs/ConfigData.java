package kr.isamin.newYear.libs;

import kr.isamin.newYear.NewYear;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigData {

    private final NewYear plugin;
    private final String namespace;
    private File configFile;
    private YamlConfiguration configData;

    public ConfigData(NewYear plugin, String namespace) {
        this.plugin = plugin;
        this.namespace = namespace;
    }

    private String filename() {
        return this.namespace + ".yaml";
    }

    private File getFile() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        configFile = new File(plugin.getDataFolder(), this.filename());

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe(this.filename() + " 파일 생성 실패!");
                e.printStackTrace();
            }
        }

        return configFile;
    }

    private static YamlConfiguration cloneYaml(YamlConfiguration original) {
        YamlConfiguration clone = new YamlConfiguration();

        Map<String, Object> values = original.getValues(true);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            clone.set(entry.getKey(), entry.getValue());
        }

        return clone;
    }

    private void loadFile() {
        this.configFile = this.getFile();

        this.configData = YamlConfiguration.loadConfiguration(configFile);
    }

    private void saveFile() {
        if (this.configData == null || this.configFile == null) return;

        try {
            this.configData.save(this.configFile);
        } catch (IOException e) {
            this.plugin.getLogger().info(this.filename() + " 데이터 저장 실패!");
            e.printStackTrace();
        }
    }

    public void init() {
        this.getFile();
    }

    public YamlConfiguration configData() {
        this.loadFile();
        return ConfigData.cloneYaml(this.configData);
    }

    public void configData(YamlConfiguration configData) {
        this.configData = configData;
    }

    public void commit() {
        this.saveFile();
    }
}
