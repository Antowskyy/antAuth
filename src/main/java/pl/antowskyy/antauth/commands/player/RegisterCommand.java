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

public class RegisterCommand extends Command
{
    public RegisterCommand() {
        super("register", null, "reg");
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
        if (user.isRegistered()) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.userRegistered")));
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.user.register")));
            return;
        }
        if (UserManager.hasMaxAccounts(user.getLastIP())) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.maxAccountsIP")));
            return;
        }
        if (!args[0].equals(args[1])) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.passwordsNotsame")));
            return;
        }
        user.setLogged(true);
        user.setRegistered(true);
        user.setPassword(BCrypt.hashpw(args[0], BCrypt.gensalt()));
        ProxiedPlayer player = (ProxiedPlayer)sender;
        MessageLoginRunnable.players.remove(player);
        player.connect(AntAuth.getInstance().getProxy().getServerInfo(ConfigurationPlugin.getConfiguration().getString("auth-settings.lobby-server")));
        if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.registered.title")) {
            ChatHelper.sendTitle(player, ConfigurationPlugin.getConfiguration().getString("messages.success.registered.title-registered"), ConfigurationPlugin.getConfiguration().getString("messages.success.registered.subtitle-registered"), 5);
        }
        if (ConfigurationPlugin.getConfiguration().getBoolean("auth-settings.messages.registered.chat")) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.registered.chat-registered")));
        }
    }
}
