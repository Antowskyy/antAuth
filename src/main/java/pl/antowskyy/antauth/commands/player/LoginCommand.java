package pl.antowskyy.antauth.commands.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mindrot.jbcrypt.BCrypt;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;
import pl.antowskyy.antauth.runnables.MessageLoginRunnable;

public class LoginCommand extends Command
{
    public LoginCommand() {
        super("login", null, "l");
    }

    public void execute(CommandSender sender, String[] args) {
        User user = UserManager.getUser(sender.getName());
        if (user == null) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userError")));
            return;
        }
        if (user.isPremium()) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userisPremium")));
            return;
        }
        if (user.isLogged()) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userLogged")));
            return;
        }
        if (!user.isRegistered()) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userNotRegistered")));
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.user.login")));
            return;
        }
        if (!BCrypt.checkpw(args[0], user.getPassword())) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.passwordIncorrect")));
            return;
        }
        user.setLogged(true);
        ProxiedPlayer player = (ProxiedPlayer)sender;
        MessageLoginRunnable.players.remove(player);
        player.connect(AntAuth.getInstance().getProxy().getServerInfo(ConfigurationPlugin.getConfiguration().getString("auth-settings.lobby-server")));

        if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.logged.title")) {
            ChatHelper.sendTitle(player, ConfigurationPlugin.getConfiguration().getString("messages.success.logged.title-logged"), ConfigurationPlugin.getConfiguration().getString("messages.success.logged.subtitle-logged"), 5);
        }
        if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.logged.chat")) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.logged.chat-logged")));
        }
    }
}
