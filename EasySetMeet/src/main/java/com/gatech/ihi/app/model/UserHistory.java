package com.gatech.ihi.app.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

import java.util.List;

@Data
@DynamoDBTable(tableName = "UserHistory")
public class UserHistory {
    @DynamoDBHashKey(attributeName = "UserId")
    private String userId;
    @DynamoDBAttribute(attributeName = "Username")
    private String username;
    @DynamoDBAttribute(attributeName = "History")
    private List<UserHistoryRecord> history;
}
