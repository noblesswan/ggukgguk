package com.ggukgguk.batch.checkContent.service;

import com.ggukgguk.batch.checkContent.vo.MediaFileBlockedHistory;

import java.util.List;

public interface RekognizeService {
    public List<MediaFileBlockedHistory> detectModLabel(String mediaFileId, String sourceImage);
}
