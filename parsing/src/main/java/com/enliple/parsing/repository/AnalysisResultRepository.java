package com.enliple.parsing.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.enliple.parsing.domain.AnalysisResultDomain;

public interface AnalysisResultRepository extends MongoRepository<AnalysisResultDomain, Long> {
	
	
	public AnalysisResultDomain findByUri(String uri);
	
	public List<AnalysisResultDomain> findAll();
	
	

}
