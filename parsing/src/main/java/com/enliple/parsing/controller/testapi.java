package com.enliple.parsing.controller;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enliple.parsing.domain.CrawlingResultDomain;
import com.enliple.parsing.dto.RuleVO;
import com.enliple.parsing.parser.Crawler;
import com.enliple.parsing.service.CrawlingServiceImpl;
import com.enliple.parsing.service.DbService;
import com.enliple.parsing.service.LogServiceImpl;

@RestController
public class testapi {

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

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody ArrayList<String> parsing_uri(@RequestParam(value = "tag", required = true) String tag, @RequestParam(value = "uri", required = true) String uri) throws Throwable {
		/** 시간측정 **/
		long start = System.currentTimeMillis();


		String title = null;
		String content = null;
		String content1 = null;
		String content2 = null;
		String content3 = null;
		String content4 = null;
		String body = null;
		ArrayList<String> result = new ArrayList<String>();
		
		/** 대상 URI 루트 도메인 분리하기 **/
		StringTokenizer tokens = new StringTokenizer(uri);
		String ssl_check = tokens.nextToken("//");
		String root_domain = tokens.nextToken("/");
		String sub_domain = tokens.nextToken("/");
		System.out.println("SSL_CHECK : " + ssl_check + "    ROOT_DOMAIN :  " + root_domain + "    SUB_DOMAIN :  " + sub_domain);
		
		/*Connection.Response response = Jsoup.connect(uri).method(Connection.Method.GET).execute();
		Document doc = response.parse();
		Element btnK = doc.select(tag).first();
		String btnKValue = btnK.attr(tag);
		System.out.println(btnKValue);*/ 
		
		Document doc = null;
		Document doc2 = null;
		doc = Jsoup.connect(uri).timeout(jsoupConnectionTimeout).get();
		doc2 = Jsoup.connect(uri).execute().parse();
		System.out.println(doc2.body().ownText());
		System.out.println("********************************************");
		System.out.println("********************************************");
		System.out.println("");
		System.out.println(doc2.select(tag));
		title = doc.title();
		content=doc.body().getElementsContainingOwnText(tag).toString();
		content1 = doc.select(tag).first().text();
		content2 = doc.select(tag).text();
		//content3 = doc.select(tag).toString();
		//content4 = doc.body().text();
		body = doc.body().text();
		

		result.add(uri);
		result.add(title);
		result.add(content);
		result.add(content1);
		result.add(content2);
		result.add(content3);
		result.add(content4);
		result.add(body);
		

		long end = System.currentTimeMillis();
		System.out.println("****** 전체 처리시간 : " + (end - start) / 1000.0);
		return result;
	}
	
	
	@RequestMapping(value = "/crawlingtest", method = RequestMethod.GET)
	public @ResponseBody String crawlingTest(@RequestParam(value = "tag", required = false) String tag,	@RequestParam(value = "uri", required = true) String uri) throws Throwable {
		/** 시간측정 **/
		long start = System.currentTimeMillis();
		String resultString = null;
		String target1 = tag;
		ArrayList<String> parsing = new ArrayList<String>();
		CrawlingResultDomain crawlingResult = new CrawlingResultDomain();
		/** 대상 URI가 이미 크롤링 되었는지 여부 확인 **/
		CrawlingResultDomain check = new CrawlingResultDomain();
		if (uri.length() > 0) {
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

			crawlingResult.setRootDomain(root_domain);
			if (c_rule == null && tag == null) {
				resultString = "해당 URI에 대한 수집 규칙이 없습니다.";
				System.out.println("****** URI에 대한 수집 규칙이 없습니다.");
				return resultString;
			} else {
				if (tag == null) {
					target1 = c_rule.getSelect_tag1();
				}
				System.out.println("****** 크롤링 규칙 태그 : " + target1);

				try {
					parsing = crawler.crawling(uri, target1, jsoupConnectionTimeout, jsoupRetry);
					/** 트랜잭션 구간 **/
					crawlingResult.setUri(parsing.get(0));
					crawlingResult.setTitle(parsing.get(2));
					crawlingResult.setContents(parsing.get(3));
					crawlingResult.setWordCount(Integer.parseInt(parsing.get(4)));
					crawlingResult.setMediaName(c_rule.getMedia_name());
					crawlingResult.setReadCheck(Integer.parseInt(parsing.get(5)));
					crawlingResult.setCategory(parsing.get(6));
					crawlingResult.setLikeCount(parsing.get(7));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("********** Crawling Fail **********");
				}
				resultString = "URI :    " + parsing.get(0) + "<br><br>" + " 분류 :    " + parsing.get(6) + " 긍/부정  :  " + parsing.get(7) + "<br><br>" 
						+ "RootDomain :    " + root_domain + "<br><br>" + "Tag  :" + target1 + "<br><br>" + "제목 :    " + parsing.get(2) + "<br><br>" + "본문 :    " + parsing.get(3)
						+ "<br><br>" + "테스트 크롤링상태 :    " + parsing.get(5) + " (0번이면 정상, 3번이면 확인필요)";
				long end = System.currentTimeMillis();
				System.out.println("****** 전체 처리시간 : " + (end - start) / 1000.0);
				return resultString;
			}
		} else {
			if (uri.length() == 0) {
				System.out.println("****** 크롤링 테스트 수행 여부 :  No! URI를 정확하게 입력하십시오" + check.getUri());
				resultString = "****** 크롤링 테스트 수행 여부 :  No! 해당 URI를 정확하게 입력하십시오.";
			} else {
				System.out.println("****** 크롤링 테스트 수행 여부 :  No! 해당 URI는 이미 수집되었습니다." + check.getUri());
				resultString = "****** 크롤링 테스트 수행 여부 :  No! 해당 URI는 이미 수집되었습니다." + "<br><br>" + "분류 :    "
						+ check.getCategory() + ",      긍/부정 :  " + check.getLikeCount() + "<br><br>" + check.getUri() + "<br><br>" + "Tag  :" + target1
						+ "<br><br>" + "제목 :    " + check.getTitle() + "<br><br>" + "본문 :    " + check.getContents();
			}
			long end = System.currentTimeMillis();
			System.out.println("****** 전체 처리시간 : " + (end - start) / 1000.0);
			return resultString;
		}
	}
}
    
