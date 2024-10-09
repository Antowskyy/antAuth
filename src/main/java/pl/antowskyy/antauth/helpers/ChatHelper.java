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

    public static void handleTitleMessage(ProxiedPlayer player, String message) {

        if (message.length() < 7) {
            AntAuth.getInstance().getLogger().warning("Message too short: " + message);
            return;
        }

        String[] parts = message.substring(7).split("\\|");

        if (parts.length != 3) {
            AntAuth.getInstance().getLogger().warning("Invalid title format: " + message);
            return;
        }

        String title = parts[0].trim();
        String subtitle = parts[1].trim();

        int stay;
        try {
            stay = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            AntAuth.getInstance().getLogger().warning("Invalid numbers in title format: " + message);
            return;
        }

        sendTitle(player, title, subtitle, stay);
    }

    public static void handleChatMessage(ProxiedPlayer player, String message) {
        String chatMessage = message.substring(9);
        player.sendMessage(fixColor(chatMessage));
    }
}
