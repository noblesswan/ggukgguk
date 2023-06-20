package com.ggukgguk.batch.extractKeyword.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ColorService {

    public List<String> getColor(String keyword) throws JsonProcessingException;
}
