package com.enliple.parsing.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enliple.parsing.domain.AnalysisResultDomain;
import com.enliple.parsing.service.AnalysisServiceImpl;
import com.enliple.parsing.service.DbService;

@RestController
@RequestMapping("/analysisresult")
public class AnalysisResultController {

	@Autowired
    DbService dbService;  // MariaDB 
	
	@Autowired
	AnalysisServiceImpl analysisService; // MongoDB 
	
    @RequestMapping(method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody List<AnalysisResultDomain> analysisresult_List_Select() throws Exception{
    	List<AnalysisResultDomain> result = new ArrayList<AnalysisResultDomain>();
    	result = analysisService.findAll();
        return result;
    }
    
    @RequestMapping(value = "select", method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody AnalysisResultDomain analysisresult_id_Select(@RequestParam(value = "uri", required = true) String uri) throws Exception{  
    	AnalysisResultDomain result = new AnalysisResultDomain();
    	result = analysisService.findbyUri(uri);
    	return result;
    }
    
}
