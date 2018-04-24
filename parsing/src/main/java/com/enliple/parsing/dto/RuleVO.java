package com.enliple.parsing.dto;

public class RuleVO {

	private String root_domain;
	private String check_SSL;
	private String select_tag1;
	private String select_tag2;
	private String select_tag3;
	private String media_name;
	private String pre_word;
	private String post_word;
	private String write_date;
	private String mod_date;
	private String pltfom_tp_code;
	
	
	
	public String getRoot_domain() {
		return root_domain;
	}
	public void setRoot_domain(String root_domain) {
		this.root_domain = root_domain;
	}
	public String getCheck_SSL() {
		return check_SSL;
	}
	public void setCheck_SSL(String check_SSL) {
		this.check_SSL = check_SSL;
	}
	public String getSelect_tag1() {
		return select_tag1;
	}
	public void setSelect_tag1(String select_tag1) {
		this.select_tag1 = select_tag1;
	}
	public String getSelect_tag2() {
		return select_tag2;
	}
	public void setSelect_tag2(String select_tag2) {
		this.select_tag2 = select_tag2;
	}
	public String getSelect_tag3() {
		return select_tag3;
	}
	public void setSelect_tag3(String select_tag3) {
		this.select_tag3 = select_tag3;
	}
	public String getMedia_name() {
		return media_name;
	}
	public void setMedia_name(String media_name) {
		this.media_name = media_name;
	}
	public String getPre_word() {
		return pre_word;
	}
	public void setPre_word(String pre_word) {
		this.pre_word = pre_word;
	}
	public String getPost_word() {
		return post_word;
	}
	public void setPost_word(String post_word) {
		this.post_word = post_word;
	}
	public String getWrite_date() {
		return write_date;
	}
	public void setWrite_date(String write_date) {
		this.write_date = write_date;
	}
	public String getMod_date() {
		return mod_date;
	}
	public void setMod_date(String mod_date) {
		this.mod_date = mod_date;
	}
	public String getPltfom_tp_code() {
		return pltfom_tp_code;
	}
	public void setPltfom_tp_code(String pltfom_tp_code) {
		this.pltfom_tp_code = pltfom_tp_code;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RuleVO [root_domain=" + root_domain + ", check_SSL=" + check_SSL + ", select_tag1=" + select_tag1
				+ ", select_tag2=" + select_tag2 + ", select_tag3=" + select_tag3 + ", media_name=" + media_name
				+ ", pre_word=" + pre_word + ", post_word=" + post_word + ", write_date=" + write_date + ", mod_date="
				+ mod_date + ", pltfom_tp_code=" + pltfom_tp_code + "]";
	}
	
	
}
