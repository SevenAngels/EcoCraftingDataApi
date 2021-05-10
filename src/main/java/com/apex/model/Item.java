package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private String name;
    private String nameID;
    private boolean tag;
}
