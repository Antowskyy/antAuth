package pl.antowskyy.antauth;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import pl.antowskyy.antauth.commands.admin.AuthCommand;
import pl.antowskyy.antauth.commands.player.*;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.*;
import pl.antowskyy.antauth.database.Database;
import pl.antowskyy.antauth.handlers.login.*;
import pl.antowskyy.antauth.handlers.player.*;
import pl.antowskyy.antauth.handlers.server.*;
import pl.antowskyy.antauth.managers.QueueManager;
import pl.antowskyy.antauth.managers.UserManager;
import pl.antowskyy.antauth.runnables.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class AntAuth extends Plugin
{

    @Getter
    private static AntAuth instance;
    @Getter
    private static Database database;
    private QueueManager queueManager;

    @Override
    public void onEnable()
    {
        getLogger().info("[antAuth] Starting plugin....");
        instance = this;
        getLogger().info("[antAuth] Loading configuration...");
        new ConfigurationPlugin().loadConfig();
        getLogger().info("[antAuth] Loading database...");
        database = new Database();
        registerDatabase();
        getLogger().info("[antAuth] Loading managers...");
        UserManager.loadUsers();
        queueManager = new QueueManager();
        getLogger().info("[antAuth] Loading commands...");
        registerCommands();
        getLogger().info("[antAuth] Loading handlers...");
        registerHandlers();
        getLogger().info("[antAuth] Loading runnables...");
        registerRunnables();
        getLogger().info("[antAuth] Enabled plugin!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("[antAuth] Disabling plugin...");
        getLogger().info("[antAuth] Saving players data...");
        getProxy().getPlayers().forEach(player -> {
            User user = UserManager.getUser(player.getUniqueId());
            user.update();
        });
        getLogger().info("[antAuth] Saving configuration...");
        new ConfigurationPlugin().saveConfig();
        instance = null;
        getLogger().info("[antAuth] Disconnecting database...");
        if (database != null) {
            database.close();
        }
        getLogger().info("[antAuth] Disabled plugin!");
    }


    private void registerHandlers()
    {
        getProxy().getPluginManager().registerListener(this, new PostLoginHandler());
        getProxy().getPluginManager().registerListener(this, new PreLoginHandler());
        getProxy().getPluginManager().registerListener(this, new PlayerChatHandler());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectHandler(queueManager));
        getProxy().getPluginManager().registerListener(this, new ServerPingHandler());
        getProxy().getPluginManager().registerListener(this, new ServerConnectHandler(queueManager));
    }

    private void registerCommands()
    {
        getProxy().getPluginManager().registerCommand(this, new AuthCommand());
        getProxy().getPluginManager().registerCommand(this, new ChangepasswordCommand());
        getProxy().getPluginManager().registerCommand(this, new LoginCommand());
        getProxy().getPluginManager().registerCommand(this, new RegisterCommand());
        getProxy().getPluginManager().registerCommand(this, new UnregisterCommand());
    }

    private void registerRunnables()
    {
        getProxy().getScheduler().schedule(this, new MessageLoginRunnable(), 5L, 5L, TimeUnit.SECONDS);
        getProxy().getScheduler().schedule(this,
                new QueueRunnable(
                        queueManager,
                        ConfigurationPlugin.getConfiguration().getString("auth-settings.auth-server"),
                        ConfigurationPlugin.getConfiguration().getString("auth-settings.lobby-server"),
                        ConfigurationPlugin.getConfiguration().getInt("auth-settings.queue.max-connection-time"),
                        ConfigurationPlugin.getConfiguration().getInt("auth-settings.queue.max-retries")
                ),
                5L, 5L, TimeUnit.SECONDS);
    }

    private void registerDatabase()
    {
        database.executeUpdate("CREATE TABLE IF NOT EXISTS `antauth_users` (`uuid` char(64) NOT NULL, `name` varchar(32) NOT NULL, `password` text NOT NULL, `premium` int(1) NOT NULL, `lastip` text NOT NULL, `registered` int(1) NOT NULL, `logged` int(1) NOT NULL);");
    }

}
