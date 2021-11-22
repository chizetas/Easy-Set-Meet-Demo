package com.gatech.ihi.app.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Patient {
    private final String patientId;
    private final String name;
}
