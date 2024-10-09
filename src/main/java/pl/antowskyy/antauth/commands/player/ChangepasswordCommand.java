package pl.antowskyy.antauth.commands.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.mindrot.jbcrypt.BCrypt;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;

public class ChangepasswordCommand extends Command
{
    public ChangepasswordCommand() {
        super("changeepassword", null, "zmienhaslo");
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
        if (args.length != 2) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.user.changepassword")));
            return;
        }
        if (args[0].equals(args[1])) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.passwordSame")));
            return;
        }
        if (!BCrypt.checkpw(args[0], user.getPassword())) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.passwordIncorrect")));
            return;
        }
        user.setPassword(BCrypt.hashpw(args[1], BCrypt.gensalt()));
        sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.user.changepassword")));
    }
}
