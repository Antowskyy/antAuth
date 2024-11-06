package pl.antowskyy.antauth.handlers.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;
import java.util.Objects;

public final class PlayerChatHandler implements Listener
{
    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer) || !event.isCommand()) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        User user = UserManager.getUser(player.getName());
        if (Objects.isNull(user)) {
            return;
        }
        if (!player.getServer().getInfo().getName().equalsIgnoreCase("auth")) {
            return;
        }

        final String message = event.getMessage().split(" ")[0];
        if (user.isLogged() || message.equalsIgnoreCase("/login") || message.equalsIgnoreCase("/l")
                || message.equalsIgnoreCase("/register") || message.equalsIgnoreCase("/reg")) {
            return;
        }

        event.setCancelled(true);

        if (!user.isRegistered()) {
            player.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.notRegistered")));
        }
        if (!user.isLogged()) {
            player.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.notLogged")));
        }
    }
}
