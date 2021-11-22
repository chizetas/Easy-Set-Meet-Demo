package com.gatech.ihi.app.facade;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.gatech.ihi.app.model.UserHistory;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class DatabaseFacade {
    private final DynamoDBMapper client;

    public void createUserHistory(final String userId, final String username) {
        final UserHistory userHistory = new UserHistory();
        userHistory.setUserId(userId);
        userHistory.setUsername(username);
        userHistory.setHistory(Collections.emptyList());

        client.save(userHistory);
    }

    public UserHistory getUserHistory(final String userId) {
        final UserHistory userHistory = new UserHistory();
        userHistory.setUserId(userId);

        final DynamoDBQueryExpression<UserHistory> queryExpression =
                new DynamoDBQueryExpression<UserHistory>().withHashKeyValues(userHistory);

        final List<UserHistory> userHistoryList = client.query(UserHistory.class, queryExpression);

        if (userHistoryList.isEmpty()) {
            throw new IllegalArgumentException(String.format("Missing UserHistory from the table, userId=%s", userId));
        }

        return userHistoryList.get(0);
    }

    public void saveUserHistory(final UserHistory userHistory) {
        client.save(userHistory);
    }

    public void removeUserHistory(final String userId) {
        final UserHistory userHistory = new UserHistory();
        userHistory.setUserId(userId);

        client.delete(userHistory);
    }
}
