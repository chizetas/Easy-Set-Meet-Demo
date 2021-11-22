package com.gatech.ihi.app.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Procedure {
    private final String procedureId;
    private final String condition;
    private final String code;
    private final String codeSystem;
    private final String codeDisplay;
}
