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

import com.enliple.parsing.domain.CrawlingResultDomain;
import com.enliple.parsing.repository.CrawlingResultRepository;

@Service
public class CrawlingServiceImpl {
	
	@Autowired
	private CrawlingResultRepository crawlingResultRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	

    /**
     * 수집결과를 몽고DB에서 URI로 검색함
     * @param String uri
     * @return CrawlingResultDomain
     */
	public CrawlingResultDomain findbyUri(String uri) {
		return crawlingResultRepository.findByUri(uri);
	}
	
	
    /**
     * 수집결과를 몽고DB에 저장함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public CrawlingResultDomain insert(CrawlingResultDomain crawlingResult) {
		String uri = crawlingResult.getUri();
		mongoTemplate.insert(crawlingResult);
		return crawlingResultRepository.findByUri(uri);
	}
	
	
    /**
     * 수집결과 콜렉션에서 URI로 검색하여 업데이트함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public CrawlingResultDomain update(CrawlingResultDomain crawlingResult) {
		String uri = crawlingResult.getUri();
		Query query = new Query();
		query.addCriteria(Criteria.where("uri").is(uri));
		Update update = new Update();
		update.set("contents", crawlingResult.getContents());
		update.set("readCheck", crawlingResult.getReadCheck());
		update.set("wordCount", crawlingResult.getWordCount());
		update.set("category", crawlingResult.getCategory());
		update.set("modDate", crawlingResult.getModDate());
		mongoTemplate.updateFirst(query, update, CrawlingResultDomain.class);
		//WriteResult writeResult = mongoTemplate.updateFirst(query, update, CrawlingResultDomain.class);
		//System.out.println(writeResult.toString());
		
		return crawlingResultRepository.findByUri(uri);
	}
	
	public CrawlingResultDomain upsert(CrawlingResultDomain crawlingResult) {
		String uri = crawlingResult.getUri();
		Query query = new Query();
		query.addCriteria(Criteria.where("uri").is(uri));
		Update update = new Update();
		update.set("readCheck", crawlingResult.getReadCheck());
		update.set("modDate", crawlingResult.getModDate());
		mongoTemplate.upsert(query, update, CrawlingResultDomain.class);
		
		return crawlingResultRepository.findByUri(uri);
	}
	
    /**
     * 수집결과 콜렉션에서 URI로 검색하여 readcount를 0으로 업데이트함
     * @param CrawlingResultDomain crawlingResult
     * @return CrawlingResultDomain
     */
	public CrawlingResultDomain updateReadcount(CrawlingResultDomain crawlingResult) {
		String uri = crawlingResult.getUri();
		Criteria criteria = new Criteria("uri");
		criteria.is(uri);
		Query query = new Query(criteria);
		Update update = new Update();
		update.set("readCheck", 0);
		mongoTemplate.updateMulti(query, update, CrawlingResultDomain.class);
		
		return crawlingResultRepository.findByUri(uri);
	}
	
	
    /**
     * 몽고DB에서 수집결과를 LIST로 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public List<CrawlingResultDomain> findAll() {
		return crawlingResultRepository.findAll();
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
		mongoTemplate.remove(query, "crawling");
	}
	
	
    /**
     * 몽고DB에서 수집결과를 Paging하여 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public Page<CrawlingResultDomain> findCrawlingResultDomainListAllwithPages(Pageable pageRequest) {
	    return crawlingResultRepository.findAll(pageRequest);
	}
	
    /**
     * 몽고DB에서 수집결과를 Rootdomain으로 그룹화 및 Paging하여 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public Page<CrawlingResultDomain> findCrawlingResultDomainListGroupByMediaNamewithPages(String mediaName, Pageable pageRequest) {
	    return crawlingResultRepository.findBymediaName(mediaName, pageRequest);
	}
	
    /**
     * 몽고DB에서 수집결과를 Rootdomain으로 그룹화 및 Paging하여 출력
     * @param 
     * @return List<CrawlingResultDomain>
     */
	public Page<CrawlingResultDomain> findCrawlingResultDomainListGroupByRootDomainwithPages(String rootDomain, Pageable pageRequest) {
	    return crawlingResultRepository.findByrootDomain(rootDomain, pageRequest);
	}
	
	

}
