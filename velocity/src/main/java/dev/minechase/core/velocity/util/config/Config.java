package dev.minechase.core.velocity.util.config;

import com.google.common.io.ByteStreams;
import dev.minechase.core.velocity.CoreVelocity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * Represents a Configuration file to retrieve data.
 */

@NoArgsConstructor
@Getter
public class Config {

    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Configuration configuration;

    private String name;
    private File file, configFile;

    public Config(String name) {
        this.name = name;
        this.file = CoreVelocity.getInstance().getDataDirectory().toFile();
        this.configFile = new File(this.file, this.name + ".yml");
        loadConfig();
    }

    public Config(String name, File file) {
        this.name = name;
        this.file = file;
        this.configFile = new File(this.file, this.name + ".yml");
        loadConfig();
    }

    public void loadConfig() {
        if (!this.file.exists()) {
            this.file.mkdir();
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = CoreVelocity.getInstance().getResourceAsStream(this.name + ".yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
