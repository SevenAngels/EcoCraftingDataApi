package com.apex;

import com.apex.model.EcoDataResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

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
    }
}
