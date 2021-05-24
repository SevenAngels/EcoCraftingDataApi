package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ingredient implements Comparable<Ingredient> {
    private Item item;
    private int quantity;
    private boolean reducible;


    @Override
    public int compareTo(Ingredient o) {
        return item.getName().compareTo(o.item.getName());
    }
}
