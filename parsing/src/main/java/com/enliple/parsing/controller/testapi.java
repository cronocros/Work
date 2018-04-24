package com.enliple.parsing.controller;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testapi {

	@Value("${jsoupConnectionTimeout}")
	private int jsoupConnectionTimeout;

	@Value("${jsoupRetry}")
	private int jsoupRetry;

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
}
    
