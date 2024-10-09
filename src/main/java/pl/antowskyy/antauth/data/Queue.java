package pl.antowskyy.antauth.data;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Getter
public class Queue {

    private final ProxiedPlayer player;
    private final int priority;
    private boolean isAttemptingConnection;

    public Queue(ProxiedPlayer player, int priority) {
        this.player = player;
        this.priority = priority;
        this.isAttemptingConnection = false;
    }

    public void setAttemptingConnection(boolean attemptingConnection) {
        this.isAttemptingConnection = attemptingConnection;
    }
}
