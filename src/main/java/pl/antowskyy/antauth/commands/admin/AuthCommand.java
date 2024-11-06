package pl.antowskyy.antauth.commands.admin;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.*;
import org.mindrot.jbcrypt.BCrypt;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;
import java.util.HashSet;

public class AuthCommand extends Command implements TabExecutor {
    public AuthCommand() {
        super("auth", "antauth.admin");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !sender.hasPermission("antauth.admin"))
        {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.noPermission")));
            return;
        }
        if (args.length == 0) {
            ConfigurationPlugin.getConfiguration().getStringList("messages.usage.admin.help").forEach(message -> sender.sendMessage(ChatHelper.fixColor(message)));
            return;
        }
        switch (args[0]) {
            case "changepassword":
            {
                if (args.length != 3)
                {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.admin.changepassword")));
                    return;
                }
                User user = UserManager.getUser(args[1]);
                if (user == null) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
                    return;
                }
                if (user.isPremium()) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.adminUserPremium")));
                    return;
                }
                if (!user.isRegistered()) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.adminUserNotRegistered")));
                    return;
                }
                ProxiedPlayer player = AntAuth.getInstance().getProxy().getPlayer(args[1]);

                player.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.admin.changepassword.playerMsg")
                        .replace("{ADMIN}", sender.getName())));

                sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.admin.changepassword.adminMsg")
                        .replace("{PLAYER}", user.getName())));


                user.setPassword(BCrypt.hashpw(args[2], BCrypt.gensalt()));

            }
            case "unregister":
            {
                if (args.length != 2) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.admin.unregister")));
                    return;
                }
                User user = UserManager.getUser(args[1]);
                if (user == null) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
                    return;
                }
                ProxiedPlayer player = AntAuth.getInstance().getProxy().getPlayer(args[1]);
                UserManager.deleteUser(user);
                if (player != null) {
                    player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.admin.unregister.playerKick")
                            .replace("{ADMIN}", sender.getName())));
                    return;
                }
                sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.admin.unregister.adminMsg")
                        .replace("{PLAYER}", args[1])));
                return;
            }
            case "info":
            {
                if (args.length != 2) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.admin.info")));
                    return;
                }
                User user = UserManager.getUser(args[1]);
                if (user == null) {
                    sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
                    return;
                }
                ConfigurationPlugin.getConfiguration().getStringList("messages.success.commands.admin.info").forEach(message ->
                        sender.sendMessage(ChatHelper.fixColor(message)
                                .replace("{PLAYER}", user.getName())
                                .replace("{PREMIUM}", (user.isPremium() ? ChatHelper.fixColor("&2&l✔") : ChatHelper.fixColor("&4&l❌")))
                                .replace("{LASTIP}", user.getLastIP())
                                .replace("{UUID}", user.getUUID().toString())));

                return;
            }
            case "reload":
            {
                new ConfigurationPlugin().loadConfig();
                sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.admin.reload")));
                return;
            }
        }
        ConfigurationPlugin.getConfiguration().getStringList("messages.usage.admin.help").forEach(message -> sender.sendMessage(ChatHelper.fixColor(message)));
    }


    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 3) return ImmutableSet.of();
        if (args.length < 1) {
            return ImmutableSet.of();
        }
        HashSet<String> matches = new HashSet<>();
        if (args.length != 2) return matches;
        String search = args[1].toLowerCase();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.getName().toLowerCase().startsWith(search)) continue;
            matches.add(player.getName());
        }
        return matches;
    }
}
