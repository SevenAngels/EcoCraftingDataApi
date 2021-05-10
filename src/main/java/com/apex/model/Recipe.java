package com.apex.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Recipe {
    private String name;
    private String nameID;
    private Skill skill;
    private int level;
    private int labor;
    private CraftingTable craftingTable;
    private List<Ingredient> ingredients;
    private List<Output> outputs;
}
