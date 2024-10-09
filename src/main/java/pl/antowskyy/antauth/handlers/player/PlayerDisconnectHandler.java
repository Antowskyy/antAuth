package pl.antowskyy.antauth.handlers.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.managers.*;
import pl.antowskyy.antauth.runnables.MessageLoginRunnable;

public class PlayerDisconnectHandler implements Listener
{
    private final QueueManager queueManager;

    public PlayerDisconnectHandler(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event)
    {
        ProxiedPlayer player = event.getPlayer();
        User user = UserManager.getUser(player.getName());
        if (user != null) {
            user.setLogged(false);
            MessageLoginRunnable.players.remove(player);
            if (queueManager.isPlayerInQueue(player)) {
                queueManager.pollPlayerFromQueue();
            }
        }
    }
}
