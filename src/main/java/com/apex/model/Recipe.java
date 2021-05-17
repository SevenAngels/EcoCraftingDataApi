package com.apex.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Recipe implements Comparable<Recipe> {
    private String name;
    private String nameID;
    private Skill skill;
    private int level;
    private int labor;
    private CraftingTable craftingTable;
    private List<Ingredient> ingredients;
    private List<Output> outputs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipe recipe = (Recipe) o;

        if (level != recipe.level) return false;
        if (labor != recipe.labor) return false;
        if (!name.equals(recipe.name)) return false;
        if (!nameID.equals(recipe.nameID)) return false;
        if (!skill.equals(recipe.skill)) return false;
        if (!craftingTable.equals(recipe.craftingTable)) return false;
        if (!ingredients.equals(recipe.ingredients)) return false;
        return outputs.equals(recipe.outputs);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + nameID.hashCode();
        result = 31 * result + skill.hashCode();
        result = 31 * result + level;
        result = 31 * result + labor;
        result = 31 * result + craftingTable.hashCode();
        result = 31 * result + ingredients.hashCode();
        result = 31 * result + outputs.hashCode();
        return result;
    }


    @Override
    public int compareTo(Recipe r) {
        int outputNameCompare = outputs.get(0).getItem().getName().compareTo(r.getOutputs().get(0).getItem().getName());
        if (outputNameCompare == 0) {
            return name.compareTo(r.getName());
        } else {
            return outputNameCompare;
        }
    }
}
