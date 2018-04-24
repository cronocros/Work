package com.enliple.parsing.parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Parsing {

	public static String getCurrentData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static ArrayList<String> parsing(String pre_word, String post_word, String content, String title) throws IOException {

		System.out.println("====================================================== Start  News Parsing " + getCurrentData() +" =========================================");
        
        try {
        	
        	if (pre_word == null || pre_word == " ") {
			} else {
				content = content.substring(content.indexOf(pre_word) + 1, content.length());
			}
        	if (post_word == null || post_word == " ") {
			} else {
				content = content.substring(0, content.indexOf(post_word));
			}
        	
    		System.out.println(" *** News Title : " + title);
            System.out.println(" ");
            System.out.println(" *** News content : " + content);
            System.out.println(" ");
            System.out.println(" ");            

        } catch (Exception e) {
           e.printStackTrace();
        }

        System.out.println("====================================================== End  News Parsing " + getCurrentData() +" =========================================");
		return null;
	}
}
