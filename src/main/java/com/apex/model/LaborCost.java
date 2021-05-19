package com.apex.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LaborCost {
    private int level;
    private BigDecimal modifier;
}
