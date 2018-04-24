package com.enliple.parsing.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="crawling")
public class CrawlingResultDomain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String uri;         // URI
	
	private String title;      // 타이틀
	private String contents;   // 본문
	private String rootDomain; // 루트도메인
	private int age;
	private int sex;
	private String likeCount;     // 카운트
	private int wordCount;     // 글자수
	private String mediaName;  //매체명
	private String category;   // 카테고리
	
	
	
	/** Flag **/
	private int readCheck;
	
	
	/** Log & Date **/
	private Date regDate;
	private Date modDate;
	
	
	
}
