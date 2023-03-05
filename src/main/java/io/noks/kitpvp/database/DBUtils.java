package io.noks.kitpvp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.zaxxer.hikari.HikariDataSource;

import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;
import io.noks.kitpvp.managers.caches.Stats;

public class DBUtils {
	private boolean connected = false;

	private final String address;
	private final String name;
	private final String username;
	private final String password;

	private HikariDataSource hikari;

	private final String SAVE = "UPDATE players SET kills=?, death=?, bestks=?, bounty=?, hascompass=?, swordslot=?, itemslot=?, compassslot=?, money=? WHERE uuid=?";
	private final String INSERT = "INSERT INTO players VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=?";
	private final String SELECT = "SELECT kills,death,bestks,bounty,hascompass,swordslot,itemslot,compassslot,money FROM players WHERE uuid=?";

	public DBUtils(String address, String name, String user, String password) {
		this.address = address;
		this.name = name;
		this.username = user;
		this.password = password;
		this.connectDatabase();
	}
	
	private void connectDatabase() {
		try {
			this.hikari = new HikariDataSource();
			this.hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
			this.hikari.addDataSourceProperty("serverName", this.address);
			this.hikari.addDataSourceProperty("port", "3306");
			this.hikari.addDataSourceProperty("databaseName", this.name);
			this.hikari.addDataSourceProperty("user", this.username);
			this.hikari.addDataSourceProperty("password", this.password);
			this.hikari.addDataSourceProperty("autoReconnect", Boolean.valueOf(true));
			this.hikari.addDataSourceProperty("cachePrepStmts", Boolean.valueOf(true));
			this.hikari.addDataSourceProperty("prepStmtCacheSize", Integer.valueOf(250));
			this.hikari.addDataSourceProperty("prepStmtCacheSqlLimit", Integer.valueOf(2048));
			this.hikari.addDataSourceProperty("useServerPrepStmts", Boolean.valueOf(true));
			this.hikari.addDataSourceProperty("cacheResultSetMetadata", Boolean.valueOf(true));
			this.hikari.setMaximumPoolSize(20);
			this.hikari.setConnectionTimeout(30000L);
			this.connected = true;
			createTable();
		} catch (Exception exception) {}
	}
	
	private void createTable() {
		if (!isConnected()) {
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			PreparedStatement statement = (PreparedStatement) connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS players(uuid varchar(36), kills int(11), death int(11), bestks int(11), bounty int(11), hascompass boolean, swordslot int(11), itemslot int(11), compassslot int(11), money int(11), PRIMARY KEY(`uuid`), UNIQUE(`uuid`));");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void loadPlayer(UUID uuid) {
		if (!isConnected()) {
			new PlayerManager(uuid).giveMainItem();
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			PreparedStatement statement = connection.prepareStatement(INSERT);

			statement.setString(1, uuid.toString());
			statement.setInt(2, 0);
			statement.setInt(3, 0);
			statement.setInt(4, 0);
			statement.setInt(5, 0); // Bounty
			statement.setBoolean(6, Boolean.TRUE.booleanValue());
			statement.setInt(7, 0);
			statement.setInt(8, 1);
			statement.setInt(9, 8);
			statement.setInt(10, 0);
			statement.setString(11, uuid.toString());
			statement.executeUpdate();
			statement.close();

			statement = connection.prepareStatement(SELECT);
			statement.setString(1, uuid.toString());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				new PlayerManager(uuid, 
						new Stats(result.getInt("kills"), result.getInt("death"), result.getInt("bestks"), result.getInt("bounty")), 
						new PlayerSettings(result.getBoolean("hascompass"), result.getInt("swordslot"), result.getInt("itemslot"), result.getInt("compassslot")), 
						new Economy(result.getInt("money"))).giveMainItem();
			}
			statement.close();
			result.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void savePlayer(PlayerManager pm) {
		if (!isConnected()) {
			pm.remove();
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			PreparedStatement statement = connection.prepareStatement(SAVE);

			statement.setInt(1, pm.getStats().getKills());
			statement.setInt(2, pm.getStats().getDeaths());
			statement.setInt(3, pm.getStats().getBestKillStreak());
			statement.setInt(4, pm.getStats().getBestKillStreak());
			statement.setInt(5, pm.getStats().getBounty());
			statement.setBoolean(6, pm.getSettings().hasCompass());
			statement.setInt(7, pm.getSettings().getSlot(SlotType.SWORD));
			statement.setInt(8, pm.getSettings().getSlot(SlotType.ITEM));
			statement.setInt(9, pm.getSettings().getSlot(SlotType.COMPASS));
			statement.setInt(10, pm.getEconomy().getMoney());
			statement.setString(11, pm.getPlayerUUID().toString());
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
					pm.remove();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public HikariDataSource getHikari() {
		return this.hikari;
	}

	public boolean isConnected() {
		return this.connected;
	}
}
