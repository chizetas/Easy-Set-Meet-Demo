package com.gatech.ihi.app.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AppointmentWithoutId {
    private final String procedureId;
    private final String patientId;
    private final String practitionerId;
    private final String timeSlotId;
}
