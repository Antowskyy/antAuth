package pl.antowskyy.antauth.database;

import com.zaxxer.hikari.HikariDataSource;
import pl.antowskyy.antauth.configuration.ConfigurationPlugin;
import java.sql.*;
import java.util.function.Consumer;

public class Database
{
    private final HikariDataSource dataSource;

    public Database()
    {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl("jdbc:mysql://" + ConfigurationPlugin.getConfiguration().getString("database.host") + ":" + ConfigurationPlugin.getConfiguration().getString("database.port") + "/" + ConfigurationPlugin.getConfiguration().getString("database.base"));
        this.dataSource.setUsername(ConfigurationPlugin.getConfiguration().getString("database.user"));
        this.dataSource.setPassword(ConfigurationPlugin.getConfiguration().getString("database.password"));
        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);
        this.dataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        this.dataSource.setConnectionTimeout(15000L);
        this.dataSource.setMaximumPoolSize(5);
    }

    public void close() {
        this.dataSource.close();
    }

    public void executeQuery(String query, Consumer<ResultSet> action) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {
            action.accept(result);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeUpdate(String query) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (statement == null) {
                return;
            }
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
