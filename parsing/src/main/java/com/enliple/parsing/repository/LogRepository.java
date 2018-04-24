package com.enliple.parsing.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.enliple.parsing.domain.LogDomain;

public interface LogRepository extends MongoRepository<LogDomain, Long> {
	
	
	public LogDomain findByUri(String uri);
	
	
	public List<LogDomain> findAll();
	
	public Page<LogDomain> findByuri(String uri, Pageable pageable);
	
	
}
