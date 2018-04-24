package com.enliple.parsing.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class AnalysisKeywords_freqDomain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int rank;
	private String contents;
	private int freq;
	private int percent;
	private Double rate;
	
	
	
}
