package io.noks.kitpvp.interfaces.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.zaxxer.hikari.HikariDataSource;

import io.noks.kitpvp.enums.RefreshType;

public interface FetchLeaderboard {
	
	default Map<String, Integer> scanLeaderboard(RefreshType type, boolean connected, HikariDataSource hikari, ExecutorService executor) {
		if (!connected) {
			return null;
		}
		final Map<String, Integer> map = new LinkedHashMap<String, Integer>(10);
		final String selectLine = "SELECT nickname," + type.getName().toLowerCase() + " FROM stats ORDER BY " + type.getName().toLowerCase() + " DESC LIMIT 10";
		CompletableFuture.runAsync(() -> {
			Connection connection = null;
			try {
				connection = hikari.getConnection();
				final PreparedStatement statement = connection.prepareStatement(selectLine);
				final ResultSet result = statement.executeQuery();
				while (result.next()) {
					String name = result.getString("nickname");
					int stat = result.getInt(type.getName().toLowerCase());
					map.put(name, stat);
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
		}, executor).join();
		return map.isEmpty() ? Collections.emptyMap() : map;
	}
}
