package pl.antowskyy.antauth.commands.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.UserManager;

public class UnregisterCommand extends Command
{
    public UnregisterCommand() {
        super("unregister", null, "unreg");
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
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.usage.user.unregister")));
            return;
        }
        if (!args[0].equals(args[1])) {
            sender.sendMessage(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.error.passwordsNotsame")));
            return;
        }
        user.setLogged(false);
        user.setRegistered(false);

        ProxiedPlayer player = AntAuth.getInstance().getProxy().getPlayer(sender.getName());
        player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.success.commands.user.unregister")));
        UserManager.deleteUser(user);
    }
}
