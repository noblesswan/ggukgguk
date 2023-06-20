package com.ggukgguk.batch.extractKeyword.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@StepScope
@Slf4j
public class ItemListWriter<T> implements ItemWriter<List<T>> {
    private final ItemWriter<T> nestedWriter;

    public ItemListWriter(ItemWriter<T> nestedWriter) {
        this.nestedWriter = nestedWriter;
    }

    @Override
    public void write(List<? extends List<T>> items) throws Exception {
        for (List<T> subList : items) {
            if (subList.size() > 0){
                log.info(subList.get(0) + "외 " + (subList.size() - 1) + "건 INSERT 시작");
                nestedWriter.write(subList);
            }
        }
    }
}