package controller;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import main.TicketWorldMain;
import model.PerformanceVO;

public class PerformanceRegisterManager {
	public static PerformanceDAO pdao = new PerformanceDAO();
	public static Scanner sc = new Scanner(System.in);

	// 공연목록 로딩기능구현
	public static void performanceList() {
		TicketWorldMain.performanceInfoList = pdao.getPerformanceTotalList();
	}

	// 공연목록 출력기능구현
	public static void printPerformanceList(ArrayList<PerformanceVO> performanceInfoList) {
		System.out.println("----------------------------------------------------------------");
		System.out.println(" 공연ID  공연일        장르    제한연령  잔여좌석/총좌석  티켓가격    공연명     장소     ");
		System.out.println("----------------------------------------------------------------");
		for (PerformanceVO vo : performanceInfoList) {
			System.out.printf(" %-5s  %-10s  %-4s  %-4d  %4d/%-4d  %-6d  %-10s  %-6s \n", vo.getPerformance_id(),
					vo.getPerformance_day(), vo.getPerformance_genre(), vo.getPerformance_limit_age(),
					vo.calcRemainingSeat(), vo.getPerformance_total_seats(), vo.getPerformance_ticket_price(),
					vo.getPerformance_name(), vo.getPerformance_venue());
		}
		System.out.println("----------------------------------------------------------------");
	}

	// 공연정보 저장기능구현
	public static void performanceRegister() {
		System.out.println("공연정보입력");
		System.out.print("공연명 : ");
		String p_name = sc.nextLine();
		System.out.print("장르 : ");
		String p_genre = sc.nextLine();
		System.out.print("공연일(예:2023/01/01) : ");
		String p_day = sc.nextLine();
		System.out.print("장소 : ");
		String p_venue = sc.nextLine();
		System.out.print("관람제한연령 : ");
		int p_limit_age = sc.nextInt();
		sc.nextLine();
		System.out.print("총좌석수 : ");
		int p_total_seats = sc.nextInt();
		sc.nextLine();
		System.out.print("티켓가격 : ");
		int p_ticket_price = sc.nextInt();
		sc.nextLine();
		PerformanceVO pvo = new PerformanceVO(p_name, p_genre, p_day, p_venue, p_limit_age, p_total_seats,
				p_ticket_price);

		pdao.setPerformanceRegister(pvo);
		performanceList();
	}

	// 공연정보 수정기능구현
	public static void performanceUpdate() {
		System.out.print("수정할 공연ID>>");
		int p_id = sc.nextInt();
		sc.nextLine();
		System.out.print("공연명 : ");
		String p_name = sc.nextLine();
		System.out.print("장르 : ");
		String p_genre = sc.nextLine();
		System.out.print("공연일(예:2023/01/01) : ");
		String p_day = sc.nextLine();
		System.out.print("장소 : ");
		String p_venue = sc.nextLine();
		System.out.print("관람제한연령 : ");
		int p_limit_age = sc.nextInt();
		sc.nextLine();
		System.out.print("총좌석수 : ");
		int p_total_seats = sc.nextInt();
		sc.nextLine();
		System.out.print("판매된 좌석수 : ");
		int p_sold_seats = sc.nextInt();
		sc.nextLine();
		System.out.print("티켓가격 : ");
		int p_ticket_price = sc.nextInt();
		sc.nextLine();
		PerformanceVO pvo = new PerformanceVO(p_id, p_name, p_genre, p_day, p_venue, p_limit_age, p_total_seats,
				p_sold_seats, p_ticket_price);

		pdao.setPerformanceUpdate(pvo);
		performanceList();
	}

	// 공연정보 삭제기능구현
	public static void performanceDelete() {
		System.out.print("삭제할 공연ID>> ");
		int p_id = sc.nextInt();
		sc.nextLine();

		pdao.setPerformanceDelete(p_id);
		performanceList();
	}

	// 예매 후 공연 정보 수정기능구현
	public static void performanceUpdateAfterTicketing(int numId) {
		PerformanceVO pvo = TicketWorldMain.performanceInfoList.get(numId);
		pdao.setPerformanceUpdateAfterTicketing(pvo);
		performanceList();
	}

	// 공연 이름 검색기능구현
	public static boolean selectPerformanceName(ArrayList<PerformanceVO> performanceInfoList) {
		boolean findFlag = false;
		ArrayList<PerformanceVO> perfomanceList = new ArrayList<>();
		System.out.print("검색할 공연명을 입력하세요. ");
		String p_name = sc.nextLine();

		perfomanceList = (ArrayList<PerformanceVO>) performanceInfoList.stream()
				.filter(e -> p_name.equals(e.getPerformance_name())).sorted().collect(Collectors.toList());

		if (!perfomanceList.isEmpty()) {
			printPerformanceList(perfomanceList);
			findFlag = true;
		} else {
			System.out.println("찾으시는 정보가 없습니다.");
		}
		return findFlag;
	}

	// 공연 장르 검색기능구현
	public static boolean selectPerformanceGenre(ArrayList<PerformanceVO> performanceInfoList, String p_genre) {
		boolean findFlag = false;
		ArrayList<PerformanceVO> perfomanceList = new ArrayList<>();
		// 공연 장르 검색
		if (p_genre == null) {
			// 기타 장르
			System.out.print("검색어할 장르를 입력하세요. ");
			String str = sc.nextLine();
			perfomanceList = (ArrayList<PerformanceVO>) performanceInfoList.stream()
					.filter(e -> str.equals(e.getPerformance_genre())).sorted().collect(Collectors.toList());
		} else {
			perfomanceList = (ArrayList<PerformanceVO>) performanceInfoList.stream()
					.filter(e -> p_genre.equals(e.getPerformance_genre())).sorted().collect(Collectors.toList());
		}
		// 검색한 공연 출력
		if (!perfomanceList.isEmpty()) {
			printPerformanceList(perfomanceList);
			findFlag = true;
		} else {
			System.out.println("찾으시는 정보가 없습니다.");
		}
		return findFlag;
	}
}
