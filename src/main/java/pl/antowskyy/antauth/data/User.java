package pl.antowskyy.antauth.data;

import lombok.Getter;
import pl.antowskyy.antauth.AntAuth;
import java.sql.*;
import java.util.UUID;

public class User
{
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private String password;
    @Getter
    private boolean premium;
    @Getter
    private String lastIP;
    @Getter
    private boolean registered;
    @Getter
    private boolean logged;


    public User(UUID uuid, String name, String lastIP, Boolean premium) {
        this.uuid = uuid;
        this.name = name;
        this.lastIP = lastIP;
        this.premium = premium;
        this.insert();
    }

    public User(ResultSet resultSet) throws SQLException {
        uuid = UUID.fromString(resultSet.getString("uuid"));
        name = resultSet.getString("name");
        password = resultSet.getString("password");
        premium = (resultSet.getInt("premium") == 1);
        lastIP = resultSet.getString("lastip");
        registered = (resultSet.getInt("registered") == 1);
        logged = (resultSet.getInt("logged") == 1);
    }

    public UUID getUUID() {
        return uuid;
    }


    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        update();
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
        update();
    }

    public void setPassword(String password) {
        this.password = password;
        update();
    }

    public void setPremium(boolean status) {
        this.premium = status;
        update();
    }

    public void setRegistered(boolean register) {
        this.registered = register;
        update();
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
        update();
    }

    public void insert() {
        AntAuth.getDatabase().executeUpdate("INSERT INTO `antauth_users` (uuid, name, password, premium, lastip, registered, logged) VALUES ('" + uuid.toString() + "', '" + name + "', '" + password + "','" + (isPremium() ? 1 : 0) + "','" + lastIP + "','" + (isRegistered() ? 1 : 0) + "','" + (isLogged() ? 1 : 0) + "')");
    }

    public void update() {
        AntAuth.getDatabase().executeUpdate("UPDATE `antauth_users` SET `uuid` = '" + uuid + "', `password` = '" + password + "', `premium` = '" + (isPremium() ? 1 : 0) + "', `lastip` = '" + lastIP + "', `registered` = '" + (isRegistered() ? 1 : 0) + "', `logged` = '" + (isLogged() ? 1 : 0) + "' WHERE `name` = '" + name + "';");
    }

}
