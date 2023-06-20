package com.ggukgguk.batch.checkContent.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileBlockedHistory {
    private int mediaFileBlockedHistoryId;
    private String mediaFileId;
    private String mediaFileDetectedLabel;
    private float mediaFileDetectedWeights;
    private Date mediaFileCheckedAt;
}
