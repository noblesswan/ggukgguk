package com.ggukgguk.api.record.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaFile {
	
	private String mediaFileId;
	private String mediaTypeId;
	private boolean mediaFileBlocked;
	private boolean mediaFileChecked;
}
