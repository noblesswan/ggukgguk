package com.ggukgguk.api.admin.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {
	private String mediaFileId;
	private String mediaTypeId;
	private boolean mediaFileBlocked;
	private boolean mediaFileChecked;
	private List<MediaFileBlockedHistory> mediaFileBlockedHistory;
}
