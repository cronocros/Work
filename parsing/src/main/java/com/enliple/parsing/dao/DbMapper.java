package com.enliple.parsing.dao;

import com.enliple.parsing.dto.RuleVO;

public interface DbMapper {
    
	/* DB Select  */
    public String getDual() throws Exception;
    
    /* select Uri_search uri를 가지고 크롤링 이력을 확인한다.  */
    public int getUri_search(String uri) throws Exception;
    
    /* select Rule 룰을 루트도메인을 기준으로 셀렉트 한다. */
    public RuleVO getRule(String root_domain) throws Exception;
    
    
}


