package pl.antowskyy.antauth.handlers.login;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;
import pl.antowskyy.antauth.runnables.TimeLoginRunnable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostLoginHandler implements Listener
{
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
            player.sendMessage(ChatHelper.fixColor(!user.isRegistered() ? ConfigurationPlugin.getConfiguration().getString("messages.usage.register.chat")
                    : ConfigurationPlugin.getConfiguration().getString("messages.usage.login.chat")));

            ChatHelper.sendTitle(player,
                    (!user.isRegistered() ? ConfigurationPlugin.getConfiguration().getString("messages.usage.register.title")
                    : ConfigurationPlugin.getConfiguration().getString("messages.usage.login.title")),
                    (!user.isRegistered() ? ConfigurationPlugin.getConfiguration().getString("messages.usage.register.subtitle")
                    : ConfigurationPlugin.getConfiguration().getString("messages.usage.login.subtitle")), 3);

            AntAuth.getInstance().getProxy().getScheduler().schedule(AntAuth.getInstance(), new TimeLoginRunnable(player, user), 60L, TimeUnit.SECONDS);
        } else {
            user.setLogged(true);

            if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.premium-logged.title")) {
                ChatHelper.sendTitle(player, ConfigurationPlugin.getConfiguration().getString("messages.success.premium-logged.title-premium"), ConfigurationPlugin.getConfiguration().getString("messages.success.premium-logged.subtitle-premium"), 5);
            }
            if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.premium-logged.chat")) {
                player.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.premium-logged.chat-premium")));
            }
        }
    }
}
