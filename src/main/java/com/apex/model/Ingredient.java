package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ingredient {
    private Item item;
    private int quantity;
    private boolean reducible;
}
