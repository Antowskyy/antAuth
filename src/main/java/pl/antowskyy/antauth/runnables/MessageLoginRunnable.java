package pl.antowskyy.antauth.runnables;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;
import java.util.*;

public class MessageLoginRunnable implements Runnable
{
    public static List<ProxiedPlayer> players = new ArrayList<>();

    @Override
    public void run() {
        for (ProxiedPlayer player : AntAuth.getInstance().getProxy().getPlayers()) {
            User user = UserManager.getUser(player.getUniqueId());
            if (user == null) {
                player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
                continue;
            }
            if (user.isPremium()) return;
            if (!user.isRegistered())
            {
                List<String> messages = ConfigurationPlugin.getConfiguration().getStringList("messages.usage.register");
                for (String message : messages) {
                    if (message.startsWith("[TITLE]")) {
                        ChatHelper.handleTitleMessage(player, message);
                    }
                    else if (message.startsWith("[MESSAGE]")) {
                        ChatHelper.handleChatMessage(player, message);
                    }
                }
                continue;
            }
            if (!user.isLogged())
            {
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
        }
    }
}
