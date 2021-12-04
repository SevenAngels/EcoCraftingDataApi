package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CraftingTable implements Comparable<CraftingTable> {
    private String name;
    private String nameID;
    private String upgradeModuleType;
    private boolean hidden;


    @Override
    public int compareTo(CraftingTable o) {
        return name.compareTo(o.getName());
    }
}
