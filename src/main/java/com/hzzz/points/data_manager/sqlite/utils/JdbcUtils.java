package com.hzzz.points.data_manager.sqlite.utils;

import com.hzzz.points.text.text;

import java.sql.*;

import static com.hzzz.points.utils.Utils.logError;

public class JdbcUtils {
	private static final String driver = "org.sqlite.JDBC";

    // 加载驱动
	static{
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logError(text.database_driver_error);
			e.printStackTrace();
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
