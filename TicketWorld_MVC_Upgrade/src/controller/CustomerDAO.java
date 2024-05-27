package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import model.CustomerVO;

public class CustomerDAO {
	// 회원목록리스트함수
	public void getCustomerTotalList() {
		String sql = "select * from customers order by customer_id";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			System.out.println("----------------------------------------------------------------");
			System.out.println(" ID | 등급 | PW | 이름 | 전화번호 | 이메일 | 주소 | 나이 | 누적결제금액 | 포인트 | 포인트적립률 | 구매할인율 ");
			System.out.println("----------------------------------------------------------------");
			while (rs.next()) {
				CustomerVO cvo = new CustomerVO();
				cvo.setCustomer_id(rs.getString("customer_id"));
				cvo.setCustomer_grade(rs.getString("customer_grade"));
				cvo.setCustomer_pw(rs.getString("customer_pw"));
				cvo.setCustomer_name(rs.getString("customer_name"));
				cvo.setCustomer_phone(rs.getString("customer_phone"));
				cvo.setCustomer_email(rs.getString("customer_email"));
				cvo.setCustomer_address(rs.getString("customer_address"));
				cvo.setCustomer_age(rs.getInt("customer_age"));
				cvo.setCustomer_accumulated_payment(rs.getInt("customer_accumulated_payment"));
				cvo.setCustomer_points(rs.getInt("customer_points"));
				cvo.setCustomer_point_ratio(rs.getDouble("customer_point_ratio"));
				cvo.setCustomer_sale_ratio(rs.getDouble("customer_sale_ratio"));
				System.out.println(cvo.toString());
			}
			System.out.println("----------------------------------------------------------------");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, pstmt, rs);
		}
	}

	// 회원 수 출력 함수
	public static int getCustomer_Count() {
		Connection con = null;
		CallableStatement cstmt = null;
		int count = 0;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CUS_COUNT_PROC(?)}");
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

	// 회원정보수정함수
	public void setCustomerUpdate(CustomerVO cvo) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CUS_UPDATE_PROC(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, cvo.getCustomer_grade());
			cstmt.setString(2, cvo.getCustomer_pw());
			cstmt.setString(3, cvo.getCustomer_name());
			cstmt.setString(4, cvo.getCustomer_phone());
			cstmt.setString(5, cvo.getCustomer_email());
			cstmt.setString(6, cvo.getCustomer_address());
			cstmt.setInt(7, cvo.getCustomer_age());
			cstmt.setInt(8, cvo.getCustomer_accumulated_payment());
			cstmt.setInt(9, cvo.getCustomer_points());
			cstmt.setDouble(10, cvo.getCustomer_point_ratio());
			cstmt.setDouble(11, cvo.getCustomer_sale_ratio());
			cstmt.setString(12, cvo.getCustomer_id());
			cstmt.registerOutParameter(13, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(13);
			if (value == 0) {
				System.out.println(cvo.getCustomer_name() + " 회원정보 수정 성공");
			} else {
				System.out.println(cvo.getCustomer_name() + " 회원정보 수정 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 회원정보삭제함수
	public void setCustomerDelete(String c_id) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CUS_DELETE_PROC(?,?)}");
			cstmt.setString(1, c_id);
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(2);
			if (value == 0) {
				System.out.println(c_id + " 회원 삭제 성공");
			} else {
				System.out.println(c_id + " 회원 삭제 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 회원가입 시 회원정보입력함수
	public void setCustomerRegister(CustomerVO cvo) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL CUS_INSERT_PROC(?,?,?,?,?,?,?,?)}");
			cstmt.setString(1, cvo.getCustomer_id());
			cstmt.setString(2, cvo.getCustomer_pw());
			cstmt.setString(3, cvo.getCustomer_name());
			cstmt.setString(4, cvo.getCustomer_phone());
			cstmt.setString(5, cvo.getCustomer_email());
			cstmt.setString(6, cvo.getCustomer_address());
			cstmt.setInt(7, cvo.getCustomer_age());
			cstmt.registerOutParameter(8, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(8);
			if (value == 0) {
				System.out.println(cvo.getCustomer_name() + " 회원가입 성공하셨습니다.");
			} else {
				System.out.println(cvo.getCustomer_name() + "회원가입 실패하셨습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 로그인 시 정보확인함수
	public CustomerVO loginCustomerRegister(String login_id, String login_pw) {
		CustomerVO cvo = null;
		String sql = "select * from customers where customer_id = ?";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, login_id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (login_pw.equals(rs.getString("customer_pw"))) {
					// System.out.println("로그인 성공");
					cvo = new CustomerVO();
					cvo.setCustomer_id(rs.getString("customer_id"));
					cvo.setCustomer_grade(rs.getString("customer_grade"));
					cvo.setCustomer_pw(rs.getString("customer_pw"));
					cvo.setCustomer_name(rs.getString("customer_name"));
					cvo.setCustomer_phone(rs.getString("customer_phone"));
					cvo.setCustomer_email(rs.getString("customer_email"));
					cvo.setCustomer_address(rs.getString("customer_address"));
					cvo.setCustomer_age(rs.getInt("customer_age"));
					cvo.setCustomer_accumulated_payment(rs.getInt("customer_accumulated_payment"));
					cvo.setCustomer_points(rs.getInt("customer_points"));
					cvo.setCustomer_point_ratio(rs.getDouble("customer_point_ratio"));
					cvo.setCustomer_sale_ratio(rs.getDouble("customer_sale_ratio"));
				} else {
					System.out.println("잘못된 비밀번호 입니다.");
				}
			} else {
				System.out.println("존재하지 않는 아이디 입니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, pstmt);
		}
		return cvo;
	}

	// 본인 확인함수
	public String identificationCustomerRegister(String find_name, String find_phone) {
		String sql = "select customer_id from customers where customer_name = ? AND customer_phone = ?";
		String customer_id = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.makeConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, find_name);
			pstmt.setString(2, find_phone);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				customer_id = rs.getString("customer_id");
			} else {
				System.out.println("존재하지 않는 회원입니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, pstmt);
		}
		return customer_id;
	}

	// 패스워드 재설정함수
	public void resetPWCustomerRegister(String customer_id, String reset_pw) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL RESET_PW_PROC(?,?,?)}");
			cstmt.setString(1, reset_pw);
			cstmt.setString(2, customer_id);
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.executeUpdate();
			int value = cstmt.getInt(3);
			if (value == 0) {
				System.out.println("비밀번호가 성공적으로 재설정되었습니다.");
			} else {
				System.out.println("비밀번호 재설정에 실패하셨습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
	}

	// 아이디 중복 막기
	public boolean preventOverlapId(String c_id) {
		boolean flag = false;
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = DBUtil.makeConnection();
			cstmt = con.prepareCall("{CALL PREVENT_OVERLAP_ID_PROC(?,?)}");
			cstmt.setString(1, c_id);
			cstmt.registerOutParameter(2, Types.INTEGER);
			cstmt.executeUpdate();
			int count = cstmt.getInt(2);
 			if (count == 0) {
				flag = true;
			} else {
				System.out.println("중복된 아이디입니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(con, cstmt);
		}
		return flag;
	}
}
