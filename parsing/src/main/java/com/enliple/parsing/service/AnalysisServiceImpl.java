package com.enliple.parsing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.enliple.parsing.domain.AnalysisResultDomain;
import com.enliple.parsing.repository.AnalysisResultRepository;

@Service
public class AnalysisServiceImpl {
	
	@Autowired
	private AnalysisResultRepository analysisResultRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
    /**
     * 분석결과를 몽고DB에서 URI로 검색함
     * @param String uri
     * @return List<AnalysisResultDomain>
     */
	public AnalysisResultDomain findbyUri(String uri) {
		return analysisResultRepository.findByUri(uri);
	}
	
	
    /**
     * 분석결과를 몽고DB에 저장함
     * @param AnalysisResultDomain analysisResult
     * @return List<AnalysisResultDomain>
     */
	public AnalysisResultDomain insert(AnalysisResultDomain analysisResult) {
		String uri = analysisResult.getUri();
		mongoTemplate.insert(analysisResult);
		return analysisResultRepository.findByUri(uri);
	}
	
	
    /**
     * 분석결과를 몽고DB에 URI로 검색하여 업데이트함
     * @param AnalysisResultDomain analysisResult
     * @return List<AnalysisResultDomain>
     */
	public AnalysisResultDomain update(AnalysisResultDomain analysisResult) {
		String uri = analysisResult.getUri();
		Query query = new Query();
		query.addCriteria(Criteria.where("uri").is(uri));
		Update update = new Update();
		update.set("contents", analysisResult.getSentences());
		mongoTemplate.updateFirst(query, update, AnalysisResultDomain.class);
		return analysisResultRepository.findByUri(uri);
	}
	
    /**
     * 몽고DB에서 수집결과를 LIST로 출력
     * @param 
     * @return List<AnalysisResultDomain>
     */
	public List<AnalysisResultDomain> findAll() {
		return analysisResultRepository.findAll();
	}
	
	
	

}
