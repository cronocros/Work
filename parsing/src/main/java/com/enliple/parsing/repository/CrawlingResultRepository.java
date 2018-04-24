package com.enliple.parsing.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.enliple.parsing.domain.CrawlingResultDomain;

public interface CrawlingResultRepository extends MongoRepository<CrawlingResultDomain, Long> {
	
	
	public CrawlingResultDomain findByUri(String uri);
	
	
	public List<CrawlingResultDomain> findAll();
	
	public Page<CrawlingResultDomain> findBymediaName(String mediaName, Pageable pageable);
	
	public Page<CrawlingResultDomain> findByrootDomain(String rootDomain, Pageable pageable);
	
	
}
