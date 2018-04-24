package com.enliple.parsing.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enliple.parsing.domain.CrawlingResultDomain;
import com.enliple.parsing.service.CrawlingServiceImpl;
import com.enliple.parsing.service.DbService;

@RestController
@RequestMapping("/crawlingresult")
public class CrawlingResultController {

	@Autowired
    DbService dbService;  // MariaDB 
	
	@Autowired
	CrawlingServiceImpl crawlingService; // MongoDB 
	
	
    @RequestMapping(method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody List<CrawlingResultDomain> crawlingresult_List_Select() throws Exception{
    	List<CrawlingResultDomain> result = new ArrayList<CrawlingResultDomain>();
    	result = crawlingService.findAll();
        return result;
    }
    
    
    /** 크롤링 수집결과 reprocessing **/
    @RequestMapping(value = "reprocessing", method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody String crawlingresult_uri_AnalysisReprocessing(@RequestParam(value = "uri", required = true) String uri) throws Exception{  
		String result = null;
		CrawlingResultDomain reprocessing = new CrawlingResultDomain();
		try {
			reprocessing = crawlingService.findbyUri(uri);

			if (reprocessing.getReadCheck() == 3) {
				result = "수집된 데이터에 문제가 있어 처리하지 못하였습니다. 다시 크롤링을 수행하십시오.";
			} else if (reprocessing.getReadCheck() == 1) {
				crawlingService.updateReadcount(reprocessing);
				System.out.println("***** Update 하였습니다.   ReadCheck 번호:" + reprocessing.getReadCheck());
				result = "정상적으로 재처리를 수행하였습니다. 기사확인에서 확인하십시오";
			} else {
				System.out.println("***** Update 할수 없습니다.   ReadCheck 번호:" + reprocessing.getReadCheck());
				result = "아직 처리되지 않은 기사입니다. 잠시후에 다시 시도하십시오";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***** Update 할수 없습니다.   ReadCheck 번호:" + reprocessing.getReadCheck());
			result = "오류가 발생하여 재처리를 할 수가 없습니다. 담당자에게 문의하여 주십시오.";
		}
		return result;
    }
    
    @RequestMapping(value = "delete", method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody String crawlingresult_uri_Delete(@RequestParam(value = "uri", required = true) String uri) throws Exception{  
		String result = null;
		CrawlingResultDomain remove = new CrawlingResultDomain();
		try {
			remove = crawlingService.findbyUri(uri);
			if (remove != null) {
				crawlingService.remove(uri);
				result = "정상적으로 수집된 기사를 삭제하였습니다." + remove.getUri();
				System.out.println("***** 정상적으로 수집된 기사를 삭제하였습니다. uri : " + remove.getUri());
			} else {
				result = "해당 주소로 수집된 기사가 없어서 삭제 할 수 없습니다. uri : " + uri;
				System.out.println("***** 해당 주소로 수집된 기사가 없어서 삭제 할 수 없습니다. uri: " + uri);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***** 오류가 발생하여 Remove 할수 없습니다.   URI : " + uri);
			result = "오류가 발생하여 삭제를 할 수가 없습니다. 담당자에게 문의하여 주십시오.";
		}
		return result;
    }
    
    /** 페이징 테스트 **/
    @RequestMapping(value = "paging", method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody Page<CrawlingResultDomain> crawlingResult_list_paging(@RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum) throws Exception{
    	Page<CrawlingResultDomain> result = crawlingService.findCrawlingResultDomainListAllwithPages(new PageRequest(pageNum, 20, new Sort(Sort.Direction.DESC, "regDate")));
        return result;
    }
    
    /** 크롤링 수집결과 paging & group **/
    @RequestMapping(value = "group", method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody Page<CrawlingResultDomain> crawlingResult_list_group_paging(@RequestParam(value = "mediaName", required = false, defaultValue = "") String mediaName, @RequestParam(value = "rootDomain", required = false, defaultValue = "") String rootDomain, @RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum, @RequestParam(value = "pageSize", required = false, defaultValue = "30") int pageSize) throws Exception{

    	if (mediaName.isEmpty() && rootDomain.isEmpty()) {
    		Page<CrawlingResultDomain> result = crawlingService.findCrawlingResultDomainListAllwithPages(new PageRequest(pageNum, pageSize, new Sort(Sort.Direction.DESC, "regDate")));
			return result;

		} else if(rootDomain.isEmpty() && (mediaName.length() >0)) {
			Page<CrawlingResultDomain> result = crawlingService.findCrawlingResultDomainListGroupByMediaNamewithPages(mediaName, new PageRequest(pageNum, pageSize, new Sort(Sort.Direction.DESC, "regDate")));
			return result;
		} else {
			Page<CrawlingResultDomain> result = crawlingService.findCrawlingResultDomainListGroupByRootDomainwithPages(rootDomain, new PageRequest(pageNum, pageSize, new Sort(Sort.Direction.DESC, "regDate")));
			return result;
		}
    }
    /** 크롤링 수집결과 Select uri **/
    @RequestMapping(value = "select", method=RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody CrawlingResultDomain crawlingResult_select_uri(@RequestParam(value = "uri", required = true) String uri) throws Exception{
		CrawlingResultDomain result = new CrawlingResultDomain();
		result = crawlingService.findbyUri(uri);
		return result;
    }
    
}
