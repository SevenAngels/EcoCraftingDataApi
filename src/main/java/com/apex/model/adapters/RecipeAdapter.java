package com.apex.model.adapters;

import com.apex.model.Ingredient;
import com.apex.model.Output;
import com.apex.model.Recipe;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class RecipeAdapter extends TypeAdapter<Recipe> {

    @Override
    public void write(JsonWriter out, Recipe recipe) throws IOException {
        out.beginObject();

        out.name("name").value(recipe.getName());
        out.name("nameID").value(recipe.getNameID());
        out.name("skill").value("getSkillByNameID('" + recipe.getSkill().getNameID() + "')");
        out.name("level").value(recipe.getLevel());
        out.name("labor").value(recipe.getLabor());
        out.name("craftingTable").value("getCraftingTableByNameID('" + recipe.getCraftingTable().getNameID() + "')");
        out.name("hidden").value(recipe.isHidden());

        //Print ingredients
        out.name("ingredients");
        out.beginArray();
        for (Ingredient ing : recipe.getIngredients()) {
            out.beginObject();
            out.name("item").value("getItemByNameID('" + ing.getItem().getNameID() + "')");
            out.name("quantity").value(ing.getQuantity());
            out.name("reducible").value(ing.isReducible());
            out.endObject();
        }
        out.endArray();

        //Print outputs
        out.name("outputs");
        out.beginArray();
        for (Output output : recipe.getOutputs()) {
            out.beginObject();
            out.name("item").value("getItemByNameID('" + output.getItem().getNameID() + "')");
            out.name("quantity").value(output.getQuantity());
            out.name("reducible").value(output.isReducible());
            out.name("primary").value(output.isPrimary());
            out.endObject();
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public Recipe read(JsonReader in) throws IOException {
        return null;
    }
}
