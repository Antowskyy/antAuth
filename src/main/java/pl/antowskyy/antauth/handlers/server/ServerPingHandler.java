package pl.antowskyy.antauth.handlers.server;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.*;
import net.md_5.bungee.event.EventHandler;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.helpers.ChatHelper;
import org.apache.commons.lang3.StringUtils;
import java.lang.management.*;
import java.util.UUID;

public class ServerPingHandler implements Listener
{
    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing response = event.getResponse();
        Players responsePlayers = response.getPlayers();
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        int playersOnline = AntAuth.getInstance().getProxy().getOnlineCount();
        PlayerInfo[] samples = new PlayerInfo[ConfigurationPlugin.getConfiguration().getStringList("motd.hoverLines").size()];
        int maxPlayers;
        if (ConfigurationPlugin.getConfiguration().getBoolean("motd.maxPlayersCount-status")) {
            maxPlayers = ConfigurationPlugin.getConfiguration().getInt("motd.maxPlayersCount");
        } else {
            maxPlayers = playersOnline + 1;
        }
        for (int index = 0; index < samples.length; index++) {
            String line = ConfigurationPlugin.getConfiguration().getStringList("motd.hoverLines").get(index);
            line = line.replace("{PROXY_CPU_USAGE}", String.format("%.2f", operatingSystemMXBean.getSystemLoadAverage()));
            line = line.replace("{COUNTER_PLAYERS_LIMIT}",
                    String.valueOf(playersOnline + 1));
            samples[index] = new PlayerInfo(ChatHelper.fixColor(line), UUID.randomUUID().toString());
        }
        responsePlayers.setOnline(playersOnline);
        responsePlayers.setMax(maxPlayers);
        responsePlayers.setSample(samples);
        response.setVersion(new Protocol(ChatHelper.fixColor(
                (ConfigurationPlugin.getConfiguration().getString("motd.third-line") + StringUtils.repeat(' ', 24))
                        + "&r" + ConfigurationPlugin.getConfiguration().getString("motd.playersInfo").replace("{ONLINE_PLAYERS}",
                        String.valueOf(playersOnline)).replace("{MAX_PLAYERS}", String.valueOf(maxPlayers))), 1337));
        response.setDescriptionComponent(new TextComponent(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("motd.first-line") + '\n' + ConfigurationPlugin.getConfiguration().getString("motd.second-line"))));
    }
}
