package pl.antowskyy.antauth.configuration;

import lombok.Getter;
import net.md_5.bungee.config.*;
import pl.antowskyy.antauth.AntAuth;
import java.io.*;
import java.nio.file.Files;

public final class ConfigurationPlugin
{
    @Getter
    private static Configuration configuration;
    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final File file = new File(AntAuth.getInstance().getDataFolder(), "config.yml");

    public void loadConfig() {
        try {
            if (!file.exists()) {
                Files.createDirectories(AntAuth.getInstance().getDataFolder().toPath());
                try (InputStream in = AntAuth.getInstance().getResourceAsStream("config.yml"))
                {
                    Files.copy(in, file.toPath());
                }
            }
            configuration = provider.load(file);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            provider.save(configuration, file);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
