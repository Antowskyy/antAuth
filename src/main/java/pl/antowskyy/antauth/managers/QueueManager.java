package pl.antowskyy.antauth.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.yaml.snakeyaml.Yaml;
import pl.antowskyy.antauth.data.Queue;
import java.io.InputStream;
import java.util.*;

public class QueueManager {
    private final List<Queue> queue = new LinkedList<>();
    private final Map<String, Integer> priorities;

    public QueueManager() {
        priorities = loadPrioritiesFromConfig();
    }

    private Map<String, Integer> loadPrioritiesFromConfig() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            Map<String, Object> data = yaml.load(in);
            Map<String, Object> authSettings = (Map<String, Object>) data.get("auth-settings");
            Map<String, Object> queueConfig = (Map<String, Object>) authSettings.get("queue");
            return (Map<String, Integer>) queueConfig.get("priorities");
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void addPlayerToQueue(ProxiedPlayer player) {
        if (isPlayerInQueue(player)) {
            return;
        }
        int priority = getPlayerPriority(player);
        queue.add(new Queue(player, priority));
        sortQueue();
    }

    public void pollPlayerFromQueue() {
        if (!queue.isEmpty()) {
            queue.remove(0);
        }
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public List<Queue> getQueueAsList() {
        return new LinkedList<>(queue);
    }

    private void sortQueue() {
        queue.sort(Comparator.comparingInt(Queue::getPriority).reversed());
    }

    public boolean isPlayerInQueue(ProxiedPlayer player) {
        return queue.stream().anyMatch(queuedPlayer -> queuedPlayer.getPlayer().equals(player));
    }

    public Queue getPlayerQueue(ProxiedPlayer player) {
        return queue.stream()
                .filter(queuedPlayer -> queuedPlayer.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    private int getPlayerPriority(ProxiedPlayer player) {
        for (Map.Entry<String, Integer> entry : priorities.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                return entry.getValue();
            }
        }
        return priorities.getOrDefault("queue.default", 3);
    }
}
