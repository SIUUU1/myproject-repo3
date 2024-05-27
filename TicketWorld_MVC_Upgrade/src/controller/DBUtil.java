package controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
	public static Connection makeConnection() {
		// db.properties 파일경로
		String filePath = "/Users/ansiu/myproject-repo1/TicketWorld/src/db.properties";
		Connection con = null;
		try {
			// db.properties 디비주소, 사용자명, 사용자암호 가져오기
			Properties properties = new Properties();
			properties.load(new FileReader(filePath));
			String url = properties.getProperty("url");
			String user = properties.getProperty("user");
			String password = properties.getProperty("password");
			// ORACLE JDBC LOADING
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// DATABASE CONNECT
			con = DriverManager.getConnection(url, user, password);
			// System.out.println("데이터베이스 접속 성공");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("데이타베이스 드라이버 로드 실패");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("데이타베이스 연결 실패");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("DB.PROPERTIES 연결 실패");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("DB.PROPERTIES 연결 실패");
		}
		return con;
	}

	public static void closeResources(Connection con, CallableStatement cstmt) {
		try {
			if (cstmt != null) {
				cstmt.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeResources(Connection con, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeResources(Connection con, PreparedStatement pstmt) {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeResources(Connection con, CallableStatement cstmt, ResultSet rs1, ResultSet rs2) {
		try {
			if (rs1 != null) {
				rs1.close();
			}
			if (rs2 != null) {
				rs2.close();
			}
			if (cstmt != null) {
				cstmt.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
