package com.apex.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpgradeModule implements Comparable<UpgradeModule> {
    private String name;
    private String nameID;
    private String typeNameID;
    private BigDecimal modifier;

    @Override
    public int compareTo(UpgradeModule o) {
        return name.compareTo(o.getName());
    }
}
