package io.noks.kitpvp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zaxxer.hikari.HikariDataSource;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.GuildRank;
import io.noks.kitpvp.enums.PerksEnum;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.PlayerManager;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Guild;
import io.noks.kitpvp.managers.caches.Perks;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.PlayerSettings.SlotType;
import io.noks.kitpvp.managers.caches.Stats;

public class DBUtils {
	private final Map<RefreshType, Map<UUID, Integer>> leaderboard = new HashMap<RefreshType, Map<UUID, Integer>>(RefreshType.values().length - 3);
	private boolean connected = false;

	private final String address;
	private final String name;
	private final String username;
	private final String password;
	private HikariDataSource hikari;
	private final Main main;
	private final ExecutorService executorService;

	public DBUtils(String address, String name, String user, String password, Main main) {
		this.address = address;
		this.name = name;
		this.username = user;
		this.password = password;
		this.main = main;
		this.connectDatabase();
		this.executorService = (this.connected ? Executors.newCachedThreadPool() : null);
		/*for (RefreshType type : RefreshType.values()) {
			if (!type.canBeScanned()) {
				continue;
			}
			updateLeaderboard(type);
		}*/
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
			return;
		}
		Connection connection = null;
		try {
			connection = this.hikari.getConnection();
			Statement statement = connection.createStatement();
			// STATS
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS stats(uuid VARCHAR(36) PRIMARY KEY, nickname VARCHAR(16), kills INT, death INT, bestks INT, bounty INT, UNIQUE(`uuid`));");
			// ECONOMY
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS economy(uuid VARCHAR(36) PRIMARY KEY, nickname VARCHAR(16), money INT, UNIQUE(`uuid`));");
			// SETTINGS
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS settings(uuid VARCHAR(36) PRIMARY KEY, scoreboard TINYINT(1), swordslot INT, itemslot INT, UNIQUE(`uuid`));");
			// PERK
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS perks(uuid VARCHAR(36) PRIMARY KEY, firstperk VARCHAR(16), secondperk VARCHAR(20), thirdperk VARCHAR(18), UNIQUE(`uuid`));");
			// GUILD START
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS guilds(name VARCHAR(16) PRIMARY KEY, owner VARCHAR(36), tag VARCHAR(4), money INT, UNIQUE(`name`));");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS members(uuid VARCHAR(36) PRIMARY KEY, nickname VARCHAR(16), guild_rank VARCHAR(9), UNIQUE(`uuid`));");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS guild_members(guild_name VARCHAR(16), member_uuid VARCHAR(36), PRIMARY KEY (`guild_name`, `member_uuid`), "
																													 + "FOREIGN KEY (guild_name) REFERENCES guilds(name), "
																													 + "FOREIGN KEY (member_uuid) REFERENCES members(uuid), "
																													 + "UNIQUE(`member_uuid`));");
			// GUILD END
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
	
	public HikariDataSource getHikari() {
		return this.hikari;
	}
	public boolean isConnected() {
		return this.connected;
	}
	public void close() {
		if(isConnected()) {
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			this.hikari.close();
			this.connected = false;
		}
	}

	public void loadPlayer(final UUID uuid) {
		if (!isConnected()) {
			new PlayerManager(uuid);
			return;
		}
		CompletableFuture.runAsync(() -> {
			Connection connection = null;
			try {
				connection = this.hikari.getConnection();
				final String name = this.main.getServer().getPlayer(uuid).getName();
				final Stats stats = this.loadPlayerStats(uuid, name, connection);
				final PlayerSettings settings = this.loadPlayerSettings(uuid, connection);
				final Economy eco = this.loadPlayerEconomy(uuid, name, connection);
				final Perks perks = this.loadPlayerPerks(uuid, connection);
				final Guild guild = (this.is_Part_Of_A_Guild(uuid) ? this.loadGuildByPlayer(uuid) : null);
				new PlayerManager(uuid, stats, settings, eco, perks, guild); 
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
		}, executorService);
	}
	
	private Stats loadPlayerStats(final UUID uuid, final String name, final Connection connection) throws SQLException {
		Stats stats = new Stats();
		try (PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM stats WHERE uuid=?")) {
	        selectStatement.setString(1, uuid.toString());
	        try (ResultSet resultSet = selectStatement.executeQuery()) {
	            if (resultSet.next() && resultSet.getInt("count") == 0) {
	                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO stats VALUES(?, ?, ?, ?, ?, ?)")) {
	                	insertStatement.setString(1, uuid.toString());
	                	insertStatement.setString(2, name);
	                	insertStatement.setInt(3, 0);
	                	insertStatement.setInt(4, 0);
	                	insertStatement.setInt(5, 0);
	                	insertStatement.setInt(6, 0);
	                    insertStatement.executeUpdate();
	                    insertStatement.close();
	                }
	            }
	            resultSet.close();
	        }
	        selectStatement.close();
	    }
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM stats WHERE uuid=?")){
			statement.setString(1, uuid.toString());
			try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    stats = new Stats(result.getInt("kills"), result.getInt("death"), result.getInt("bestks"), result.getInt("bounty"));
                }
                result.close();
            }
			statement.close();
		}
		return stats;
	}
	
	private PlayerSettings loadPlayerSettings(final UUID uuid, final Connection connection) throws SQLException {
		PlayerSettings settings = new PlayerSettings();
		try (PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM settings WHERE uuid=?")) {
	        selectStatement.setString(1, uuid.toString());
	        try (ResultSet resultSet = selectStatement.executeQuery()) {
	            if (resultSet.next() && resultSet.getInt("count") == 0) {
	                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO settings VALUES(?, ?, ?, ?)")) {
	                	insertStatement.setString(1, uuid.toString());
	                	insertStatement.setBoolean(2, true);
	                	insertStatement.setInt(3, 0);
	                	insertStatement.setInt(4, 1);
	                    insertStatement.executeUpdate();
	                    insertStatement.close();
	                }
	            }
	            resultSet.close();
	        }
	        selectStatement.close();
	    }
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM settings WHERE uuid=?")){
			statement.setString(1, uuid.toString());
			try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    settings = new PlayerSettings(result.getBoolean("scoreboard"), result.getInt("swordslot"), result.getInt("itemslot"));
                }
                result.close();
            }
			statement.close();
		}
		return settings;
	}
	
	private Economy loadPlayerEconomy(final UUID uuid, final String name, final Connection connection) throws SQLException {
		Economy eco = new Economy();
		try (PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM economy WHERE uuid=?")) {
	        selectStatement.setString(1, uuid.toString());
	        try (ResultSet resultSet = selectStatement.executeQuery()) {
	            if (resultSet.next() && resultSet.getInt("count") == 0) {
	                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO economy VALUES(?, ?, ?)")) {
	                	insertStatement.setString(1, uuid.toString());
	                	insertStatement.setString(2, name);
	                	insertStatement.setInt(3, 0);
	                    insertStatement.executeUpdate();
	                    insertStatement.close();
	                }
	            }
	            resultSet.close();
	        }
	        selectStatement.close();
	    }
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM economy WHERE uuid=?")){
			statement.setString(1, uuid.toString());
			try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    eco = new Economy(result.getInt("money"));
                }
                result.close();
            }
			statement.close();
		}
		return eco;
	}
	private Perks loadPlayerPerks(final UUID uuid, final Connection connection) throws SQLException {
		Perks perks = new Perks();
		try (PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM perks WHERE uuid=?")) {
	        selectStatement.setString(1, uuid.toString());
	        try (ResultSet resultSet = selectStatement.executeQuery()) {
	            if (resultSet.next() && resultSet.getInt("count") == 0) {
	                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO perks VALUES(?, ?, ?, ?)")) {
	                    insertStatement.setString(1, uuid.toString());
	                    insertStatement.setString(2, "none");
	                    insertStatement.setString(3, "none");
	                    insertStatement.setString(4, "none");
	                    insertStatement.executeUpdate();
	                    insertStatement.close();
	                }
	            }
	            resultSet.close();
	        }
	        selectStatement.close();
	    }
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM perks WHERE uuid=?")){
			statement.setString(1, uuid.toString());
			try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    perks = new Perks(new PerksEnum[] {PerksEnum.getPerksFromName(result.getString("firstperk")), PerksEnum.getPerksFromName(result.getString("secondperk")), PerksEnum.getPerksFromName(result.getString("thirdperk"))});
                }
                result.close();
            }
			statement.close();
		}
		return perks;
	}
	
	public void savePlayer(final PlayerManager pm) {
		if (!isConnected()) {
			pm.drop();
			return;
		}
		CompletableFuture.runAsync(() -> {
			Connection connection = null;
			try {
				connection = this.hikari.getConnection();
				final UUID uuid = pm.getPlayerUUID();
				final String name = pm.getPlayer().getName();
				this.savePlayerStats(uuid, name, pm.getStats(), connection);
				this.savePlayerSettings(uuid, pm.getSettings(), connection);
				this.savePlayerEconomy(uuid, name, pm.getEconomy(), connection);
				this.savePlayerPerks(uuid, pm.getActivePerks(), connection);
				if (pm.isPartOfAGuild()) {
					this.try_Unload_N_Save_Guild(pm.getGuild());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				pm.drop();
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		}, executorService);
	}
	
	private void savePlayerStats(final UUID uuid, final String name, final Stats stats, final Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE stats SET nickname=?, kills=?, death=?, bestks=?, bounty=? WHERE uuid=?")) {
        	statement.setString(1, name);
        	statement.setInt(2, stats.getKills());
            statement.setInt(3, stats.getDeaths());
            statement.setInt(4, stats.getBestKillStreak());
            statement.setInt(5, stats.getBounty());
            statement.setString(6, uuid.toString());
            statement.executeUpdate();
            statement.close();
        }
    }
	private void savePlayerSettings(final UUID uuid, final PlayerSettings settings, final Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE settings SET scoreboard=?, swordslot=?, itemslot=? WHERE uuid=?")) {
            statement.setBoolean(1, settings.hasScoreboardEnabled());
            statement.setInt(2, settings.getSlot(SlotType.SWORD));
            statement.setInt(3, settings.getSlot(SlotType.ITEM));
            statement.setString(4, uuid.toString());
            statement.executeUpdate();
            statement.close();
        }
    }
	private void savePlayerEconomy(final UUID uuid, final String name, final Economy eco, final Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE economy SET nickname=?, money=? WHERE uuid=?")) {
        	statement.setString(1, name);
        	statement.setInt(2, eco.getMoney());
            statement.setString(3, uuid.toString());
            statement.executeUpdate();
            statement.close();
        }
    }
	private void savePlayerPerks(final UUID uuid, final Perks perks, final Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE perks SET firstperk=?, secondperk=?, thirdperk=? WHERE uuid=?")) {
            statement.setString(1, "none");
            statement.setString(2, "none");
            statement.setString(3, "none");
            statement.setString(4, uuid.toString());
            statement.executeUpdate();
            statement.close();
        }
    }
	
	// Leaderboard
	public Map<UUID, Integer> getLeaderboard(RefreshType type){
		return this.leaderboard.get(type);
	}
	private Map<UUID, Integer> scanLeaderboard(RefreshType type) {
		if (!isConnected()) {
			return null;
		}
		final Map<UUID, Integer> map = new LinkedHashMap<UUID, Integer>(10);
		final String selectLine = "SELECT uuid," + type.getName().toLowerCase() + " FROM stats ORDER BY " + type.getName().toLowerCase() + " DESC LIMIT 10";
		CompletableFuture.runAsync(() -> {
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
		}, executorService).join();
		return map.isEmpty() ? Collections.emptyMap() : map;
	}
	
	// GUILDS
	private Guild loadGuildByPlayer(UUID playerUUID) {
		if (!isConnected()) {
			return null;
		}
		Guild guild = Guild.getGuildByPlayer(playerUUID);
		if (guild != null) {
			return guild;
		}
	    Connection connection = null;
	    try {
	        connection = this.hikari.getConnection();
	        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM guilds WHERE owner=?")) {
	            statement.setString(1, playerUUID.toString());
	            try (ResultSet resultSet = statement.executeQuery()) {
	                if (resultSet.next()) {
	                    final String guildName = resultSet.getString("name");
	                    guild = this.loadGuildByName(guildName);
	                }
	                resultSet.close();
	            }
	            statement.close();
	        }
	        if (guild == null) {
		        try (PreparedStatement statement = connection.prepareStatement("SELECT guild_name FROM guild_members WHERE member_uuid=?")) {
		            statement.setString(1, playerUUID.toString());
		            try (ResultSet resultSet = statement.executeQuery()) {
		                if (resultSet.next()) {
		                    final String guildName = resultSet.getString("guild_name");
		                    guild = this.loadGuildByName(guildName);
		                }
		                resultSet.close();
		            }
		            statement.close();
		        }
	        }
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
		return guild;
	}
	
	private boolean is_Part_Of_A_Guild(UUID uuid) {
		if (!isConnected()) {
			return false;
		}
		boolean isMember = false;
	    Connection connection = null;
	    try {
	        connection = this.hikari.getConnection();
	        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM guilds WHERE owner=?")) {
	            statement.setString(1, uuid.toString());
	            try (ResultSet resultSet = statement.executeQuery()) {
	                if (resultSet.next() && resultSet.getInt("count") > 0) {
	                    isMember = true;
	                }
	                resultSet.close();
	            }
	            statement.close();
	        }
	        if (!isMember) {
		        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM guild_members WHERE member_uuid=?")) {
		            statement.setString(1, uuid.toString());
		            try (ResultSet resultSet = statement.executeQuery()) {
		                if (resultSet.next() && resultSet.getInt("count") > 0) {
		                    isMember = true;
		                }
		                resultSet.close();
		            }
		            statement.close();
		        }
	        }
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
	    return isMember;
	}
	
	// SPAGHET
	private Guild loadGuildByName(String name) {
	    if (!isConnected()) {
	        return null;
	    }
	    if (Guild.guildList.containsKey(name)) {
	        return Guild.getGuildFromName(name);
	    }
	    Guild guild = null;
	    Connection connection = null;
	    try {
	        connection = this.hikari.getConnection();
	        try (PreparedStatement guildStatement = connection.prepareStatement("SELECT * FROM guilds WHERE name=?")) {
	            guildStatement.setString(1, name);
	            try (ResultSet guildResult = guildStatement.executeQuery()) {
	                if (guildResult.next()) {
	                    final UUID leaderUUID = UUID.fromString(guildResult.getString("owner"));
	                    final String motd = guildResult.getString("motd");
	                    final String tag = guildResult.getString("tag");
	                    final int money = guildResult.getInt("money");
	                    final boolean open = guildResult.getBoolean("open");

	                    final Map<UUID, GuildRank> membersList = new LinkedHashMap<>();
	                    try (PreparedStatement memberStatement = connection.prepareStatement("SELECT * FROM guild_members WHERE guild_name=?")) {
	                        memberStatement.setString(1, name);
	                        try (ResultSet memberResult = memberStatement.executeQuery()) {
	                            while (memberResult.next()) {
	                                UUID memberUUID = UUID.fromString(memberResult.getString("member_uuid"));
	                                GuildRank rank = GuildRank.valueOf(memberResult.getString("guild_rank"));
	                                    
	                                membersList.put(memberUUID, rank);
	                            }
	                            memberResult.close();
	                        }
	                        memberStatement.close();
	                    }
	                    guild = new Guild(name, leaderUUID, motd, tag, membersList, money, open);
	                }
	                guildResult.close();
	            }
	            guildStatement.close();
	        }
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
	    return guild;
	}
	
	private void try_Unload_N_Save_Guild(Guild guild) {
		if (!isConnected()) {
			return;
		}
		if (guild.isAnyMemberOnline()) {
			return;
		}
	    Connection connection = null;
	    // TODO: need to DELETE removed members from members table
	    try {
	        connection = this.hikari.getConnection();
	        try (PreparedStatement guildStatement = connection.prepareStatement("UPDATE guilds SET owner=?, motd=?, tag=?, money=?, open=? WHERE name=?")) {
	            guildStatement.setString(1, guild.leaderUUID().toString());
	            guildStatement.setString(2, guild.getMOTD());
	            guildStatement.setString(3, guild.getTag());
	            guildStatement.setInt(4, guild.getMoney());
	            guildStatement.setBoolean(5, guild.isOpen());
	            guildStatement.setString(6, guild.getName());
	            guildStatement.executeUpdate();
	            guildStatement.close();
	        }
	        try (PreparedStatement deleteMembersStatement = connection.prepareStatement("DELETE FROM guild_members WHERE guild_name=?")) {
	            deleteMembersStatement.setString(1, guild.getName());
	            deleteMembersStatement.executeUpdate();
	            deleteMembersStatement.close();
	        }
	        try (PreparedStatement insertMembersStatement = connection.prepareStatement("INSERT INTO guild_members (guild_name, member_uuid, guild_rank) VALUES (?, ?, ?)")) {
	            for (Map.Entry<UUID, GuildRank> memberEntry : guild.getMembers().entrySet()) {
	                insertMembersStatement.setString(1, guild.getName());
	                insertMembersStatement.setString(2, memberEntry.getKey().toString());
	                insertMembersStatement.setString(3, memberEntry.getValue().getName());
	                insertMembersStatement.addBatch();
	            }
	            insertMembersStatement.executeBatch();
	            insertMembersStatement.close();
	        }
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
		guild.drop();
	}
}
