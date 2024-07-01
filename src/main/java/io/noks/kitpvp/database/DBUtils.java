package io.noks.kitpvp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.zaxxer.hikari.HikariDataSource;

import io.noks.kitpvp.enums.PerksEnum;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Perks;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;
import io.noks.kitpvp.managers.caches.Stats;

public class DBUtils {
	private Map<RefreshType, Map<UUID, Integer>> leaderboard = new HashMap<RefreshType, Map<UUID, Integer>>(RefreshType.values().length - 2);
	private boolean connected = false;

	private final String address;
	private final String name;
	private final String username;
	private final String password;

	private HikariDataSource hikari;

	private final String SAVE = "UPDATE stats SET kills=?, death=?, bestks=?, bounty=?, scoreboard=?, swordslot=?, itemslot=?, money=?, firstperk=?, secondperk=?, thirdperk=? WHERE uuid=?";
	private final String INSERT = "INSERT INTO stats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=?";
	private final String SELECT = "SELECT kills,death,bestks,bounty,scoreboard,swordslot,itemslot,money,firstperk,secondperk,thirdperk FROM stats WHERE uuid=?";

	public DBUtils(String address, String name, String user, String password) {
		this.address = address;
		this.name = name;
		this.username = user;
		this.password = password;
		this.connectDatabase();
		for (RefreshType type : RefreshType.values()) {
			if (!type.canBeScanned()) {
				continue;
			}
			updateLeaderboard(type);
		}
	}
	
	public void updateLeaderboard(RefreshType type) {
		this.leaderboard.put(type, scanLeaderboard(type));
	}
	
	private void connectDatabase() {
		try {
			this.hikari = new HikariDataSource();
			this.hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
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
			System.out.println("NOT CONNECTED");
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			final PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stats(uuid VARCHAR(36), kills INT, death INT, bestks INT, bounty INT, scoreboard TINYINT(1), swordslot INT, itemslot INT, money INT, firstperk VARCHAR(16), secondperk VARCHAR(20), thirdperk VARCHAR(18), PRIMARY KEY(`uuid`), UNIQUE(`uuid`));");
			statement.executeUpdate();
			statement.close();
			System.out.println("CREATED TABLE");
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
			new PlayerManager(uuid);
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
			statement.setBoolean(6, true);
			statement.setInt(7, 0);
			statement.setInt(8, 1);
			statement.setInt(9, 0); // Money
			statement.setString(10, "none");
			statement.setString(11, "none");
			statement.setString(12, "none");
			statement.setString(13, uuid.toString());
			statement.executeUpdate();
			statement.close();

			statement = connection.prepareStatement(SELECT);
			statement.setString(1, uuid.toString());
			final ResultSet result = statement.executeQuery();
			if (result.next()) {
				new PlayerManager(uuid, 
						new Stats(result.getInt("kills"), result.getInt("death"), result.getInt("bestks"), result.getInt("bounty")), 
						new PlayerSettings(result.getBoolean("scoreboard"), result.getInt("swordslot"), result.getInt("itemslot")), 
						new Economy(result.getInt("money")),
						new Perks(new PerksEnum[] {PerksEnum.getPerksFromName(result.getString("firstperk")), PerksEnum.getPerksFromName(result.getString("secondperk")), PerksEnum.getPerksFromName(result.getString("thirdperk"))}));
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
			pm.drop();
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			final PreparedStatement statement = connection.prepareStatement(SAVE);

			statement.setInt(1, pm.getStats().getKills());
			statement.setInt(2, pm.getStats().getDeaths());
			statement.setInt(3, pm.getStats().getBestKillStreak());
			statement.setInt(4, pm.getStats().getBounty());
			statement.setBoolean(5, pm.getSettings().hasScoreboardEnabled());
			statement.setInt(6, pm.getSettings().getSlot(SlotType.SWORD));
			statement.setInt(7, pm.getSettings().getSlot(SlotType.ITEM));
			statement.setInt(8, pm.getEconomy().getMoney());
			statement.setString(9, "none"/*pm.getActivePerks().first().getName()*/); // TODO
			statement.setString(10, "none"/*pm.getActivePerks().second().getName()*/); // TODO
			statement.setString(11, "none"/*pm.getActivePerks().third().getName()*/); // TODO
			statement.setString(12, pm.getPlayerUUID().toString());
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
					pm.drop();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public Map<UUID, Integer> getLeaderboard(RefreshType type){
		return this.leaderboard.get(type);
	}
	private Map<UUID, Integer> scanLeaderboard(RefreshType type) {
		return null;
		/*if (!isConnected()) {
			return null;
		}
		final Map<UUID, Integer> map = new LinkedHashMap<UUID, Integer>(10);
		final String selectLine = "SELECT uuid," + type.getName().toLowerCase() + " FROM stats ORDER BY " + type.getName().toLowerCase() + " DESC LIMIT 10";
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			final PreparedStatement statement = connection.prepareStatement(selectLine);
			final ResultSet result = statement.executeQuery();
			while (result.next()) {
				UUID uuid = UUID.fromString(result.getString("uuid"));
				int stat = result.getInt(type.getName().toLowerCase());
				map.put(uuid, stat);
			}
			result.close();
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
		return map.isEmpty() ? Collections.emptyMap() : map;*/
	}

	public HikariDataSource getHikari() {
		return this.hikari;
	}

	public boolean isConnected() {
		return this.connected;
	}
}
