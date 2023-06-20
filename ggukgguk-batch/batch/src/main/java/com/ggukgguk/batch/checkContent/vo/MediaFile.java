package com.ggukgguk.batch.checkContent.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {
    private String mediaFileId;
    private String mediaTypeId;
    private boolean mediaFileBlocked;
    private boolean mediaFileChecked;
    private List<MediaFileBlockedHistory> historyList;
}