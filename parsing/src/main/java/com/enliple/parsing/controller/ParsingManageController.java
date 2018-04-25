package com.enliple.parsing.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enliple.parsing.domain.CrawlingResultDomain;
import com.enliple.parsing.domain.LogDomain;
import com.enliple.parsing.dto.RuleVO;
import com.enliple.parsing.parser.Crawler;
import com.enliple.parsing.service.CrawlingServiceImpl;
import com.enliple.parsing.service.DbService;
import com.enliple.parsing.service.LogServiceImpl;
 

/** 
 * @author djkim 
 * @since 02/03/18 
 */
@RestController
public class ParsingManageController {
	
	@Value("${jsoupConnectionTimeout}")
	private int jsoupConnectionTimeout;
	
	@Value("${jsoupRetry}")
	private int jsoupRetry;
	
	@Autowired
    DbService dbService;  // MariaDB 관련
	
	@Autowired
	CrawlingServiceImpl crawlingService; // MongoDB Crawling관련 
	
	@Autowired
	LogServiceImpl logresultService; // MongoDB Log 관련
	
	@Autowired
	Crawler crawler; //크롤러
	
	public String getCurrentData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}
     
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody String parsing_description() throws Exception {
		return "뉴스기사 수집을 테스트 하기 위해서는 브라우저 주소창에 수집할 뉴스기사주소를 입력하세요!" + "<br><br>"
				+ "http://서버IP주소:8080/parsing?uri=수집할언론사뉴스기사주소(http://포함)" + "<br><br>"
				+ "********** URL이 길거나 특수문자(|, }, ] 등등)가 포함되어있다면 수집할 뉴스기사 주소를 URL Engoding방식으로 인코딩하여 보내십시오";
	}
 
	@RequestMapping(value = "/parsing", method = RequestMethod.GET)
	public void parsing_uri(@RequestParam(value = "uri", required = true) String uri) throws Throwable {
		/** 시간측정 **/
		long start = System.currentTimeMillis();
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.HOUR, 9);

		/** 들어오는 모든 URI를 Insert **/
		try {
			LogDomain logResult = new LogDomain();
			if (uri.length() > 0) {
				logResult.setUri(uri);
				logResult.setStatus(8);
				logResult.setRegDate(cl.getTime());
				logresultService.insert(logResult);
			}

			/** 대상 URI가 이미 크롤링 되었는지 여부 확인 **/
			CrawlingResultDomain check = new CrawlingResultDomain();
			if (uri.length() > 0) {
				check = crawlingService.findbyUri(uri);
			}

			if (uri.length() > 0 && check == null || check.getReadCheck() == 3) {
				CrawlingResultDomain crawlingResult = new CrawlingResultDomain();
				System.out.println("****** Crawling Execution : True, start  ******");
				/** 대상 URI 루트 도메인 분리하기 **/
				StringTokenizer tokens = new StringTokenizer(uri);
				String ssl_check = tokens.nextToken("//");
				String root_domain = tokens.nextToken("/");
				String sub_domain = tokens.nextToken("/");
				crawlingResult.setRootDomain(root_domain);

				/** 대상 URI의 루트 도메인을 활용하여 크롤링 대상여부인지와 대상이면 크롤링 타겟 찾아오기 **/
				RuleVO c_rule = dbService.getRule(root_domain);
				ArrayList<String> parsing = new ArrayList<String>();

				if (c_rule == null) {
					logResult.setUri(uri);
					logResult.setStatus(9);
					logResult.setRootdomain(root_domain);
					logResult.setModDate(cl.getTime());
					logresultService.upsert(logResult);
					System.out.println("****** URI에 대한 수집 규칙이 없습니다.. :" + root_domain);
				} else {
					try {
						/** 대상 URI의 크롤링 타겟(CSS 태그) 찾아오기 **/
						String target1 = c_rule.getSelect_tag1();
						/** 크롤링 실시 **/
						parsing = crawler.crawling(uri, target1, jsoupConnectionTimeout, jsoupRetry);

						/** 트랜잭션 구간 **/
						if (parsing.size() == 8) {
							crawlingResult.setUri(parsing.get(0));
							crawlingResult.setTitle(parsing.get(2));
							crawlingResult.setContents(parsing.get(3));
							crawlingResult.setWordCount(Integer.parseInt(parsing.get(4)));
							crawlingResult.setMediaName(c_rule.getMedia_name());
							crawlingResult.setReadCheck(Integer.parseInt(parsing.get(5)));
							crawlingResult.setCategory(parsing.get(6));
							crawlingResult.setLikeCount(parsing.get(7));

							if (check != null && check.getReadCheck() == 3) {
								if (crawlingResult.getContents().toString().length() > 0) {
									crawlingResult.setReadCheck(0);
								}
								crawlingResult.setModDate(cl.getTime());
								crawlingService.update(crawlingResult);
								logResult.setUri(uri);
								logResult.setStatus(crawlingResult.getReadCheck());
								logResult.setRootdomain(root_domain);
								logResult.setModDate(cl.getTime());
								logresultService.upsert(logResult);
								System.out.println("############ MongoDB Update Sucess ############");
							} else {
								crawlingResult.setRegDate(cl.getTime());
								crawlingService.insert(crawlingResult);
								logResult.setUri(uri);
								logResult.setStatus(crawlingResult.getReadCheck());
								logResult.setRootdomain(root_domain);
								logResult.setModDate(cl.getTime());
								logresultService.upsert(logResult);
								System.out.println("############ MongoDB Insert Sucess ############");
							}
						} else {
							crawlingResult.setUri(uri);
							crawlingResult.setReadCheck(5);
							crawlingService.upsert(crawlingResult);
							logResult.setUri(uri);
							logResult.setStatus(Integer.parseInt(parsing.get(0)));
							logResult.setRootdomain(root_domain);
							logResult.setModDate(cl.getTime());
							logresultService.upsert(logResult);
						}
					} catch (Exception e) {
						e.printStackTrace();
						logResult.setUri(uri);
						logResult.setStatus(9);
						logResult.setRootdomain(root_domain);
						logResult.setModDate(cl.getTime());
						logresultService.upsert(logResult);
						System.out.println("############ MongoDB Insert Fail ############");
					}
				}
			} else {
				if (uri.length() == 0) {
					System.out.println("****** Crawling Execution :  No! URI를 정확하게 입력하십시오 : " + check.getUri());
				} else {
					System.out.println("****** Crawling Execution :  No! 해당 URI는 이미 수집되었습니다. : " + check.getUri());
				}
			}
		} catch (ClientAbortException e) {

		} catch (DuplicateKeyException e) {
			System.out.println("****** Processing or already processed.  ****** ");
		} finally {
			long end = System.currentTimeMillis();
			System.out.println(getCurrentData() + "  ****** Total runtime : " + (end - start) / 1000.0 + " ******");
			System.out.println(" ");
			System.out.println(" ");
		}
	}
    	
    
	@RequestMapping(value = "/parsingtest", method = RequestMethod.GET)
	public @ResponseBody String parsing_test_uri(@RequestParam(value = "uri", required = true) String uri) throws Exception {
		/** 시간측정 **/
		long start = System.currentTimeMillis();
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.HOUR, 9);

		String resultString = null;
		/** 대상 URI가 이미 크롤링 되었는지 여부 확인 **/
		CrawlingResultDomain check = new CrawlingResultDomain();
		if (uri.length() > 0) {
			check = crawlingService.findbyUri(uri);
		}

		if (uri.length() > 0 && check == null || check.getReadCheck() == 3) {
			System.out.println("****** 크롤링 수행 여부 : OK");
			/** 대상 URI 루트 도메인 분리하기 **/
			StringTokenizer tokens = new StringTokenizer(uri);
			String ssl_check = tokens.nextToken("//");
			String root_domain = tokens.nextToken("/");
			String sub_domain = tokens.nextToken("/");
			System.out.println("SSL_CHECK : " + ssl_check + "    ROOT_DOMAIN :  " + root_domain + "    SUB_DOMAIN :  "
					+ sub_domain);

			/** 대상 URI의 루트 도메인을 활용하여 크롤링 대상여부인지와 대상이면 크롤링 타겟 찾아오기 **/
			RuleVO c_rule = dbService.getRule(root_domain);
			ArrayList<String> parsing = new ArrayList<String>();
			CrawlingResultDomain crawlingResult = new CrawlingResultDomain();
			crawlingResult.setRootDomain(root_domain);

			if (c_rule == null) {
				resultString = "해당 URI에 대한 수집 규칙이 없습니다.";
				System.out.println("****** URI에 대한 수집 규칙이 없습니다.");
				return resultString;
			} else {
				/** 대상 URI의 추가 크롤링 타겟 찾아오기 **/
				String target1 = c_rule.getSelect_tag1();
				System.out.println("******  미디어네임 :   " + c_rule.getMedia_name());
				System.out.println("****** 크롤링 규칙 태그 : " + target1);

				/** 크롤링 실시 **/
				try {
					parsing = crawler.crawling(uri, target1, jsoupConnectionTimeout, jsoupRetry);

					/** 트랜잭션 구간 **/
					if (parsing.size() == 8) {
						crawlingResult.setUri(parsing.get(0));
						crawlingResult.setTitle(parsing.get(2));
						crawlingResult.setContents(parsing.get(3));
						crawlingResult.setWordCount(Integer.parseInt(parsing.get(4)));
						crawlingResult.setMediaName(c_rule.getMedia_name());
						crawlingResult.setReadCheck(Integer.parseInt(parsing.get(5)));
						crawlingResult.setCategory(parsing.get(6));
						crawlingResult.setLikeCount(parsing.get(7));

						if (check != null && check.getReadCheck() == 3) {
							if (crawlingResult.getContents().toString().length() > 0) {
								crawlingResult.setReadCheck(0);
							}
							crawlingResult.setModDate(cl.getTime());
							crawlingService.update(crawlingResult);
							System.out.println("############ MongoDB Update Sucess ############");
						} else {
							crawlingResult.setRegDate(cl.getTime());
							crawlingService.insert(crawlingResult);
							System.out.println("############ MongoDB Insert Sucess ############");
						}
					} else {
						System.out.println("############ parsing error ############");
						crawlingResult.setUri(uri);
						crawlingResult.setReadCheck(5);
						crawlingResult.setRegDate(cl.getTime());
						crawlingService.insert(crawlingResult);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("********** MongoDB Insert Fail **********");
				}
				resultString = "URI :    " + parsing.get(0) + "<br><br>" + "분류 :    " + parsing.get(6) + ",      긍/부정 :  " + parsing.get(7) + "<br><br>" + "제목 :    " + parsing.get(2) + "<br><br>"	+ "본문 :    " + parsing.get(3) + "<br><br>" + "크롤링상태 :    " + parsing.get(5)	+ " (0번이면 정상, 3번이면 확인필요)";
				long end = System.currentTimeMillis();
				System.out.println(getCurrentData() + "  ****** Total runtime : " + (end - start) / 1000.0 + " ******");
				return resultString;
			}
		} else {
			if (uri.length() == 0) {
				System.out.println("****** 크롤링 수행 여부 :  No! URI를 정확하게 입력하십시오" + check.getUri());
				resultString = "****** 크롤링 수행 여부 :  No! 해당 URI를 정확하게 입력하십시오.";
			} else {
				System.out.println("****** 크롤링 수행 여부 :  No! 해당 URI는 이미 수집되었습니다." + check.getUri());
				resultString = "****** 크롤링 수행 여부 :  No! 해당 URI는 이미 수집되었습니다." + "<br><br>" + "분류 :    "	+ check.getCategory() + ",      긍/부정 :  " + check.getLikeCount() + "<br><br>" + check.getUri() + "<br><br>" + "제목 :    " + check.getTitle() + "<br><br>" + "본문 :    " + check.getContents();
			}
			long end = System.currentTimeMillis();
			System.out.println(getCurrentData() + "  ****** Total runtime : " + (end - start) / 1000.0 + " ******");
			return resultString;
		}
	}

}