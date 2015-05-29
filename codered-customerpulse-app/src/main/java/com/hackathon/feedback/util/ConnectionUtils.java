package com.hackathon.feedback.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {

	public static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
		}
		System.out.println("Oracle JDBC Driver Registered!");
		try {
			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:xe", "test", "test");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		
		
		return connection;
	}
}
