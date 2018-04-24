package com.enliple.parsing.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="result")
public class AnalysisResultDomain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String uri;
	private int wordCount;
	private int likeCode;
	
	/** Log & Date **/
	private Date regDate;
	private Date modDate;
	
	/** Analysis **/
	private List<AnalysisClassificationsDomain> classifications;
	private List<AnalysisKeywordsDomain> keywords;
	private List<AnalysisKeywords_freqDomain> keywords_freq;
	private List<AnalysisSentencesDomain> sentences;
	
	
}
