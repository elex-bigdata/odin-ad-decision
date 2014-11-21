package com.elex.odin.mysql;

import com.elex.odin.service.ConfigurationManager;
import org.apache.commons.configuration.ConfigurationException;

import java.sql.*;
import java.util.Map;

public final class MySQLManager {
    private static Map<String,String> mysqlConf ;

    static {
        try {
            mysqlConf = ConfigurationManager.parseMysqlConfig();
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error when parse the mysql configuration", e);
        }
    }

	public static Connection getConnection(String type) throws Exception {
		return DriverManager.getConnection(mysqlConf.get(type + ".url"),
                mysqlConf.get(type + ".username"), mysqlConf.get(type + ".password"));
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