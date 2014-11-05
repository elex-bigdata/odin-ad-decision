package com.elex.odin.mysql;

import com.elex.odin.service.ConfigurationManager;
import org.apache.commons.configuration.ConfigurationException;

import java.sql.*;
import java.util.Map;

public final class MySQLManager {
	private String url = "";
	private String username = "root";
	private String password = "123456";

	private static MySQLManager instance = new MySQLManager();

	private MySQLManager() {
        try {
            Map<String,String> mysqlConf = ConfigurationManager.parseMysqlConfig();
            this.url = mysqlConf.get("url");
            this.username = mysqlConf.get("username");
            this.password = mysqlConf.get("password");
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error when parse the mysql configuration", e);
        }
    }

	public synchronized static MySQLManager getInstance() {
		return instance;
	}

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public Connection getConnection() throws Exception {
		return DriverManager.getConnection(this.url, this.username, this.password);
	}

	public void close(ResultSet rs, Statement st, Connection conn) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}

}