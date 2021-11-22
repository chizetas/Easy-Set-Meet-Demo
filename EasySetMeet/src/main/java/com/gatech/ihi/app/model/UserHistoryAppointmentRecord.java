package com.gatech.ihi.app.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.gatech.ihi.app.model.converter.InstantConverter;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@DynamoDBDocument
public class UserHistoryAppointmentRecord {
    private String appointmentId;
    @DynamoDBTypeConverted(converter = InstantConverter.class)
    private Instant startTime;
    @DynamoDBTypeConverted(converter = InstantConverter.class)
    private Instant endTime;
    private List<String> participants;
}
