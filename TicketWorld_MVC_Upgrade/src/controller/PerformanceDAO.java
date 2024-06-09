package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import model.PerformanceVO;
import oracle.jdbc.OracleTypes;

public class PerformanceDAO {
	// 공연목록리스트함수
	public ArrayList<PerformanceVO> getPerformanceTotalList() {
		ArrayList<PerformanceVO> performanceList = new ArrayList<PerformanceVO>();
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PERFORMANCES_PRINT_PROC(?)}");
			cstmt.registerOutParameter(1, OracleTypes.CURSOR);
			cstmt.executeQuery();
			rs = (ResultSet) cstmt.getObject(1);
			while (rs.next()) {
				PerformanceVO pvo = new PerformanceVO();
				pvo.setPerformance_id(rs.getInt("performance_id"));
				pvo.setPerformance_name(rs.getString("performance_name"));
				pvo.setPerformance_genre(rs.getString("performance_genre"));
				pvo.setPerformance_day(String.valueOf(rs.getDate("performance_day")));
				pvo.setPerformance_venue(rs.getString("performance_venue"));
				pvo.setPerformance_limit_age(rs.getInt("performance_limit_age"));
				pvo.setPerformance_total_seats(rs.getInt("performance_total_seats"));
				pvo.setPerformance_sold_seats(rs.getInt("performance_sold_seats"));
				pvo.setPerformance_seatsInfo(rs.getString("performance_seatsInfo"));
				pvo.setPerformance_ticket_price(rs.getInt("performance_ticket_price"));
				performanceList.add(pvo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt, rs);
		}
		return performanceList;
	}

	// 공연 수 출력 함수
	public static int getPerformance_Count() {
		Connection con = null;
		CallableStatement cstmt = null;
		int count = 0;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PERFORMANCE_COUNT_PROC(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			cstmt.executeUpdate();
			count = cstmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
		return count;
	}

	// 공연정보입력함수
	public void setPerformanceRegister(PerformanceVO pvo) {
		// 총좌석수를 문자열로 변환
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pvo.getPerformance_total_seats(); i++) {
			sb.append("0");
		}
		CallableStatement cstmt = null;
		Connection con = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PER_INSERT_PROC(?,?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, pvo.getPerformance_name());
			cstmt.setString(2, pvo.getPerformance_genre());
			cstmt.setString(3, pvo.getPerformance_day());
			cstmt.setString(4, pvo.getPerformance_venue());
			cstmt.setInt(5, pvo.getPerformance_limit_age());
			cstmt.setInt(6, pvo.getPerformance_total_seats());
			cstmt.setString(7, sb.toString());
			cstmt.setInt(8, pvo.getPerformance_ticket_price());
			cstmt.registerOutParameter(9, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(9);
			if (value == 0) {
				System.out.println(pvo.getPerformance_name() + " 공연 등록 성공");
			} else {
				System.out.println(pvo.getPerformance_name() + " 공연 등록 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 공연정보수정함수
	public void setPerformanceUpdate(PerformanceVO pvo) {
		// 총좌석수를 문자열로 변환
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pvo.getPerformance_total_seats(); i++) {
			sb.append("0");
		}
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PER_UPDATE_PROC(?,?,?,?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, pvo.getPerformance_name());
			cstmt.setString(2, pvo.getPerformance_genre());
			cstmt.setString(3, pvo.getPerformance_day());
			cstmt.setString(4, pvo.getPerformance_venue());
			cstmt.setInt(5, pvo.getPerformance_limit_age());
			cstmt.setInt(6, pvo.getPerformance_total_seats());
			cstmt.setInt(7, pvo.getPerformance_sold_seats());
			cstmt.setString(8, sb.toString());
			cstmt.setInt(9, pvo.getPerformance_ticket_price());
			cstmt.setInt(10, pvo.getPerformance_id());
			cstmt.registerOutParameter(11, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(11);
			if (value == 0) {
				System.out.println(pvo.getPerformance_name() + " 공연정보 수정 성공");
			} else {
				System.out.println(pvo.getPerformance_name() + " 공연정보 수정 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 공연정보삭제함수
	public void setPerformanceDelete(int p_id) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PER_DELTE_PROC(?,?)}");
			cstmt.setInt(1, p_id);
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(2);
			if (value == 0) {
				System.out.println(p_id + " 공연 삭제 성공");
			} else {
				System.out.println(p_id + " 공연 삭제 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 예매 후 공연 정보 수정함수
	public void setPerformanceUpdateAfterTicketing(PerformanceVO pvo) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PER_UPDATE_TICKEING_PROC(?,?,?,?)}");
			cstmt.setInt(1, pvo.getPerformance_sold_seats());
			cstmt.setString(2, pvo.getPerformance_seatsInfo());
			cstmt.setInt(3, pvo.getPerformance_id());
			cstmt.registerOutParameter(4, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(4);
			if (value == 0) {
				// System.out.println(pvo.getPerformance_name() + "예매 후 공연정보 수정 성공");
			} else {
				// System.out.println(pvo.getPerformance_name() + "예매 후 공연정보 수정 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}
}
