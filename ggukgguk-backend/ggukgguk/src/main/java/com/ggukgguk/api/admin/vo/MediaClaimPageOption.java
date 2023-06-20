package com.ggukgguk.api.admin.vo;

import lombok.Data;

@Data
public class MediaClaimPageOption {

	private int page;
	private int size;
	private int skip;
	private String mediaFileRecheckRequestStatus;
	private String mediaFileId;
	
	public MediaClaimPageOption() {
		this(1,  10);
	}
	
	public MediaClaimPageOption(int page, int size) {
		this.page = page;
		this.size = size;
		computeSkip();
	}
	
	public MediaClaimPageOption(int page, int size, String mediaFileRecheckRequestStatus) {
		this(page, size);
		this.mediaFileRecheckRequestStatus = mediaFileRecheckRequestStatus;
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