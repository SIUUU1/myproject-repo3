package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import main.TicketWorldMain;
import model.PaymentVO;

public class PaymentDAO {
	// 결제내역 저장기능구현
	public void setPaymentRegister(PaymentVO payvo) {
		Connection con = null;
		CallableStatement cstmt = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PAY_INSERT_PROC(?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, payvo.getCustomer_id());
			cstmt.setInt(2, payvo.getPerformance_id());
			cstmt.setString(3, payvo.getRecipient_name());
			cstmt.setString(4, payvo.getRecipient_phone());
			cstmt.setString(5, payvo.getRecipient_address());
			cstmt.setString(6, payvo.getReservation_seats());
			cstmt.setInt(7, payvo.getTotal_reservation_seats());
			cstmt.setInt(8, payvo.getTotal_payment_amount());
			int value = cstmt.executeUpdate();
			if (value == 1) {
				System.out.println("결제내역 등록 성공");
			} else {
				System.out.println("결제내역 등록 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
	}

	// 결제내역출력함수
	public void getPaymentList(String cus_id) {
		ArrayList<PaymentVO> payList = new ArrayList<>();
		String sql = "select a.*, b.performance_name from payment a inner join performances b "
				+ "on a.performance_id=b.performance_id where a.customer_id=? order by reservation_date desc";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, cus_id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				PaymentVO payvo = new PaymentVO();
				payvo.setPayment_id(rs.getInt("payment_id"));
				payvo.setCustomer_id(rs.getString("customer_id"));
				payvo.setPerformance_id(rs.getInt("performance_id"));
				payvo.setReservation_date(String.valueOf(rs.getDate("reservation_date")));
				payvo.setRecipient_name(rs.getString("recipient_name"));
				payvo.setRecipient_phone(rs.getString("recipient_phone"));
				payvo.setRecipient_address(rs.getString("recipient_address"));
				payvo.setReservation_seats(rs.getString("reservation_seats"));
				payvo.setTotal_reservation_seats(rs.getInt("total_reservation_seats"));
				payvo.setTotal_payment_amount(rs.getInt("total_payment_amount"));

				System.out.println("==========================  결제  내역  ==========================");
				System.out.println(
						" 고객명 : " + rs.getString("recipient_name") + "   \t\t연락처 : " + rs.getString("recipient_phone"));
				System.out.println(" 배송지 : " + rs.getString("recipient_address") + "\t예매날짜 : "
						+ String.valueOf(rs.getDate("reservation_date")));
				System.out.println("----------------------------------------------------------------");
				System.out.println(
						" 공연ID : " + rs.getInt("performance_id") + "\t공연명 : " + rs.getString("performance_name"));
				System.out.println(" 예매좌석 :  " + rs.getString("reservation_seats") + "\t총예매수 : "
						+ rs.getInt("total_reservation_seats"));
				System.out.println("----------------------------------------------------------------");
				System.out.println("\t\t   총 결제금액 : " + rs.getInt("total_payment_amount"));
				System.out.println("================================================================");
				payList.add(payvo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
	}

	// 결제내역 수 출력 함수
	public static int getPayment_Count() {
		Connection con = null;
		CallableStatement cstmt = null;
		int count = 0;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PAYMENT_COUNT_PROC(?,?)}");
			cstmt.setString(1, TicketWorldMain.customer.getCustomer_id());
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.executeUpdate();
			count = cstmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		return count;
	}

	// 백업테이블 결제내역 수 출력 함수
	public static int getPaymentBack_Count() {
		Connection con = null;
		CallableStatement cstmt = null;
		int count = 0;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PAYMENT_BACK_COUNT_PROC(?,?)}");
			cstmt.setString(1, TicketWorldMain.customer.getCustomer_id());
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.executeUpdate();
			count = cstmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		return count;
	}

	// 현재 결제 내역 출력
	public void getPayment(PaymentVO payvo) {
		String sql = "select a.*, b.performance_name from payment a inner join performances b "
				+ "on a.performance_id=b.performance_id where a.payment_id=?";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, payvo.getPayment_id());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println("==========================  결제  내역  ==========================");
				System.out.println(
						" 고객명 : " + rs.getString("recipient_name") + "   \t\t연락처 : " + rs.getString("recipient_phone"));
				System.out.println(" 배송지 : " + rs.getString("recipient_address") + "\t예매날짜 : "
						+ String.valueOf(rs.getDate("reservation_date")));
				System.out.println("----------------------------------------------------------------");
				System.out.println(
						" 공연ID : " + rs.getInt("performance_id") + "\t공연명 : " + rs.getString("performance_name"));
				System.out.println(" 예매좌석 :  " + rs.getString("reservation_seats") + "\t총예매수 : "
						+ rs.getInt("total_reservation_seats"));
				System.out.println("----------------------------------------------------------------");
				System.out.println("\t\t   총 결제금액 : " + rs.getInt("total_payment_amount"));
				System.out.println("================================================================");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
	}
	//백업테이블에서 결제 내역 출력
	public void getPaymentBackList(String cus_id) {

		
	}
}
