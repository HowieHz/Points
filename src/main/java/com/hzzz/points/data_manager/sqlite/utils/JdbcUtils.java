package com.hzzz.points.data_manager.sqlite.utils;

import java.sql.*;

public class JdbcUtils {
	private static final String driver = "org.sqlite.JDBC";

    // 加载驱动
	static{
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

    // 提供连接
	public static Connection getConnection (String url){
		try {
            return DriverManager.getConnection(url);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
