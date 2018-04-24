package com.enliple.parsing.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class AnalysisSentencesDomain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private int rank;
	private Double weight;
	private String contents;
	private Double rate;
	private int percent;
	
	
}
