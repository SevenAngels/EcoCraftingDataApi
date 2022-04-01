package com.apex;

import com.apex.model.EcoDataResponse;
import com.apex.model.Recipe;
import com.apex.model.adapters.RecipeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.apex.db.DbService.*;
import static org.assertj.core.api.Assertions.assertThat;

class RequestTest {

    @Test
    void testGetEcoData() throws Exception {

        EcoDataResponse response = EcoDataResponse.builder()
                .craftingTables(getAllCraftingTables())
                .upgradeModules(getAllUpgradeModules())
                .skills(getAllSkills())
                .items(getAllItems())
                .ingredients(getAllIngredients())
                .outputs(getAllOutputs())
                .recipes(getAllRecipes())
                .laborCosts(getAllLaborCosts())
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getCraftingTables()).isNotEmpty();
        assertThat(response.getIngredients()).isNotEmpty();
        assertThat(response.getItems()).isNotEmpty();
        assertThat(response.getOutputs()).isNotEmpty();
        assertThat(response.getRecipes()).isNotEmpty();
        assertThat(response.getSkills()).isNotEmpty();
        assertThat(response.getUpgradeModules()).isNotEmpty();
        assertThat(response.getLaborCosts()).isNotEmpty();

        String json = new Gson().toJson(response);

        assertThat(json).startsWith("{");

        //Check default Gson object serialization to ensure no function pattern
        Pattern functionPattern = Pattern.compile(".*get[A-z]+ByNameID.*");
        assertThat(json).doesNotMatch(functionPattern);

        //Check custom recipe adapter Gson serialization to ensure function pattern is present
        Gson gson = new GsonBuilder().registerTypeAdapter(Recipe.class, new RecipeAdapter()).create();
        String customJson = gson.toJson(response);
        assertThat(customJson).matches(functionPattern);
    }
}
