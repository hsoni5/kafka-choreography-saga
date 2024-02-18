package com.soni.payment.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface JsonMapperUtil {
    static <D, T> D readValue(final String event, Class<D> outClass) {
        try {
            return new ObjectMapper().readValue(event, outClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
