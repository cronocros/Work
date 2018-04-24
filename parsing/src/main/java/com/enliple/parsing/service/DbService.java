package com.enliple.parsing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enliple.parsing.dao.DbMapper;
import com.enliple.parsing.dto.RuleVO;
 
@Service
public class DbService {
 
    @Autowired
    DbMapper dbMapper;
 
    /* select dual */
    public String getDual() throws Exception{
        return dbMapper.getDual();
    }
    
    /* select Uri_search */
    public int getUri_search(String uri) throws Exception{
        return dbMapper.getUri_search(uri);
    }

    /* select Rule */
    public RuleVO getRule(String root_domain) throws Exception{
        return dbMapper.getRule(root_domain);
    }
    
    

}


