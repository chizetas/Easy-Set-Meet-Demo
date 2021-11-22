package com.gatech.ihi.app.model;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Builder
@Value
public class TimeSlot {
    private final String timeSlotId;
    private final Date startTime;
    private final Date endTime;
}
