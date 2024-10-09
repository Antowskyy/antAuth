package pl.antowskyy.antauth.handlers.server;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.*;
import pl.antowskyy.antauth.runnables.MessageLoginRunnable;

public class ServerConnectHandler implements Listener
{
    private final QueueManager queueManager;

    public ServerConnectHandler(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        User user = UserManager.getUser(player.getName());
        if (user == null) {
            player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
            return;
        }
        user.setUUID(event.getPlayer().getUniqueId());
        if (!user.isPremium() && !user.isLogged() && !user.isRegistered()) {
            if (MessageLoginRunnable.players.contains(player)) return;
            MessageLoginRunnable.players.add(player);
            return;
        }
        if (event.getTarget().equals(ConfigurationPlugin.getConfiguration().getString("auth-settings.lobby-server"))) {
            return;
        }
        if (user.isLogged()) {
            queueManager.addPlayerToQueue(player);
        }
    }
}
