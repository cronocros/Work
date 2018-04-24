package com.enliple.parsing.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="log")
public class LogDomain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private String uri;         // URI
	
	private int status;
	private String rootdomain;
	
	/** Log & Date **/
	private Date regDate;
	private Date modDate;
	
	
	
}
