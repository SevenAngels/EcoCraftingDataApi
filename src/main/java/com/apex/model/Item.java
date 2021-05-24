package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item implements Comparable<Item> {
    private String name;
    private String nameID;
    private boolean tag;

    @Override
    public int compareTo(Item o) {
        return name.compareTo(o.getName());
    }
}
