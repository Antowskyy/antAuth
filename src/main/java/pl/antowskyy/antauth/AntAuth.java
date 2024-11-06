package pl.antowskyy.antauth;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import pl.antowskyy.antauth.commands.admin.AuthCommand;
import pl.antowskyy.antauth.commands.player.*;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.*;
import pl.antowskyy.antauth.database.Database;
import pl.antowskyy.antauth.handlers.login.*;
import pl.antowskyy.antauth.handlers.player.*;
import pl.antowskyy.antauth.handlers.server.*;
import pl.antowskyy.antauth.helpers.UpdateHelper;
import pl.antowskyy.antauth.managers.*;
import pl.antowskyy.antauth.runnables.*;
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
        getLogger().info("Starting plugin....");
        instance = this;
        getLogger().info("Checking update...");
        new UpdateHelper(this, 119985).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("");
                getLogger().info("There is a new update available!");
                getLogger().info("Be sure to download the new version!");
                getLogger().info("");
                this.getProxy().stop();
            }
        });
        getLogger().info("Loading configuration...");
        new ConfigurationPlugin().loadConfig();
        getLogger().info("Loading database...");
        database = new Database();
        registerDatabase();
        getLogger().info("Loading managers...");
        UserManager.loadUsers();
        queueManager = new QueueManager();
        getLogger().info("Loading commands...");
        registerCommands();
        getLogger().info("Loading handlers...");
        registerHandlers();
        getLogger().info("Loading runnables...");
        registerRunnables();
        getLogger().info("Enabled plugin!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Disabling plugin...");
        getLogger().info("Saving players data...");
        getProxy().getPlayers().forEach(player -> {
            User user = UserManager.getUser(player.getName());
            user.update();
        });
        getLogger().info("Saving configuration...");
        new ConfigurationPlugin().saveConfig();
        getLogger().info("Disconnecting database...");
        if (database != null) {
            database.close();
        }
        instance = null;
        getLogger().info("Disabled plugin!");
    }


    private void registerHandlers()
    {
        getProxy().getPluginManager().registerListener(this, new PostLoginHandler(queueManager));
        getProxy().getPluginManager().registerListener(this, new PreLoginHandler());
        getProxy().getPluginManager().registerListener(this, new PlayerChatHandler());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectHandler(queueManager));
        getProxy().getPluginManager().registerListener(this, new ServerPingHandler());
        getProxy().getPluginManager().registerListener(this, new ServerConnectHandler());
    }

    private void registerCommands()
    {
        getProxy().getPluginManager().registerCommand(this, new AuthCommand());
        getProxy().getPluginManager().registerCommand(this, new ChangepasswordCommand());
        getProxy().getPluginManager().registerCommand(this, new LoginCommand(queueManager));
        getProxy().getPluginManager().registerCommand(this, new RegisterCommand(queueManager));
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
