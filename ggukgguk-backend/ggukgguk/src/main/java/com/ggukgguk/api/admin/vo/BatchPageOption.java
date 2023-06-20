package com.ggukgguk.api.admin.vo;

import lombok.Data;

@Data
public class BatchPageOption {
	String jobName;
	int page;
	int size;
	int skip; 
	
	public BatchPageOption() {
		this(1,  10);
	}
	
	public BatchPageOption(int page, int size) {
		this.page = page;
		this.size = size;
		computeSkip();
	}
	
	public BatchPageOption(String jobName) {
		this(1,  10);
		this.jobName = jobName;
	}
	
	public BatchPageOption(String jobName, int page, int size) {
		this(page, size);
		this.jobName = jobName;
	}
	
	public void setPage(int page) {
		this.page = page;
		computeSkip();
	}
	
	public void setSize(int size) {
		this.size = size;
		computeSkip();
	}
	
	private void computeSkip() {
		this.skip = (page - 1) * size;
	}
}
