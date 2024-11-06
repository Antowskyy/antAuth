package pl.antowskyy.antauth.commands.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mindrot.jbcrypt.BCrypt;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.QueueManager;
import pl.antowskyy.antauth.managers.UserManager;
import pl.antowskyy.antauth.runnables.MessageLoginRunnable;

import java.util.List;

public class RegisterCommand extends Command
{
    private final QueueManager queueManager;

    public RegisterCommand(QueueManager queueManager) {
        super("register", null, "reg");
        this.queueManager = queueManager;
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

        List<String> messages = ConfigurationPlugin.getConfiguration().getStringList("messages.success.registered");

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
