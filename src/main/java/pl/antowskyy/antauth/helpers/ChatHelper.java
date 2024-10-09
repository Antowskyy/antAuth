package pl.antowskyy.antauth.helpers;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.antowskyy.antauth.AntAuth;
import java.util.regex.*;

public class ChatHelper
{
    public static String fixColor(String text) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        for (Matcher matcher = pattern.matcher(text); matcher.find(); matcher = pattern.matcher(text)) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }
        return ChatColor.translateAlternateColorCodes('&', text).replace(">>", "»").replace("<<", "«");
    }

    public static void sendTitle(ProxiedPlayer proxiedPlayer, String titleMsg, String subtitleMsg, int stay) {
        if (!proxiedPlayer.isConnected())
            return;
        Title title = AntAuth.getInstance().getProxy().createTitle();
        title.reset();
        title.title(new TextComponent(fixColor(titleMsg)));
        title.subTitle(new TextComponent(fixColor(subtitleMsg)));
        title.stay(stay);
        title.send(proxiedPlayer);
    }

    public static void sendActionbar(ProxiedPlayer player, String message) {
        if (!player.isConnected()) {
            return;
        }
        player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fixColor(message)));
    }
}
