package com.enliple.parsing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectTimeout {

	
    @Value("${jsoupConnectionTimeout}")
    private String jsoupConnectionTimeout;

    public String getKey() {
        return jsoupConnectionTimeout;
    }

    public void setKey(String jsoupConnectionTimeout) {
        this.jsoupConnectionTimeout = jsoupConnectionTimeout;
    }
    
/*	
    @Value("${jsoupRetry}")
    private String jsoupRetry;

    public String getKey1() {
        return jsoupRetry;
    }

    public void setKey1(String jsoupRetry) {
        this.jsoupRetry = jsoupRetry;
    }
	*/
	
	

}
