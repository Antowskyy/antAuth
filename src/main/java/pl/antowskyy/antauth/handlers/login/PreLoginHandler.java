package pl.antowskyy.antauth.handlers.login;

import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.*;
import pl.antowskyy.antauth.managers.UserManager;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class PreLoginHandler implements Listener {
    private static final Pattern pattern = Pattern.compile("^[0-9a-zA-Z-_]+$");
    private static final HashMap<String, Long> times = new HashMap<>();

    @EventHandler(priority = 10)
    public void onLogin(LoginEvent event) {
        if (event.isCancelled())
            return;
        PendingConnection connection = event.getConnection();
        String name = event.getConnection().getName();
        User user = UserManager.getUser(connection.getName());
        if (user != null) {
            if (!user.getName().equals(name)) {
                event.setCancelled(true);
                event.setCancelReason(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.invalidName").replace("{NAME}", user.getName())));
                user.setLogged(false);
            }
        }
    }

    @EventHandler(priority = 69)
    public void onPreLogin(PreLoginEvent event) {
        if (event.isCancelled())
            return;
        PendingConnection connection = event.getConnection();
        if (AntAuth.getInstance().getProxy().getPlayers().contains(AntAuth.getInstance().getProxy().getPlayer(connection.getName()))) {
            event.setCancelled(true);
            event.setCancelReason(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.playerOnline").replace("{PLAYER}", event.getConnection().getName())));
            return;
        }
        Long loginLong = times.get(event.getConnection().getAddress().getAddress().getHostAddress());
        if (loginLong != null && loginLong > System.currentTimeMillis()) {
            event.setCancelled(true);
            event.setCancelReason(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.connectCooldown").replace("{TIME}", TimeHelper.timeToString(System.currentTimeMillis() - loginLong))));
            return;
        }
        ProxiedPlayer player = AntAuth.getInstance().getProxy().getPlayer(event.getConnection().getName());
        if (AntAuth.getInstance().getProxy().getPlayers().contains(player)) {
            event.setCancelled(true);
            event.setCancelReason(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.playerOnline").replace("{PLAYER}", event.getConnection().getName())));
            return;
        }
        if (!pattern.matcher(event.getConnection().getName()).find()) {
            event.setCancelled(true);
            event.setCancelReason(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.invalidName")));
            return;
        }
        User user = UserManager.getUser(connection.getName());
        if (user == null) {
            PremiumHelper.checkPremiumPerLogin(connection.getName())
                    .thenAccept(premium -> UserManager.createUser(connection.getUniqueId(), connection.getName(), connection.getAddress().getAddress().getHostAddress(), premium))
                    .exceptionally(throwable -> {
                        AntAuth.getInstance().getProxy().getLogger().warning("An error occurred with player verification: " + connection.getName() + " Error: " + throwable.getMessage());
                        return null;
                    });
            event.setCancelReason(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.createUser")));
            event.setCancelled(true);
        } else {
            connection.setOnlineMode(user.isPremium());
        }
        times.put(connection.getAddress().getAddress().getHostAddress(), System.currentTimeMillis() + 1500L);
    }

}
