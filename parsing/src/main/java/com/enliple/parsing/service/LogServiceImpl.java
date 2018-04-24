package com.enliple.parsing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.enliple.parsing.domain.LogDomain;
import com.enliple.parsing.repository.LogRepository;

@Service
public class LogServiceImpl {
	
	@Autowired
	private LogRepository logRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	

    /**
     * 수집결과를 몽고DB에서 URI로 검색함
     * @param String uri
     * @return CrawlingResultDomain
     */
	public LogDomain findbyUri(String uri) {
		return logRepository.findByUri(uri);
	}
	
	
    /**
     * 수집결과를 몽고DB에 저장함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public LogDomain insert(LogDomain logResult) {
		String uri = logResult.getUri();
		mongoTemplate.insert(logResult);
		return logRepository.findByUri(uri);
	}
	
	
    /**
     * 수집결과 콜렉션에서 URI로 검색하여 업데이트함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public LogDomain update(LogDomain logResult) {
		String uri = logResult.getUri();
		Query query = new Query();
		query.addCriteria(Criteria.where("uri").is(uri));
		Update update = new Update();
		update.set("status", logResult.getStatus());
		update.set("modDate", logResult.getModDate());
		mongoTemplate.updateFirst(query, update, LogDomain.class);
		
		return logRepository.findByUri(uri);
	}
	
    /**
     * 수집결과 콜렉션에서 URI로 검색하여 업데이트함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public LogDomain upsert(LogDomain logResult) {
		String uri = logResult.getUri();
		Query query = new Query();
		query.addCriteria(Criteria.where("uri").is(uri));
		Update update = new Update();
		update.set("status", logResult.getStatus());
		update.set("modDate", logResult.getModDate());
		mongoTemplate.upsert(query, update, LogDomain.class);
		
		return logRepository.findByUri(uri);
	}
	
    /**
     * 수집결과 콜렉션에서 URI로 검색하여 readcount를 0으로 업데이트함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public LogDomain updateReadcount(LogDomain logResult) {
		String uri = logResult.getUri();
		Criteria criteria = new Criteria("uri");
		criteria.is(uri);
		Query query = new Query(criteria);
		Update update = new Update();
		update.set("readCheck", 0);
		mongoTemplate.updateMulti(query, update, LogDomain.class);
		
		return logRepository.findByUri(uri);
	}
	
	
    /**
     * 몽고DB에서 수집결과를 LIST로 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public List<LogDomain> findAll() {
		return logRepository.findAll();
	}

	
    /**
     * 몽고DB에서 수집결과를 LIST로 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public void remove(String uri) {
		Criteria criteria = new Criteria("uri");
		criteria.is(uri);
		Query query = new Query(criteria);
		mongoTemplate.remove(query, "log");
	}
	
	
    /**
     * 몽고DB에서 수집결과를 Paging하여 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public Page<LogDomain> findCrawlingResultDomainListAllwithPages(Pageable pageRequest) {
	    return logRepository.findAll(pageRequest);
	}
	
	
	

}
