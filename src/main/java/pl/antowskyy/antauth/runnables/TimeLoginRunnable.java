package pl.antowskyy.antauth.runnables;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;

public class TimeLoginRunnable implements Runnable
{
    private final ProxiedPlayer player;
    private final User user;

    public TimeLoginRunnable(ProxiedPlayer player, User user) {
        this.player = player;
        this.user = user;
    }

    @Override
    public void run() {
        if (!this.player.isConnected()) return;
        if (this.user.isLogged()) {
            return;
        }
        this.player.disconnect(ConfigurationPlugin.getConfiguration().getString(ChatHelper.fixColor("messages.error.timeoutLogin")));
    }
}
