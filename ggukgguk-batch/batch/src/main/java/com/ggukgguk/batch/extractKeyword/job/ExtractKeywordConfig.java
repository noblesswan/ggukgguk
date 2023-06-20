package com.ggukgguk.batch.extractKeyword.job;

import com.ggukgguk.batch.extractKeyword.service.ColorService;
import com.ggukgguk.batch.extractKeyword.service.ComprehendService;
import com.ggukgguk.batch.extractKeyword.service.GgukggukNlpService;
import com.ggukgguk.batch.extractKeyword.service.GgukggukNlpServiceImpl;
import com.ggukgguk.batch.extractKeyword.vo.*;
import com.ggukgguk.batch.extractKeyword.writer.ItemListWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExtractKeywordConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final GgukggukNlpService ggukggukNlpService;
    private final ColorService colorService;

    private static final int chunkSize = 10;

    @Bean
    public Job extractKeywordJob() {
        return jobBuilderFactory.get("extractKeywordJob")
                .start(getKeywordsStep())
                .next(countKeywordsStep())
                .next(setDiaryMainKeywordStep())
                .next(setDiaryColorStep())
                .build();
    }

    @Bean
    public Step getKeywordsStep() {
        return stepBuilderFactory.get("getKeywordsStep")
                .<Record, List<RecordKeyword>> chunk(chunkSize)
                .reader(recordReader(0, 0, null))
                .processor(recordProcessor())
                .writer(recordWriter())
                .build();
    }

    @Bean
    public Step countKeywordsStep() {
        return stepBuilderFactory.get("countKeywordsStep")
                .<RecordKeywordExtended, RecordKeywordExtended> chunk(chunkSize)
                .reader(recordKeywordReader(0, 0, null))
                .writer(recordKeywordWriter())
                .build();
    }

    @Bean
    public Step setDiaryMainKeywordStep() {
        return stepBuilderFactory.get("setDiaryMainKeywordStep")
                .<DiaryKeywordAndColor, DiaryKeywordAndColor> chunk(chunkSize)
                .reader(diaryKeywordReader(0, 0, null))
                .writer(diaryKeywordWriter())
                .build();
    }

    @Bean
    public Step setDiaryColorStep() {
        return stepBuilderFactory.get("setDiaryColorStep")
                .<Diary, List<DiaryKeywordAndColor>> chunk(chunkSize)
                .reader(diaryReader(0, 0, null))
                .processor(fetchDiaryColorProcessor())
                .writer(diaryWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Record> recordReader(
            @Value("#{jobParameters[year]}") int year,
            @Value("#{jobParameters[month]}") int month,
            @Value("#{jobParameters[memberId]}") String memberId) {
        Calendar calendar = new GregorianCalendar(year, month - 1, 1);
        Date startDate = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT *\n");
        sb.append("FROM record\n");
        sb.append("WHERE record_created_at\n");
        sb.append("BETWEEN '" + format.format(startDate) + " 00:00'\n");
        sb.append("AND DATE_ADD('" + format.format(startDate) + " 00:00', INTERVAL +1 month)\n");

        if (memberId != null && !"".equals(memberId)) {
            sb.append("AND member_id = '" + memberId + "'");
        }

        return new JdbcCursorItemReaderBuilder<Record>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Record.class))
                .sql(sb.toString()) // Job 파라미터에서 받아오도록 수정하기
                .name("recordReader")
                .build();
    }

    @Bean
    public ItemProcessor<Record, List<RecordKeyword>> recordProcessor() {
        return record -> {
//            List<RecordKeyword> result = comprehendService.extractKeywordFromRecord(record);
            List<RecordKeyword> result = ggukggukNlpService.extractKeywordFromRecord(record);
            return result;
        };
    }

    @Bean
    public ItemWriter<List<RecordKeyword>> recordWriter() {
        return new ItemListWriter<RecordKeyword>(new JdbcBatchItemWriterBuilder<RecordKeyword>()
                .dataSource(dataSource)
                .sql("INSERT IGNORE INTO record_keyword VALUES (NULL, ?, ?)")
                .beanMapped()
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setInt(1, item.getRecordId());
                    ps.setString(2, item.getRecordKeyword());
                })
                .assertUpdates(false)
                .build());
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<RecordKeywordExtended> recordKeywordReader(
            @Value("#{jobParameters[year]}") int year,
            @Value("#{jobParameters[month]}") int month,
            @Value("#{jobParameters[memberId]}") String memberId) {
        Calendar calendar = new GregorianCalendar(year, month - 1, 1);
        Date startDate = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT YEAR(r.record_created_at) AS 'diary_year', MONTH(r.record_created_at) AS 'diary_month', r.member_id, rk.record_keyword FROM record_keyword rk\n");
        sb.append("LEFT JOIN record r \n");
        sb.append("ON r.record_id = rk.record_id\n");
        sb.append("WHERE r.record_created_at BETWEEN '" + format.format(startDate) + " 00:00'\n");
        sb.append("AND DATE_ADD('" + format.format(startDate) + " 00:00', INTERVAL +1 month)\n");

        if (memberId != null && !"".equals(memberId)) {
            sb.append("AND member_id = '" + memberId + "'");
        }

        return new JdbcCursorItemReaderBuilder<RecordKeywordExtended>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(RecordKeywordExtended.class))
                .sql(sb.toString()) // Job 파라미터에서 받아오도록 수정하기
                .name("recordKeywordReader")
                .build();
    }

    public JdbcBatchItemWriter<RecordKeywordExtended> insertDiaryEntity() {
        return new JdbcBatchItemWriterBuilder<RecordKeywordExtended>()
                .dataSource(dataSource)
                .sql("INSERT IGNORE INTO diary VALUES (NULL, ?, ?, ?, '', '')")
                .beanMapped()
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setString(1, item.getMemberId());
                    ps.setString(2, item.getDiaryYear());
                    ps.setString(3, item.getDiaryMonth());
                })
                .assertUpdates(false) // INSERT IGNORE이므로 affectedRows가 0일 수 있음
                .build();
    }

    public JdbcBatchItemWriter<RecordKeywordExtended> insertOrUpdateKeywordFreq() {
        return new JdbcBatchItemWriterBuilder<RecordKeywordExtended>()
                .dataSource(dataSource)
                .sql("INSERT INTO diary_keyword\n" +
                        "VALUES (" +
                        "    NULL," +
                        "    (SELECT diary_id FROM diary WHERE member_id = ? AND diary_year = ? AND diary_month = ?)," +
                        "    ?," +
                        "    1" +
                        ")" +
                        "ON DUPLICATE KEY\n" +
                        "UPDATE diary_freq = diary_freq + 1;")
                .beanMapped()
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setString(1, item.getMemberId());
                    ps.setString(2, item.getDiaryYear());
                    ps.setString(3, item.getDiaryMonth());
                    ps.setString(4, item.getRecordKeyword());
                })
                .build();
    }

    @Bean
    public CompositeItemWriter<RecordKeywordExtended> recordKeywordWriter() {
        return new CompositeItemWriterBuilder<RecordKeywordExtended>()
                .delegates(Arrays.asList(insertDiaryEntity(), insertOrUpdateKeywordFreq()))
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<DiaryKeywordAndColor> diaryKeywordReader(
            @Value("#{jobParameters[year]}") int year,
            @Value("#{jobParameters[month]}") int month,
            @Value("#{jobParameters[memberId]}") String memberId) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT dk.diary_id, dk.diary_keyword, dk.diary_freq\n"); // freq가 가장 높은 행을 가져오는 쿼리
        sb.append("FROM (\n");
        sb.append("\tSELECT diary_id, diary_keyword, diary_freq\n");
        sb.append("\tFROM diary_keyword d\n");
        sb.append("\tWHERE diary_freq = (\n");
        sb.append("\t  SELECT MAX(diary_freq)\n");
        sb.append("\t  FROM diary_keyword\n");
        sb.append("\t  WHERE diary_id = d.diary_id\n");
        sb.append("\t)) dk\n");
        sb.append("LEFT JOIN diary d \n");
        sb.append("ON dk.diary_id = d.diary_id\n");
        sb.append("WHERE d.diary_year = " + year + " AND d.diary_month = " + month + " ");


        if (memberId != null && !"".equals(memberId)) {
            sb.append("AND member_id = '" + memberId + "'");
        }

        return new JdbcCursorItemReaderBuilder<DiaryKeywordAndColor>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(DiaryKeywordAndColor.class))
                .sql(sb.toString())
                .name("diaryKeywordReader")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<DiaryKeywordAndColor> diaryKeywordWriter() {
        return new JdbcBatchItemWriterBuilder<DiaryKeywordAndColor>()
                .dataSource(dataSource)
                .sql("UPDATE diary SET main_keyword = ? WHERE diary_id = ?;")
                .beanMapped()
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setString(1, item.getDiaryKeyword());
                    ps.setInt(2, item.getDiaryId());
                })
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Diary> diaryReader(
            @Value("#{jobParameters[year]}") int year,
            @Value("#{jobParameters[month]}") int month,
            @Value("#{jobParameters[memberId]}") String memberId) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT diary_id, member_id, diary_year, diary_month, main_color, main_keyword\n");
        sb.append("FROM diary d\n");
        sb.append("WHERE diary_year = " + year + " AND diary_month = " + month + " ");
        if (memberId != null && !"".equals(memberId)) {
            sb.append("AND member_id = '" + memberId + "'");
        }

        return new JdbcCursorItemReaderBuilder<Diary>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Diary.class))
                .sql(sb.toString())
                .name("diaryReader")
                .build();
    }

    @Bean
    public ItemProcessor<Diary, List<DiaryKeywordAndColor>> fetchDiaryColorProcessor() {
        return diary -> {
            List<String> colorList = colorService.getColor(diary.getMainKeyword());

            List<DiaryKeywordAndColor> result = new ArrayList<>();
            for (String color : colorList) {
                DiaryKeywordAndColor item = new DiaryKeywordAndColor();
                item.setDiaryId(diary.getDiaryId());
                item.setDiaryColor(color);
                item.setMemberId(diary.getMemberId());
                result.add(item);
            }

            return result;
        };
    }

    @Bean
    public ItemWriter<List<DiaryKeywordAndColor>> setDiaryColors() {
        return new ItemListWriter<DiaryKeywordAndColor>(new JdbcBatchItemWriterBuilder<DiaryKeywordAndColor>()
                .dataSource(dataSource)
                .sql("INSERT INTO diary_color VALUES (NULL, ?, ?)")
                .beanMapped()
                .itemPreparedStatementSetter((item, ps) -> {
                    ps.setInt(1, item.getDiaryId());
                    ps.setString(2, item.getDiaryColor());
                })
                .build());
    }

    @Bean
    public JdbcBatchItemWriter<List<DiaryKeywordAndColor>> setDiaryMainColor() {
        return new JdbcBatchItemWriterBuilder<List<DiaryKeywordAndColor>>()
                .dataSource(dataSource)
                .sql("UPDATE diary SET main_color = ? WHERE diary_id = ?;")
                .beanMapped()
                .itemPreparedStatementSetter((list, ps) -> {
                    DiaryKeywordAndColor item = list.get(0);
                    ps.setString(1, item.getDiaryColor());
                    ps.setInt(2, item.getDiaryId());
                })
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<List<DiaryKeywordAndColor>> addNotification(
            @Value("#{jobParameters[year]}") int year,
            @Value("#{jobParameters[month]}") int month
    ) {
        return new JdbcBatchItemWriterBuilder<List<DiaryKeywordAndColor>>()
                .dataSource(dataSource)
                .sql("INSERT INTO notification " +
                        "(receiver_id, notification_type_id, reference_id, notification_message) " +
                        "VALUES (?, 'MONTHLY_ANALYSIS', ?, ?);")
                .beanMapped()
                .itemPreparedStatementSetter((list, ps) -> {
                    DiaryKeywordAndColor item = list.get(0);
                    ps.setString(1, item.getMemberId());
                    ps.setInt(2, item.getDiaryId());
                    ps.setString(3, year + "년 " + month + "월 월말정산이 완료되었습니다. 지금 바로 확인해보세요.");
                })
                .build();
    }

    @Bean
    public CompositeItemWriter<List<DiaryKeywordAndColor>> diaryWriter() {
        return new CompositeItemWriterBuilder<List<DiaryKeywordAndColor>>()
                .delegates(Arrays.asList(setDiaryColors(), setDiaryMainColor(), addNotification(0, 0)))
                .build();
    }
}
