package com.ggukgguk.api.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchCondition {

    private Integer page = 1;
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    private String  option = "";
    private String  keyword = "";

    public static final int MIN_PAGE_SIZE = 5;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 50;

    public SearchCondition(Integer page, Integer pageSize) {
        this(page, pageSize, "", "");
    }
}
