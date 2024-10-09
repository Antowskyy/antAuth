package pl.antowskyy.antauth.runnables;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.Queue;
import pl.antowskyy.antauth.helpers.ChatHelper;
import pl.antowskyy.antauth.managers.QueueManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueueRunnable implements Runnable {

    private final QueueManager queueManager;
    private final String targetServer;
    private final String authServer;
    private final int connectTimeout;
    private final int maxRetries;

    public QueueRunnable(QueueManager queueManager, String authServer, String targetServer, int connectTimeout, int maxRetries) {
        this.queueManager = queueManager;
        this.authServer = authServer;
        this.targetServer = targetServer;
        this.connectTimeout = connectTimeout;
        this.maxRetries = maxRetries;
    }

    @Override
    public void run() {
        if (!queueManager.isQueueEmpty()) {
            List<Queue> playersInQueue = queueManager.getQueueAsList();

            for (int i = 0; i < playersInQueue.size(); i++) {
                Queue queuedPlayer = playersInQueue.get(i);
                ProxiedPlayer player = queuedPlayer.getPlayer();

                if (!player.getServer().getInfo().getName().equalsIgnoreCase(authServer)) {
                    continue;
                }

                if (!queueManager.isPlayerInQueue(player)) {
                    continue;
                }

                if (queuedPlayer.isAttemptingConnection()) {
                    continue;
                }

                if (i == 0) {
                    sendActionBar(player, ConfigurationPlugin.getConfiguration().getString("messages.queue.sending"));
                    attemptConnection(player, 1);
                } else {
                    int position = i + 1;
                    sendActionBar(player, ConfigurationPlugin.getConfiguration().getString("messages.queue.waiting").replace("{POSITION}", "" + position));
                }
            }
        }
    }

    private void attemptConnection(ProxiedPlayer player, int retryCount) {
        Queue queuedPlayer = queueManager.getPlayerQueue(player);
        if (queuedPlayer == null || queuedPlayer.isAttemptingConnection()) {
            return;
        }

        queuedPlayer.setAttemptingConnection(true);

        if (retryCount > maxRetries) {
            player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.queue.max-retries-kick").replace("{RETRYCOUNT}", "" + retryCount)));
            queueManager.pollPlayerFromQueue();
            queuedPlayer.setAttemptingConnection(false);
            return;
        }

        if (player.isConnected()) {
            player.connect(AntAuth.getInstance().getProxy().getServerInfo(targetServer), (result, error) -> {
                if (result) {
                    sendActionBar(player, ConfigurationPlugin.getConfiguration().getString("messages.queue.success-connect"));
                    queueManager.pollPlayerFromQueue();
                    queuedPlayer.setAttemptingConnection(false);
                } else {
                    handleFailedConnection(player, retryCount);
                }
            });
        } else {
            queuedPlayer.setAttemptingConnection(false);
        }
    }

    private void handleFailedConnection(ProxiedPlayer player, int retryCount) {
        Queue queuedPlayer = queueManager.getPlayerQueue(player);
        if (queuedPlayer == null) {
            return;
        }

        if (retryCount < maxRetries) {
            sendActionBar(player, ConfigurationPlugin.getConfiguration().getString("messages.queue.connection-failed"));
            AntAuth.getInstance().getProxy().getScheduler().schedule(AntAuth.getInstance(), () -> {
                queuedPlayer.setAttemptingConnection(false);
                attemptConnection(player, retryCount + 1);
            }, connectTimeout, TimeUnit.SECONDS);
        } else {
            queueManager.pollPlayerFromQueue();
            player.disconnect(ChatHelper.fixColor(ConfigurationPlugin.getConfiguration().getString("messages.queue.max-retries-kick").replace("{RETRYCOUNT}", "" + retryCount)));
            queuedPlayer.setAttemptingConnection(false);
        }
    }


    private void sendActionBar(ProxiedPlayer player, String string) {
        ChatHelper.sendActionbar(player, string);
    }
}
