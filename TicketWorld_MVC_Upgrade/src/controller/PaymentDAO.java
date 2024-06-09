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
import oracle.jdbc.OracleTypes;

public class PaymentDAO {
	// 결제내역 저장기능구현
	public int setPaymentRegister(PaymentVO payvo) {
		Connection con = null;
		CallableStatement cstmt = null;
		int payment_id = -1;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PAY_INSERT_PROC(?,?,?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, payvo.getCustomer_id());
			cstmt.setInt(2, payvo.getPerformance_id());
			cstmt.setString(3, payvo.getPerformance_name());
			cstmt.setString(4, payvo.getRecipient_name());
			cstmt.setString(5, payvo.getRecipient_phone());
			cstmt.setString(6, payvo.getRecipient_address());
			cstmt.setString(7, payvo.getReservation_seats());
			cstmt.setInt(8, payvo.getTotal_reservation_seats());
			cstmt.setInt(9, payvo.getTotal_payment_amount());
			cstmt.registerOutParameter(10, Types.INTEGER);
			cstmt.executeUpdate();
			payment_id = cstmt.getInt(10);
			if (payment_id != -1) {
				System.out.println("결제내역 등록 성공");
			} else {
				System.out.println("결제내역 등록 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
		return payment_id;
	}

	// 결제내역출력함수
	public void getPaymentList(String cus_id) {
		ArrayList<PaymentVO> payList = new ArrayList<>();
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PAYMENT_PRINT_PROC(?,?)}");
			cstmt.setString(1, cus_id);
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);
			cstmt.executeQuery();
			rs = (ResultSet) cstmt.getObject(2);
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
				System.out.printf(" 고객명 : %-30s 연락처 : %s \n", rs.getString("recipient_name"),
						rs.getString("recipient_phone"));
				System.out.printf(" 배송지 : %-30s 예매날짜 : %s \n", rs.getString("recipient_address"),
						String.valueOf(rs.getDate("reservation_date")));
 				System.out.println("----------------------------------------------------------------");
				System.out.printf(" 공연ID : %-30d 공연명 : %s \n", rs.getInt("performance_id"),
						rs.getString("performance_name"));
				System.out.printf(" 예매좌석 : %-30s 총예매수 : %s \n", rs.getString("reservation_seats"),
						rs.getInt("total_reservation_seats"));
				System.out.println("----------------------------------------------------------------");
				System.out.printf(" %-25s 총 결제금액 : %s\n"," ",rs.getInt("total_payment_amount"));
				System.out.println("================================================================");
				payList.add(payvo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt, rs);
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
			count = cstmt.getInt(2);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
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
			count = cstmt.getInt(2);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
		return count;
	}

	// 현재 결제 내역 출력
	public void getPayment(int payment_id) {
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CPAY_PRINT_PROC(?,?)}");
			cstmt.setInt(1, payment_id);
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);
			cstmt.executeQuery();
			rs = (ResultSet) cstmt.getObject(2);
			while (rs.next()) {
				System.out.println("==========================  결제  내역  ==========================");
				System.out.printf(" 고객명 : %-30s 연락처 : %s \n", rs.getString("recipient_name"),
						rs.getString("recipient_phone"));
				System.out.printf(" 배송지 : %-30s 예매날짜 : %s \n", rs.getString("recipient_address"),
						String.valueOf(rs.getDate("reservation_date")));
 				System.out.println("----------------------------------------------------------------");
				System.out.printf(" 공연ID : %-30d 공연명 : %s \n", rs.getInt("performance_id"),
						rs.getString("performance_name"));
				System.out.printf(" 예매좌석 : %-30s 총예매수 : %s \n", rs.getString("reservation_seats"),
						rs.getInt("total_reservation_seats"));
				System.out.println("----------------------------------------------------------------");
				System.out.printf(" %-25s 총 결제금액 : %s\n"," ",rs.getInt("total_payment_amount"));
				System.out.println("================================================================");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt, rs);
		}
	}
	
	// 백업테이블에서 결제 내역 출력
	public void getPaymentBackList(String cus_id) {
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL BPAY_PRINT_PROC(?,?)}");
			cstmt.setString(1, cus_id);
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);
			cstmt.executeQuery();
			rs = (ResultSet) cstmt.getObject(2);
			while (rs.next()) {
				System.out.println("==========================  결제  내역  ==========================");
				System.out.printf(" 고객명 : %-30s 연락처 : %s \n", rs.getString("recipient_name"),
						rs.getString("recipient_phone"));
				System.out.printf(" 배송지 : %-30s 예매날짜 : %s \n", rs.getString("recipient_address"),
						String.valueOf(rs.getDate("reservation_date")));
 				System.out.println("----------------------------------------------------------------");
				System.out.printf(" 공연ID : %-30d 공연명 : %s \n", rs.getInt("performance_id"),
						rs.getString("performance_name"));
				System.out.printf(" 예매좌석 : %-30s 총예매수 : %s \n", rs.getString("reservation_seats"),
						rs.getInt("total_reservation_seats"));
				System.out.println("----------------------------------------------------------------");
				System.out.printf(" %-25s 총 결제금액 : %s\n"," ",rs.getInt("total_payment_amount"));
				System.out.println("================================================================");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt, rs);
		}
	}
}
