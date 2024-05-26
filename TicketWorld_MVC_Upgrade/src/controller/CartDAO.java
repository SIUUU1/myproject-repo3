package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import main.TicketWorldMain;
import model.CartVO;
import oracle.jdbc.OracleType;
import oracle.jdbc.OracleTypes;

public class CartDAO {
	// 장바구니 저장함수
	public void setCartRegister(CartVO cartvo) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CART_INSERT_PROC(?,?,?)}");
			cstmt.setString(1, cartvo.getCustomer_id());
			cstmt.setInt(2, cartvo.getPerformance_id());
			cstmt.setString(3, cartvo.getReservation_seats());
			int value = cstmt.executeUpdate();
			if (value == 1) {
				System.out.println(cartvo.getCustomer_id() + " 장바구니 등록 성공");
			} else {
				System.out.println(cartvo.getCustomer_id() + " 장바구니 등록 실패");
			}
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
	}

	// 장바구니목록리스트함수
	public ArrayList<CartVO> getCartTotalList() {
		ArrayList<CartVO> cartList = new ArrayList<CartVO>();
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CART_PRINT_PROC(?,?)}");
			cstmt.setString(1, TicketWorldMain.customer.getCustomer_id());
			cstmt.registerOutParameter(2, OracleTypes.CURSOR);
			cstmt.executeQuery();
			rs = (ResultSet) cstmt.getObject(2);
			System.out.println("================================================================");
			System.out.println(" \t\t\t " + "내 장바구니");
			System.out.println("================================================================");
			System.out.println(" 공연ID | 공연명 | 공연일 | 예매좌석 | 총예매수 | 총결제금액 ");
			System.out.println("================================================================");
			while (rs.next()) {
				CartVO cartvo = new CartVO();
				cartvo.setCart_id(rs.getInt("cart_id"));
				cartvo.setCustomer_id(rs.getString("customer_id"));
				cartvo.setPerformance_id(rs.getInt("performance_id"));
				cartvo.setPerformance_name(rs.getString("performance_name"));
				cartvo.setReservation_seats(rs.getString("reservation_seats"));
				cartvo.setTotal_reservation_seats(rs.getInt("total_reservation_seats"));
				cartvo.setTotal_payment_amount(rs.getInt("total_payment_amount"));
				cartList.add(cartvo);

				System.out.println(" " + rs.getInt("performance_id") + " | " + rs.getString("performance_name") + " | "
						+ String.valueOf(rs.getDate("performance_day")) + " | " + rs.getString("reservation_seats")
						+ "| " + rs.getInt("total_reservation_seats") + " | " + rs.getInt("total_payment_amount"));
			}
			System.out.println("================================================================");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
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
		return cartList;
	}

	// 장바구니 수 출력 함수
	public static int getCart_Count() {
		Connection con = null;
		CallableStatement cstmt = null;
		int count = 0;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CART_COUNT_PROC(?,?)}");
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

	// 장바구니 비우기 함수
	public void setCartDelete(String customer_id) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CART_CLEAR_PROC(?)}");
			cstmt.setString(1, customer_id);
			int value = cstmt.executeUpdate();
			if (value != 0) {
				System.out.println(customer_id + " 장바구니 비우기 성공");
			} else {
				System.out.println(customer_id + " 장바구니 비우기 실패");
			}
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
	}

	// 장바구니 항목 지우기 함수
	public void setCartDeletItem(String customer_id, int p_id) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CART_DELETE_PROC(?,?)}");
			cstmt.setString(1, customer_id);
			cstmt.setInt(2, p_id);
			int value = cstmt.executeUpdate();
			if (value == 1) {
				System.out.println(p_id + "번 공연 예매 삭제 성공");
			} else {
				System.out.println(p_id + "번 공연 예매 삭제 성공");
			}
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
	}

}
