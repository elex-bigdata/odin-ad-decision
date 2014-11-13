package com.elex.odin.mysql;

import com.elex.odin.service.ConfigurationManager;
import org.apache.commons.configuration.ConfigurationException;

import java.sql.*;
import java.util.Map;

public final class MySQLManager {
	private static String url = "";
	private static String username = "root";
	private static String password = "123456";

	private static MySQLManager instance = new MySQLManager();

    static {
        try {
            Map<String,String> mysqlConf = ConfigurationManager.parseMysqlConfig();
            url = mysqlConf.get("url");
            username = mysqlConf.get("username");
            password = mysqlConf.get("password");

            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error when parse the mysql configuration", e);
        }
    }

	public static Connection getConnection() throws Exception {
		return DriverManager.getConnection(url, username, password);
	}

	public static void close(ResultSet rs, Statement st, Connection conn) {
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