package com.gatech.ihi.app.model;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Builder
@Value
public class Appointment {
    private final String appointmentId;
    private final String procedureId;
    private final String patientId;
    private final String practitionerId;
    private final Date startTime;
    private final Date endTime;
}
