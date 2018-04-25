package com.enliple.parsing.parser;


import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Selector.SelectorParseException;
import org.springframework.stereotype.Service;

@Service
public class Crawler {

	public String getCurrentData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}
	
    /**
     * 크롤러 : 크롤링을 수행함
     * @param String uri, String target, int jsoupConnectionTimeout, int jsoupRetry
     * @param 
     * @return ArrayList<String uri, String target, String title, String contents, String wordcount, String msg>
     * msg 0 : 정상적으로 수집, 1 : 정상적으로 수집 및 분석까지 완료, 3 : 수집시도는 성공하였으나 데이터 내용이 없는경우, 5 : 수집시도는 실패 및 에러 발생 
     */
	public ArrayList<String> crawling(String uri, String target, int jsoupConnectionTimeout, int jsoupRetry) throws Exception {
		System.out.println("====================================================== Start  News Crawling " + getCurrentData() + " =========================================");
		/** 시간측정 **/
		long start = System.currentTimeMillis();
		ArrayList<String> result = new ArrayList<>();
		String msg = null; // msg 0 : 정상적으로 수집, 1 : 정상적으로 수집 및 분석까지 완료, 3 : 수집시도는 성공하였으나 데이터 내용이 없는경우, 5 : 수집시도는 실패 및 에러 발생
		Document doc = null;
		String title = null;
		String content = null;
		String wordcount = null;
		String category = null;
		String likecount = null;

		/** properties에 등록한 시간만큼사이트가 응답하지 않으면 크롤링을 중지하며 등록한 횟수만큼 시도한다. **/
		for (int i = 0; i < jsoupRetry; i++) {
			try {
				doc = Jsoup.connect(uri).timeout(jsoupConnectionTimeout).get();

				/** 크롤링할 타겟 입력이 비어있거나 잘못입력되어 있는경우 기본적으로 바디를 크롤링 **/
				if (target.isEmpty() || target == null) {
					/** 사이트의 본문 크롤링 **/
					content = doc.body().text();
					/** 빈공백으로 수집이 되어진 경우 **/
					if (content.isEmpty() || content.length() == 0 || content == null) {
						msg = "3";
					} else {
						/** 처리 메세지 코드 **/
						msg = "0";
					}
					/** 사이트의 타이틀 크롤링 **/
					title = doc.title();
					/** 글자수 체크 **/
					wordcount = Integer.toString(content.length());
					/** 임시 카테고리 **/
					category = doc.select("a.select").text();
					/** 임시 긍/부정 **/
					likecount = doc.select("div.articleUpDown").text();
				} else {
					/** CSS이용한 콘텐츠 크롤링 **/
					content = doc.select(target).not("iframe").text();
					/** 콘텐츠 필터링 **/
					content = nateparsing(content);
					/** 빈공백으로 수집이 되어진 경우 **/
					if (content.isEmpty() || content.length() == 0 || content == null || content == "") {
						content = doc.body().text();
						if (content.isEmpty() || content.length() == 0 || content == null || content == "") {
							content = doc.text();
							/** 처리 메세지 코드 **/
							msg = "3";
						} else {
							content = nateparsing(content);
							msg = "0";
						}
					} else {
						/** 처리 메세지 코드 **/
						msg = "0";
					}
					/** 사이트의 타이틀 크롤링 **/
					title = doc.title();
					/** 글자수 체크 **/
					wordcount = Integer.toString(content.length());
					/** 임시 카테고리 **/
					category = doc.select("a.select").text();
					/** 임시 긍/부정 **/
					likecount = doc.select("a.articleUp").text();
				}
				/** Result **/
				System.out.println(" 기사 URI  :  " + uri);
				System.out.println(" 처리결과 코드  :  " + msg + "   (0번:정상, 1번:분석, 3번:비정상)");
				result.add(uri);
				result.add(target);
				result.add(title);
				result.add(content);
				result.add(wordcount);
				result.add(msg);
				result.add(category);
				result.add(likecount);

				break;
			} catch (SocketTimeoutException e) {
				msg = "6";
				result.add(msg);
				e.printStackTrace();
				System.out.println(" jsoup Timeout occurred " + i + " times");
			} catch (SelectorParseException e) {
				System.out.println(" parse error !!");
				msg = "7";
				result.add(msg);
			} catch (Exception e) {
				System.out.println(" crawling error !!");
				msg = "5";
				result.add(msg);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("뉴스 크롤링 실행 시간 : " + (end - start) / 1000.0);
		System.out.println("====================================================== End  News Crawling "	+ getCurrentData() + " =========================================");
		return result;
	}
	
	
	
	
    /**
     * 필터 : 글자 자르기
     * @param 
     * @param 
     * @return 
     */
	private String nateparsing(String content) {
		if (content.contains("@")) {
			int idx = content.indexOf("@");
			String content1 = content.substring(0, idx);
			content = content1;
		} 
		if (content.contains("(끝)")) {
			int idx = content.indexOf("(끝)");
			String content1 = content.substring(0, idx);
			content = content1;
		} 
		if (content.contains("관련뉴스")) {
			int idx = content.indexOf("관련뉴스");
			String content1 = content.substring(0, idx);
			content = content1;
		} 
		if (content.contains("[이 시각 많이 본 기사]")) {
			int idx = content.indexOf("[이 시각 많이 본 기사]");
			String content1 = content.substring(0, idx);
			content = content1;
		}
		if (content.contains("☞")) {
			int idx = content.indexOf("☞");
			String content1 = content.substring(0, idx);
			content = content1;
		}
		if (content.contains("재배포 금지")) {
			int idx = content.indexOf("재배포 금지");
			String content1 = content.substring(0, idx);
			content = content1;
		} 
		if (content.contains("▶")) {
			int idx = content.indexOf("▶");
			String content1 = content.substring(0, idx);
			content = content1;
		}
		if (content.contains("라이온봇 기자")) {
			int idx = content.indexOf("라이온봇 기자");
			String content1 = content.substring(0, idx);
			content = content1;
		} 
		return content;
	}
	
	
	
	
	
	
/*	public ArrayList<String> crawling(String uri, String target, int jsoupConnectionTimeout, int jsoupRetry) throws Exception {
		System.out.println("====================================================== Start  News Crawling " + getCurrentData() + " =========================================");
		*//** 시간측정 **//*
		long start = System.currentTimeMillis();
		ArrayList<String> result = new ArrayList<>();
		String msg = null; // msg 0 : 정상적으로 수집, 1 : 정상적으로 수집 및 분석까지 완료, 3 : 수집시도는 성공하였으나 데이터 내용이 없는경우, 5 : 수집시도는 실패 및 에러 발생
		Document doc = null;
		String title = null;
		String content = null;
		String wordcount = null;
		String category = null;
		String likecount = null;

		*//** properties에 등록한 시간만큼사이트가 응답하지 않으면 크롤링을 중지하며 등록한 횟수만큼 시도한다. **//*
		for (int i = 0; i < jsoupRetry; i++) {
			try {
				doc = Jsoup.connect(uri).timeout(jsoupConnectionTimeout).get();

				*//** 크롤링할 타겟 입력이 비어있거나 잘못입력되어 있는경우 기본적으로 바디를 크롤링 **//*
				if (target.isEmpty() || target == null) {
					*//** 사이트의 본문 크롤링 **//*
					content = doc.body().text();
					*//** 빈공백으로 수집이 되어진 경우 **//*
					if (content.isEmpty() || content.length() == 0 || content == null) {
						msg = "3";
						//System.out.println(" ************ 크롤링할 타겟 입력이 잘못되었거나 비어있어 크롤링을 할 수 없습니다. ************ ");
					} else {
						*//** 처리 메세지 코드 **//*
						msg = "0";
						//System.out.println(" ************ 본문으로 크롤링 및 파싱 완료 ************ ");
					}
					*//** 사이트의 타이틀 크롤링 **//*
					title = doc.title();
					*//** 글자수 체크 **//*
					wordcount = Integer.toString(content.length());
					*//** 임시 카테고리 **//*
					category = doc.select("a.select").text();
					*//** 임시 긍/부정 **//*
					likecount = doc.select("div.articleUpDown").text();
				} else {
					*//** 사이트의 본문 크롤링 **//*
					content = doc.select(target).not("iframe").text();
					content = nateparsing(content);
					*//** 빈공백으로 수집이 되어진 경우 **//*
					if (content.isEmpty() || content.length() == 0 || content == null || content == "") {
						content = doc.body().text();
						if (content.isEmpty() || content.length() == 0 || content == null || content == "") {
							content = doc.text();
							*//** 처리 메세지 코드 **//*
							//System.out.println(" ************ 태그는 입력되었으나 잘못입력, 뉴스기사가 아닌 경우, 사이트와 연결불가 등의 이유로 크롤링을 할 수 없습니다. ************ ");
							msg = "3";
						} else {
							content = nateparsing(content);
							msg = "0";
							//System.out.println(" ************ 본문으로 크롤링 및 파싱 완료 ************ ");
						}
					} else {
						*//** 처리 메세지 코드 **//*
						msg = "0";
						//System.out.println(" ************ 정상적으로 CSS 셀렉트로 인한 크롤링 및 파싱 완료 ************ ");
					}
					*//** 사이트의 타이틀 크롤링 **//*
					title = doc.title();
					*//** 글자수 체크 **//*
					wordcount = Integer.toString(content.length());
					*//** 임시 카테고리 **//*
					category = doc.select("a.select").text();
					*//** 임시 긍/부정 **//*
					likecount = doc.select("a.articleUp").text();
				}
				*//** Result **//*
				System.out.println(" 기사 URI  :  " + uri);
				 System.out.println(" 기사 분류 : " + category); System.out.println(" ");
				 * System.out.println(" 기사 타이틀  :  " + title); System.out.println(" ");
				 * System.out.println(" 기사 본문 :  " + content); System.out.println(" ");
				 * System.out.println(" 기사 글자수  :  " + wordcount + "자, " + " 긍/부정  :  " + likecount);
				 * System.out.println(" "); 
				System.out.println(" 처리결과 코드  :  " + msg + "   (0번:정상, 1번:분석, 3번:비정상)");
				result.add(uri);
				result.add(target);
				result.add(title);
				result.add(content);
				result.add(wordcount);
				result.add(msg);
				result.add(category);
				result.add(likecount);

				break;
			} catch (SocketTimeoutException e) {
				msg = "6";
				result.add(msg);
				e.printStackTrace();
				System.out.println(" jsoup Timeout occurred " + i + " times");
			} catch (SelectorParseException e) {
				System.out.println(" parse error !!");
				msg = "7";
				result.add(msg);
			} catch (Exception e) {
				System.out.println(" crawling error !!");
				msg = "5";
				result.add(msg);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("뉴스 크롤링 실행 시간 : " + (end - start) / 1000.0);
		System.out.println("====================================================== End  News Crawling "	+ getCurrentData() + " =========================================");
		return result;
	}*/
}
