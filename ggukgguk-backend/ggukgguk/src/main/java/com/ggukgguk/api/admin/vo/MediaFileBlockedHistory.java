package com.ggukgguk.api.admin.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileBlockedHistory {
    private int mediaFileBlockedHistoryId;
    private String mediaFileId;
    private String mediaFileDetectedLabel;
    private float mediaFileDetectedWeights;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Seoul")
    private Date mediaFileCheckedAt;
}
