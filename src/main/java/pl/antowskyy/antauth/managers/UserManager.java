package pl.antowskyy.antauth.managers;

import pl.antowskyy.antauth.AntAuth;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import pl.antowskyy.antauth.data.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager
{
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    public static void createUser(UUID uuid, String name, String ip, Boolean premium) {
        User user = new User(uuid, name, ip, premium);
        users.put(user.getName(), user);
    }

    public static User getUser(String name) {
        return getUsers().parallelStream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static void deleteUser(User user) {
        users.remove(user.getName());
        AntAuth.getDatabase().executeUpdate("DELETE FROM `antauth_users` WHERE `uuid` = '" + user.getUUID() + "'");
    }

    public static boolean hasMaxAccounts(String lastIP) {
        int accounts = 0;
        for (User entry : getUsers()) {
            if (!entry.getLastIP().equals(lastIP)) continue;
            ++accounts;
        }
        return accounts > ConfigurationPlugin.getConfiguration().getInt("auth-settings.maxaccountip");
    }

    public static void loadUsers() {
        AntAuth.getDatabase().executeQuery("SELECT * FROM `antauth_users`", rs -> {
            try {
                while (rs.next()) {
                    User user = new User(rs);
                    users.put(user.getName(), user);
                }
                rs.close();
            }
            catch (Exception exception) {
                AntAuth.getInstance().getLogger().info("[antAuth-ERROR] An error occurred while loading users: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    public static Collection<User> getUsers() {
        return users.values();
    }
}
