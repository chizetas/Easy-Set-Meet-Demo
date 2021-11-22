package com.gatech.ihi.app.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Practitioner {
    private final String practitionerId;
    private final String name;
}
