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
                if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.register.title")) {
                    ChatHelper.sendTitle(player, ConfigurationPlugin.getConfiguration().getString("messages.usage.register.title"), ConfigurationPlugin.getConfiguration().getString("messages.usage.register.subtitle"), 4);
                }
                if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.register.chat")) {
                    player.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.register.chat")));
                }
                continue;
            }
            if (!user.isLogged())
            {
                if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.login.title")) {
                    ChatHelper.sendTitle(player, ConfigurationPlugin.getConfiguration().getString("messages.usage.login.title"), ConfigurationPlugin.getConfiguration().getString("messages.usage.login.subtitle"), 4);
                }
                if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.login.chat")) {
                    player.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.login.chat")));
                }
            }
        }
    }
}
