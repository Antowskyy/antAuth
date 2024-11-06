package pl.antowskyy.antauth.commands.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mindrot.jbcrypt.BCrypt;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.QueueManager;
import pl.antowskyy.antauth.managers.UserManager;
import pl.antowskyy.antauth.runnables.MessageLoginRunnable;

import java.util.List;

public class LoginCommand extends Command
{
    private final QueueManager queueManager;

    public LoginCommand(QueueManager queueManager) {
        super("login", null, "l");
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

        List<String> messages = ConfigurationPlugin.getConfiguration().getStringList("messages.success.logged");

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
