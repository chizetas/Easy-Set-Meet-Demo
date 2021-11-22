package com.gatech.ihi.app.model.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.Instant;

public class InstantConverter implements DynamoDBTypeConverter<String, Instant> {
    @Override
    public String convert(Instant instant) {
        return Long.toString(instant.toEpochMilli());
    }

    @Override
    public Instant unconvert(String str) {
        return Instant.ofEpochMilli(Long.parseLong(str));
    }
}
