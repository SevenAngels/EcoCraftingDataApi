package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Output implements Comparable<Output> {
    private Item item;
    private int quantity;
    private boolean reducible;


    @Override
    public int compareTo(Output o) {
        return item.getName().compareTo(o.getItem().getName());
    }
}
