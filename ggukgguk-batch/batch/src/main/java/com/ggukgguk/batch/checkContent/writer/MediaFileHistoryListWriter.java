package com.ggukgguk.batch.checkContent.writer;

import com.ggukgguk.batch.checkContent.vo.MediaFile;
import com.ggukgguk.batch.checkContent.vo.MediaFileBlockedHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@StepScope
@Slf4j
public class MediaFileHistoryListWriter implements ItemWriter<MediaFile> {
    private final ItemWriter<MediaFileBlockedHistory> nestedWriter;

    public MediaFileHistoryListWriter(ItemWriter<MediaFileBlockedHistory> nestedWriter) {
        this.nestedWriter = nestedWriter;
    }

    @Override
    public void write(List<? extends MediaFile> items) throws Exception {
        for (MediaFile media : items) {
            List<MediaFileBlockedHistory> subList = media.getHistoryList();
            if (media.isMediaFileBlocked() && subList != null && subList.size() > 0){
                log.info(subList.get(0) + "외 " + (subList.size() - 1) + "건 INSERT 시작");
                nestedWriter.write(subList);
            }
        }
    }
}