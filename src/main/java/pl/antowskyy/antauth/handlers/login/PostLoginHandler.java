package pl.antowskyy.antauth.handlers.login;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.QueueManager;
import pl.antowskyy.antauth.managers.UserManager;
import pl.antowskyy.antauth.runnables.TimeLoginRunnable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostLoginHandler implements Listener
{
    private final QueueManager queueManager;

    public PostLoginHandler(QueueManager queueManager)
    {
        this.queueManager = queueManager;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        User user = UserManager.getUser(player.getName());
        if (user == null) {
            player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
            return;
        }
        if (Objects.isNull(user.getLastIP()) || !user.getLastIP()
                .equals(player.getAddress().getAddress().getHostAddress())) {
            user.setLastIP(player.getAddress().getAddress().getHostAddress());
        }
        if (!user.isPremium()) {
            user.setLogged(false);

            if (user.isRegistered()) {
                List<String> messages = ConfigurationPlugin.getConfiguration().getStringList("messages.usage.login");
                for (String message : messages) {
                    if (message.startsWith("[TITLE]")) {
                        ChatHelper.handleTitleMessage(player, message);
                    }
                    else if (message.startsWith("[MESSAGE]")) {
                        ChatHelper.handleChatMessage(player, message);
                    }
                }
            }
            else {
                List<String> messages = ConfigurationPlugin.getConfiguration().getStringList("messages.usage.register");
                for (String message : messages) {
                    if (message.startsWith("[TITLE]")) {
                        ChatHelper.handleTitleMessage(player, message);
                    }
                    else if (message.startsWith("[MESSAGE]")) {
                        ChatHelper.handleChatMessage(player, message);
                    }
                }
            }

            AntAuth.getInstance().getProxy().getScheduler().schedule(AntAuth.getInstance(), new TimeLoginRunnable(player, user), 60L, TimeUnit.SECONDS);
        }
        else {
            user.setLogged(true);

            List<String> messages = ConfigurationPlugin.getConfiguration().getStringList("messages.success.premium-logged");

            for (String message : messages) {
                if (message.startsWith("[TITLE]")) {
                    ChatHelper.handleTitleMessage(player, message);
                }
                else if (message.startsWith("[MESSAGE]")) {
                    ChatHelper.handleChatMessage(player, message);
                }
            }
            queueManager.addPlayerToQueue(player);
        }
    }
}
