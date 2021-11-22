package com.gatech.ihi.app.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.gatech.ihi.app.model.converter.InstantConverter;
import lombok.Data;

import java.time.Instant;

@Data
@DynamoDBDocument
public class UserHistoryRecord {
    @DynamoDBTypeConverted(converter = InstantConverter.class)
    private Instant createdAt;
    @DynamoDBTypeConverted(converter = InstantConverter.class)
    private Instant updatedAt;
    private UserHistoryAppointmentRecord appointmentRecord;
}
