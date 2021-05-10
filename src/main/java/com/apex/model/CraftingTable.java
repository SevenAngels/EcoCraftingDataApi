package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CraftingTable {
    private String name;
    private String nameID;
    private String upgradeModuleType;
}
