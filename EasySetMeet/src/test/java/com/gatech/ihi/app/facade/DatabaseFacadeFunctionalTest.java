package com.gatech.ihi.app.facade;

import com.gatech.ihi.app.guice.LocalGuiceModule;
import com.gatech.ihi.app.model.UserHistory;
import com.gatech.ihi.app.model.UserHistoryAppointmentRecord;
import com.gatech.ihi.app.model.UserHistoryRecord;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseFacadeFunctionalTest {
    private static Injector injector;
    private DatabaseFacade databaseFacade;

    @BeforeAll
    public static void init() {
        injector = Guice.createInjector(Stage.PRODUCTION, new LocalGuiceModule());
    }

    @BeforeEach
    public void setup() {
        databaseFacade = injector.getInstance(DatabaseFacade.class);
    }

    @Test
    public void testUserHistoryFlow() {
        final String userId = "userId";
        final String username = "username";

        // Create an empty history
        databaseFacade.createUserHistory(userId, username);

        // Get that history
        UserHistory userHistory = databaseFacade.getUserHistory(userId);
        assertThat(userHistory.getUserId()).isEqualTo(userId);
        assertThat(userHistory.getUsername()).isEqualTo(username);
        assertThat(userHistory.getHistory()).isEmpty();

        // Update the history
        final UserHistoryAppointmentRecord appointmentRecord = new UserHistoryAppointmentRecord();
        appointmentRecord.setAppointmentId("appointmentId");
        appointmentRecord.setStartTime(Instant.now());
        appointmentRecord.setEndTime(Instant.now());
        appointmentRecord.setParticipants(Collections.emptyList());

        final UserHistoryRecord userHistoryRecord = new UserHistoryRecord();
        userHistoryRecord.setCreatedAt(Instant.now());
        userHistoryRecord.setUpdatedAt(Instant.now());
        userHistoryRecord.setAppointmentRecord(appointmentRecord);
        userHistory.getHistory().add(userHistoryRecord);
        databaseFacade.saveUserHistory(userHistory);

        // Get the history again
        userHistory = databaseFacade.getUserHistory(userId);
        assertThat(userHistory.getUserId()).isEqualTo(userId);
        assertThat(userHistory.getUsername()).isEqualTo(username);
        assertThat(userHistory.getHistory().size()).isEqualTo(1);

        // Remove the history
        databaseFacade.removeUserHistory(userId);
    }
}