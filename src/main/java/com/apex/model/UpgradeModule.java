package com.apex.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpgradeModule {
    private String name;
    private String nameID;
    private BigDecimal modifier;
}
