package controller;

import java.util.ArrayList;
import java.util.Scanner;

import main.TicketWorldMain;
import model.CartVO;
import model.PerformanceVO;

public class CartRegisterManager {
	public static final int COLUMN_NUM = 20; // 행
	public static final int P_SEAT = 0; // 선택 가능한 좌석
	public static final int S_SEAT = 1; // 선택된 좌석
	public static final int N_SEAT = 9; // 선택할 수 없는 좌석
	static CartDAO cartdao = new CartDAO();
	static ArrayList<CartVO> cartList = null;
	static Scanner sc = new Scanner(System.in);

	// 장바구니 출력기능구현
	public static ArrayList<CartVO> cartList() {
		cartList = cartdao.getCartTotalList();
		return cartList;
	}

	// 장바구니 항목 지우기기능구현
	public static void cartDeleteItem() {
		cartList();
		System.out.print("삭제할 공연ID를 입력하세요. ");
		int p_id = Integer.parseInt(sc.nextLine());
		int p_numId = -1;
		int c_numId = -1;
		// 해당 공연 찾기
		for (int i = 0; i < TicketWorldMain.performanceInfoList.size(); i++) {
			if (p_id == TicketWorldMain.performanceInfoList.get(i).getPerformance_id()) {
				p_numId = i;
				break;
			}
		}
		// 해당 공연 장바구니에서 찾기
		if (p_numId != -1) {
			for (int i = 0; i < cartList.size(); i++) {
				if (p_id == cartList.get(i).getPerformance_id()) {
					c_numId = i;
					break;
				}
			}
		}
		if (c_numId == -1) {
			System.out.println("삭제할 공연ID가 존재하지 않습니다.");
		} else {
			// 좌석선택해제하기
			cancelSeats(p_numId, c_numId);
			// 판매좌석돌려주기
			cancelSold_seats(p_numId, c_numId);
			cartdao.setCartDeletItem(TicketWorldMain.customer.getCustomer_id(), p_id);
			// 공연 정보 수정하기
			PerformanceRegisterManager.performanceUpdateAfterTicketing(p_numId);
			// 공연정보 로딩
			PerformanceRegisterManager.performanceList();
			cartList();
		}
	}

	// 판매좌석돌려주기
	public static void cancelSold_seats(int p_numId, int c_numId) {
		TicketWorldMain.performanceInfoList.get(p_numId)
				.setPerformance_sold_seats(TicketWorldMain.performanceInfoList.get(p_numId).getPerformance_sold_seats()
						- cartList.get(c_numId).getTotal_reservation_seats());
	}

	// 좌석선택해제기능구현
	public static void cancelSeats(int p_numId, int c_numId) {
		int x = 0, y = 0;// 좌석 배열 인덱스
		String[] reservation_seats = cartList.get(c_numId).getReservation_seats().split(" ");
		// 좌석가져오기
		int[][] seat = getPerformanceSeats(p_numId);
		// 좌석선택해제
		for (String s : reservation_seats) {
			x = s.charAt(0) - 65;
			y = Integer.parseInt(s.substring(1)) - 1;
			seat[x][y] = P_SEAT;
		}
		// 좌석정보변환하기
		String changeSeat = changeSeat(seat);
		TicketWorldMain.performanceInfoList.get(p_numId).setPerformance_seatsInfo(changeSeat);
	}

	// 장바구니 비우기기능구현
	public static void cartDelete() {
		int p_numId = -1;
		int c_numId = -1;
		for (int i = 0; i < cartList.size(); i++) {
			for (int j = 0; j < TicketWorldMain.performanceInfoList.size(); j++) {
				if (cartList.get(i).getPerformance_id() == TicketWorldMain.performanceInfoList.get(j)
						.getPerformance_id()) {
					c_numId = i;
					p_numId = j;
					break;
				}
			}
			// 좌석선택해제하기
			cancelSeats(p_numId, c_numId);
			// 판매좌석돌려주기
			cancelSold_seats(p_numId, c_numId);
			// 공연 정보 수정하기
			PerformanceRegisterManager.performanceUpdateAfterTicketing(p_numId);
			// 공연정보 로딩
			PerformanceRegisterManager.performanceList();
		}
		// 장바구니비우기
		cartdao.setCartDelete(TicketWorldMain.customer.getCustomer_id());
		cartList();
	}

	// 공연예매기능구현
	public static void ticketing(ArrayList<PerformanceVO> performanceInfoList) {
		boolean exitFlag = false;
		while (!exitFlag) {
			System.out.print("예매할 공연 ID를 입력하세요(뒤로가기: -1)");
			int p_id = Integer.parseInt(sc.nextLine());
			int numId = -1;
			if (p_id == -1) {
				exitFlag = true;
				continue;
			}
			for (int i = 0; i < performanceInfoList.size(); i++) {
				if (p_id == performanceInfoList.get(i).getPerformance_id()) {
					numId = i;
				}
			}
			// 일치하면 추가하기
			if (numId != -1) {
				if (performanceInfoList.get(numId).calcRemainingSeat() == 0) {
					System.out.println("해당 공연이 매진되어 예매할 수 없습니다.");
					exitFlag = true;
				} else {
					System.out.print("예매할 좌석수를 입력하세요.");
					String countNum = sc.nextLine().replaceAll("[^0-9]", "0");
					if (countNum.length() == 0) {
						System.out.println("다시 입력해주세요");
					} else {
						int count = Integer.parseInt(countNum);
						if (count < 1) {
							System.out.println("다시 입력해주세요");
						} else {
							if (performanceInfoList.get(numId).calcRemainingSeat() >= count) {
								// 좌석가져오기
								int[][] seat = getPerformanceSeats(numId);
								// 좌석출력하기
								printSeats(seat);
								// 좌석선택하기
								chooseSeats(numId, seat, count);
								// 예매 후 공연 정보 수정
								PerformanceRegisterManager.performanceUpdateAfterTicketing(numId);
								// 공연정보 로딩
								PerformanceRegisterManager.performanceList();
							} else {
								System.out.println("예매할 공연의 잔여좌석이 부족합니다.");
							}
							exitFlag = true;
						}
					}
				}
			} else {
				System.out.println("다시 입력해주세요.");
			}
		} // end of while
	}

	// 장바구니 저장기능구현
	public static void cartRegister(int numId, String seatNumber) {
		PerformanceVO pvo = TicketWorldMain.performanceInfoList.get(numId);
		String customer_id = TicketWorldMain.customer.getCustomer_id();
		int performance_id = pvo.getPerformance_id();
		CartVO cartvo = new CartVO(customer_id, performance_id, seatNumber);
		cartdao.setCartRegister(cartvo);
	}

	// 좌석선택하기
	public static void chooseSeats(int numId, int[][] seat, int count) {
		String seatNumber = null; // 좌석번호
		int x = 0, y = 0; // 좌석 배열 인덱스
		for (int i = 0; i < count; i++) {
			// -----------------------------------------------------------------
			boolean seatNumberFlag = false;
			while (!seatNumberFlag) {
				System.out.print("원하는 좌석" + (i + 1) + "(예:A00)을 입력하세요. ");
				seatNumber = sc.nextLine().toUpperCase();
				if (seatNumber.length() < 1 || seatNumber.length() > 4) {
				} else {
					x = seatNumber.charAt(0) - 65;
					y = Integer.parseInt(seatNumber.substring(1).replaceAll("[^0-9]", "0")) - 1;
					if (x < 0 || x > seat.length || y < 0 || y > COLUMN_NUM) {
					} else {
						seatNumberFlag = true;
					}
				}
			}
			// -----------------------------------------------------------------
			// 좌석 중복 금지
			if (seat[x][y] == S_SEAT || seat[x][y] == N_SEAT) {
				System.out.println("선택할 수 없는 좌석입니다.");
				i--;
			} else {
				seat[x][y] = S_SEAT;
				// 좌석선택보여주기
				printSeats(seat);
				// 좌석정보변환하기
				String changeSeat = changeSeat(seat);
				TicketWorldMain.performanceInfoList.get(numId).setPerformance_seatsInfo(changeSeat);
				// 카트에 저장
				cartRegister(numId, seatNumber);
			}
		} // end of for
			// 공연 판매좌석수 정보 수정
		TicketWorldMain.performanceInfoList.get(numId).setPerformance_sold_seats(
				TicketWorldMain.performanceInfoList.get(numId).getPerformance_sold_seats() + count);
	}

	// 좌석정보변환하기
	public static String changeSeat(int[][] seat) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < seat.length; i++) {
			for (int j = 0; j < seat[i].length; j++) {
				if (seat[i][j] != N_SEAT) {
					sb.append(String.valueOf(seat[i][j]));
				}
			}
		}
		return sb.toString();
	}

	// 좌석출력하기
	public static void printSeats(int[][] seat) {
		char ch = 'A';
		System.out.println("\n----------------------------------------------------------------");
		System.out.println("***************************** 무   대 ***************************");
		System.out.print(" " + "01 02 03 04 05 06 07 08 09 10    11 12 13 14 15 16 17 18 19 20");
		System.out.println("\n----------------------------------------------------------------");
		for (int i = 0; i < seat.length; i++) {
			System.out.print((char) (ch + i) + " ");
			for (int j = 0; j < seat[i].length; j++) {
				String seatsColor = null;
				switch (seat[i][j]) {
				case P_SEAT:
					seatsColor = "□";
					break;
				case S_SEAT:
					seatsColor = "■";
					break;
				case N_SEAT:
					seatsColor = "x";
					break;
				}
				if (j == seat[i].length / 2 - 1) {
					System.out.print(seatsColor + "     ");
				} else {
					System.out.print(seatsColor + "  ");
				}
			}
			System.out.println("\n----------------------------------------------------------------");
		}
	}

	// 좌석가져오기
	public static int[][] getPerformanceSeats(int numId) {
		int totalSeats = TicketWorldMain.performanceInfoList.get(numId).getPerformance_total_seats();
		int rowNum = totalSeats / COLUMN_NUM + 1;
		int remain = totalSeats % COLUMN_NUM;
		int[][] seat = new int[rowNum][COLUMN_NUM];

		String[] seatArr = TicketWorldMain.performanceInfoList.get(numId).getPerformance_seatsInfo().split("");

		if (remain != 0) {
			// 남은 좌석 있을 때
			for (int i = 0; i < seat.length; i++) {
				if (i == rowNum - 1) {
					for (int j = 0; j < COLUMN_NUM - remain; j++) {
						seat[rowNum - 1][COLUMN_NUM - 1 - j] = N_SEAT;
					}
				} else {
					for (int j = 0; j < seat[i].length; j++) {
						seat[i][j] = Integer.parseInt(seatArr[(i * COLUMN_NUM) + j]);
					}
				}
			}
		} else {
			// 남은 좌석 없을 때
			for (int i = 0; i < seat.length; i++) {
				for (int j = 0; j < seat[i].length; j++) {
					seat[i][j] = Integer.parseInt(seatArr[(i * COLUMN_NUM) + j]);
				}
			}
		}
		return seat;
	}

	// 총결제금액 계산금액
	public static int calcPrice() {
		int totalAmount = 0;
		for (CartVO data : cartList) {
			totalAmount += data.getTotal_payment_amount();
		}
		return totalAmount;
	}
}
